package com.nowiwr01p.auth.ui.auth

import com.nowiwr01p.auth.ui.auth.data.AuthType
import com.nowiwr01p.auth.ui.auth.data.AuthType.*
import com.nowiwr01p.core_ui.ui.ButtonState
import com.nowiwr01p.core_ui.ui.ButtonState.*
import com.nowiwr01p.core_ui.view_model.ViewEvent
import com.nowiwr01p.core_ui.view_model.ViewSideEffect
import com.nowiwr01p.core_ui.view_model.ViewState
import com.nowiwr01p.domain.auth.data.error.AuthError
import com.nowiwr01p.domain.auth.data.error.AuthTextFieldType

interface AuthContract {

    sealed interface Event: ViewEvent {
        object Init: Event
        object OnAuthClick: Event
        object ToggleAuthMode: Event
        object NavigateToVerification: Event
        object NavigateToChooseCountry: Event
        object TogglePasswordVisibility: Event
        data class OnValueChanged(val type: AuthTextFieldType, val value: String): Event
    }

    data class State(
        val authType: AuthType = SIGN_IN,
        val authButtonState: ButtonState = DEFAULT,
        val showKeyboard: Boolean = false,
        val hidePassword: Boolean = true,
        val isUserVerified: Boolean = false,
        val authSecurityWarningWasShown: Boolean = false,
        val authError: AuthError? = null,
        val email: String = "",
        val password: String = "",
        val passwordRepeat: String = ""
    ): ViewState

    sealed interface Effect: ViewSideEffect {
        object NavigateToVerification: Effect
        object NavigateToChooseCountry: Effect
        object ShowAuthSecurityWarning: Effect
        data class ShowError(val error: AuthError): Effect
    }

    interface Listener {
        fun authClick()
        fun toNextScreen()
        fun toggleAccountMode()
        fun togglePasswordVisibility()
        fun onValueChanged(type: AuthTextFieldType, value: String)
    }
}