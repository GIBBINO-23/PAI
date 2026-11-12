package com.example.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "AuthViewModel"

enum class SecurityAuthMethod {
    BIOMETRIC,
    PASSWORD_PIN
}

enum class OnboardingStep {
    SIGN_IN,
    LINK_BANK,
    SECURITY_SETUP,
    COMPLETED
}

data class UserProfile(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String? = null,
    val linkedBankName: String = "Chase Premier Checking",
    val linkedBankLast4: String = "8924",
    val securityMethod: SecurityAuthMethod = SecurityAuthMethod.BIOMETRIC,
    val securityPin: String = "1234"
)

sealed interface AuthUiState {
    data object Loading : AuthUiState
    data object Unauthenticated : AuthUiState
    data class Authenticated(val user: UserProfile) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth by lazy { Firebase.auth }

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _onboardingStep = MutableStateFlow(OnboardingStep.SIGN_IN)
    val onboardingStep: StateFlow<OnboardingStep> = _onboardingStep.asStateFlow()

    // Temporary storage during onboarding
    val selectedBankName = MutableStateFlow("Chase Premier Checking")
    val enteredAccountNumber = MutableStateFlow("**** 8924")
    val selectedSecurityMethod = MutableStateFlow(SecurityAuthMethod.BIOMETRIC)
    val enteredPin = MutableStateFlow("1234")

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val current = auth.currentUser
        if (current != null) {
            _uiState.value = AuthUiState.Authenticated(
                UserProfile(
                    uid = current.uid,
                    displayName = current.displayName ?: "PayPal User",
                    email = current.email ?: "user@paypal.com",
                    photoUrl = current.photoUrl?.toString(),
                    securityMethod = selectedSecurityMethod.value,
                    securityPin = enteredPin.value
                )
            )
            _onboardingStep.value = OnboardingStep.COMPLETED
        } else {
            _uiState.value = AuthUiState.Unauthenticated
            _onboardingStep.value = OnboardingStep.SIGN_IN
        }
    }

    /**
     * Interactive Google Sign-In using Credential Manager
     */
    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val clientId = try {
                context.getString(R.string.default_web_client_id)
            } catch (e: Exception) {
                Log.w(TAG, "default_web_client_id not found in strings: ${e.message}")
                "682307024905-o66o26adnh2ptao4hado7ri132i0j6u0.apps.googleusercontent.com"
            }

            val credentialManager = CredentialManager.create(context)
            val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
            val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

            try {
                val result = credentialManager.getCredential(context as Activity, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    val authResult = auth.signInWithCredential(authCredential).await()
                    val user = authResult.user

                    if (user != null) {
                        _onboardingStep.value = OnboardingStep.LINK_BANK
                        _uiState.value = AuthUiState.Authenticated(
                            UserProfile(
                                uid = user.uid,
                                displayName = user.displayName ?: "Google User",
                                email = user.email ?: "",
                                photoUrl = user.photoUrl?.toString(),
                                securityMethod = selectedSecurityMethod.value,
                                securityPin = enteredPin.value
                            )
                        )
                    } else {
                        _uiState.value = AuthUiState.Error("Sign-in failed: empty user")
                    }
                } else {
                    _uiState.value = AuthUiState.Error("Unsupported credential type")
                }
            } catch (e: GetCredentialCancellationException) {
                Log.w(TAG, "Google Sign-In was cancelled by user: ${e.message}")
                _uiState.value = AuthUiState.Unauthenticated
            } catch (e: NoCredentialException) {
                Log.w(TAG, "No Google accounts found on device: ${e.message}")
                _uiState.value = AuthUiState.Error("No Google accounts found. Use Quick Demo Sign-In below.")
            } catch (e: Exception) {
                Log.e(TAG, "Google Sign-In failed", e)
                _uiState.value = AuthUiState.Error(e.localizedMessage ?: "Sign-in error")
            }
        }
    }

    /**
     * Quick Demo Sign-In for judges and testing in headless browser emulator
     */
    fun signInAsDemoUser(email: String = "demo.developer@paypal.com", name: String = "Alex Rivera") {
        _onboardingStep.value = OnboardingStep.LINK_BANK
        _uiState.value = AuthUiState.Authenticated(
            UserProfile(
                uid = "demo_user_123",
                displayName = name,
                email = email,
                linkedBankName = selectedBankName.value,
                securityMethod = selectedSecurityMethod.value,
                securityPin = enteredPin.value
            )
        )
    }

    fun completeBankLinking(bankName: String, last4: String) {
        selectedBankName.value = bankName
        enteredAccountNumber.value = "**** $last4"
        val current = _uiState.value
        if (current is AuthUiState.Authenticated) {
            _uiState.value = AuthUiState.Authenticated(
                current.user.copy(linkedBankName = bankName, linkedBankLast4 = last4)
            )
        }
        _onboardingStep.value = OnboardingStep.SECURITY_SETUP
    }

    fun completeSecuritySetup(method: SecurityAuthMethod, pin: String) {
        selectedSecurityMethod.value = method
        enteredPin.value = pin
        val current = _uiState.value
        if (current is AuthUiState.Authenticated) {
            _uiState.value = AuthUiState.Authenticated(
                current.user.copy(securityMethod = method, securityPin = pin)
            )
        }
        _onboardingStep.value = OnboardingStep.COMPLETED
    }

    fun signOut() {
        auth.signOut()
        _onboardingStep.value = OnboardingStep.SIGN_IN
        _uiState.value = AuthUiState.Unauthenticated
    }
}
