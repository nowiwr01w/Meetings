@file:OptIn(ExperimentalComposeUiApi::class)

package com.nowiwr01p.auth.ui.auth

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.nowiwr01p.auth.BuildConfig
import com.nowiwr01p.auth.BuildConfig.PRIVACY_LINK
import com.nowiwr01p.auth.R
import com.nowiwr01p.auth.ui.auth.AuthContract.*
import com.nowiwr01p.auth.ui.auth.data.AuthType.SIGN_IN
import com.nowiwr01p.auth.ui.auth.data.AuthType.SIGN_UP
import com.nowiwr01p.core_ui.EffectObserver
import com.nowiwr01p.core_ui.extensions.appendLink
import com.nowiwr01p.core_ui.extensions.keyboardState
import com.nowiwr01p.core_ui.extensions.onTextClick
import com.nowiwr01p.core_ui.navigators.main.Navigator
import com.nowiwr01p.core_ui.theme.*
import com.nowiwr01p.core_ui.ui.bottom_sheet.BottomSheetParams
import com.nowiwr01p.core_ui.ui.button.StateButton
import com.nowiwr01p.domain.auth.main.data.error.AuthTextFieldType
import com.nowiwr01p.domain.auth.main.data.error.AuthTextFieldType.*
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AuthMainScreen(
    navigator: Navigator,
    viewModel: AuthViewModel = getViewModel { parametersOf(mainBackgroundColor) }
) {
    val state = viewModel.viewState.value

    val listener = object : Listener {
        override fun authClick() {
            viewModel.setEvent(Event.OnAuthClick)
        }
        override fun toggleAccountMode() {
            viewModel.setEvent(Event.ToggleAuthMode)
        }
        override fun togglePasswordVisibility() {
            viewModel.setEvent(Event.TogglePasswordVisibility)
        }
        override fun toNextScreen() {
            val event = when {
                state.isUserVerified && state.isUserSetCity -> Event.NavigateToMeetings
                state.isUserVerified -> Event.NavigateToChooseCountry
                else -> Event.NavigateToVerification
            }
            viewModel.setEvent(event)
        }
        override fun onValueChanged(type: AuthTextFieldType, value: String) {
            viewModel.setEvent(Event.OnValueChanged(type, value))
        }
        override fun openLink(link: String) {
            viewModel.setEvent(Event.OpenLink(link))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setEvent(Event.Init)
    }

    val bottomSheetParams = BottomSheetParams(
        topPadding = 160.dp,
        content =  { AuthSecurityWarningContent() }
    )

    EffectObserver(viewModel.effect) {
        when (it) {
            is Effect.NavigateToMeetings -> {
                navigator.navigateToMeetings()
            }
            is Effect.NavigateToVerification -> {
                navigator.authNavigator.toVerification()
            }
            is Effect.NavigateToChooseCountry -> {
                navigator.authNavigator.toChooseCity()
            }
            is Effect.ShowAuthSecurityWarning -> {
                viewModel.setEvent(Event.ShowBottomSheet(bottomSheetParams))
            }
        }
    }

    AuthMainScreenContent(state, listener)
}

@Composable
private fun AuthMainScreenContent(
    state: State,
    listener: Listener?
) = ConstraintLayout(
    modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.mainBackgroundColor)
) {
    val (icon, textFieldsContainer) = createRefs()

    val isKeyboardOpen by keyboardState()
    val authContentTransitionDp by animateDpAsState(
        targetValue = if (isKeyboardOpen) 8.dp else 160.dp
    )

    val iconModifier = Modifier
        .size(96.dp)
        .clip(CircleShape)
        .constrainAs(icon) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(parent.top, 32.dp)
        }
    Image(
        painter = painterResource(R.drawable.ic_login),
        contentDescription = "Auth icon",
        modifier = iconModifier
    )

    val authContentModifier = Modifier
        .fillMaxWidth()
        .padding(top = authContentTransitionDp)
        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        .background(Color.White)
        .constrainAs(textFieldsContainer) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            height = Dimension.fillToConstraints
        }
    AuthContent(
        state = state,
        listener = listener,
        modifier = authContentModifier
    )
}

@Composable
private fun AuthContent(
    state: State,
    listener: Listener?,
    modifier: Modifier
) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Title()
    TextFields(state, listener)
    AuthButton(state, listener)
    ToggleText(state, listener)
    Spacer(modifier = Modifier.weight(1f))
    TermsText(listener)
}

/**
 * AUTH TITLE
 */
@Composable
private fun Title() = Text(
    text = "Авторизация",
    color = MaterialTheme.colors.textPrimary,
    style = MaterialTheme.typography.h1,
    modifier = Modifier.padding(top = 32.dp)
)

/**
 * TEXT FIELDS
 */
@Composable
private fun TextFields(
    state: State,
    listener: Listener?
) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp, start = 24.dp, end = 24.dp)
) {
    val focusManager = LocalFocusManager.current
    CustomTextField(
        state = state,
        fieldType = EMAIL,
        text = state.email,
        hint = "Почта",
        focusManager = focusManager,
        listener = listener
    )
    CustomTextField(
        state = state,
        fieldType = PASSWORD,
        text = state.password,
        hint = "Пароль",
        focusManager = focusManager,
        listener = listener
    )
    if (state.authType == SIGN_UP) {
        CustomTextField(
            state = state,
            fieldType = PASSWORD_REPEAT,
            text = state.passwordRepeat,
            hint = "Подтверждения пароля",
            focusManager = focusManager,
            listener = listener
        )
    }
}

@Composable
private fun CustomTextField(
    state: State,
    fieldType: AuthTextFieldType,
    text: String,
    hint: String,
    focusManager: FocusManager,
    listener: Listener?
) {
    TextField(
        value = text,
        onValueChange = {
            listener?.onValueChanged(fieldType, it)
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            errorBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .border(
                border = BorderStroke(
                    width = 1.25.dp,
                    color = if (state.authError != null && state.authError.list.contains(fieldType)) {
                        MaterialTheme.colors.graphicsRed
                    } else {
                        MaterialTheme.colors.graphicsSecondary
                    }
                ),
                shape = RoundedCornerShape(12.dp)
            ),
        label = {
            Text(
                text = hint,
                modifier = Modifier.padding(top = 3.dp)
            )
        },
        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            },
            onDone = {
                listener?.authClick()
            }
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = when {
                state.authType == SIGN_IN && fieldType == PASSWORD -> ImeAction.Done
                state.authType == SIGN_UP && fieldType == PASSWORD_REPEAT -> ImeAction.Done
                else -> ImeAction.Next
            },
            keyboardType = if (fieldType == EMAIL) KeyboardType.Email else KeyboardType.Password
        ),
        trailingIcon = {
            if (fieldType == PASSWORD || fieldType == PASSWORD_REPEAT) {
                val icon = if (state.hidePassword) R.drawable.ic_eye_closed else R.drawable.ic_eye_opened
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            listener?.togglePasswordVisibility()
                        }
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = "Show or hide password icon",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        visualTransformation = if (state.hidePassword && fieldType != EMAIL) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        }
    )
}

/**
 * AUTH BUTTON
 */
@Composable
private fun AuthButton(
    state: State,
    listener: Listener?
) {
    val keyboard = LocalSoftwareKeyboardController.current
    StateButton(
        text = if (state.authType == SIGN_IN) "Войти" else "Зарегистрироваться",
        state = state.authButtonState,
        onSendRequest = {
            keyboard?.hide()
            listener?.authClick()
        },
        onSuccess = {
            listener?.toNextScreen()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
            .clip(RoundedCornerShape(24.dp))
    )
}

/**
 * TOGGLE TEXT
 */
@Composable
private fun ToggleText(
    state: State,
    listener: Listener?
) {
    val keyboard = LocalSoftwareKeyboardController.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                if (state.authType == SIGN_IN && !state.authSecurityWarningWasShown) {
                    keyboard?.hide()
                }
                listener?.toggleAccountMode()
            }
    ) {
        Text(
            text = if (state.authType == SIGN_IN) "Ещё нет аккаунта" else "Уже есть аккаунт",
            style = MaterialTheme.typography.subHeadlineRegular,
            color = MaterialTheme.colors.textColorSecondary,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        )
    }
}

/**
 * POLITICS TEXT
 */
@Composable
private fun TermsText(listener: Listener?) {
    val privacyText = "политикой конфиденциальности"
    val text = buildAnnotatedString {
        append("Продолжая, вы соглашаетесь с нашей ")
        appendLink(privacyText)
    }
    ClickableText(
        text = text,
        style = MaterialTheme.typography.caption2Regular.copy(
            color = MaterialTheme.colors.textColorSecondary,
            textAlign = TextAlign.Center
        ),
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
        onClick = { offset ->
            text.onTextClick(privacyText, offset) {
                listener?.openLink(PRIVACY_LINK)
            }
        }
    )
}

/***
 * PREVIEWS
 */
@Preview(showBackground = true)
@Composable
private fun PreviewSignIn() = MeetingsTheme {
    AuthMainScreenContent(
        state = State(authType = SIGN_IN),
        listener = null
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewSignUp() = MeetingsTheme {
    AuthMainScreenContent(
        state = State(),
        listener = null
    )
}