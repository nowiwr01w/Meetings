package com.nowiwr01p.meetings.ui.create_meeting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.nowiwr01p.core_ui.EffectObserver
import com.nowiwr01p.core_ui.extensions.ReversedRow
import com.nowiwr01p.core_ui.navigators.main.Navigator
import com.nowiwr01p.core_ui.theme.MeetingsTheme
import com.nowiwr01p.core_ui.theme.graphicsSecondary
import com.nowiwr01p.core_ui.theme.graphicsTertiary
import com.nowiwr01p.core_ui.theme.subHeadlineRegular
import com.nowiwr01p.core_ui.ui.button.StateButton
import com.nowiwr01p.core_ui.ui.toolbar.ToolbarBackButton
import com.nowiwr01p.core_ui.ui.toolbar.ToolbarTitle
import com.nowiwr01p.core_ui.ui.toolbar.ToolbarTop
import com.nowiwr01p.meetings.ui.create_meeting.CreateMeetingContract.*
import com.nowiwr01p.meetings.ui.create_meeting.CreateMeetingContract.State
import com.nowiwr01p.meetings.ui.create_meeting.data.CheckBoxType
import com.nowiwr01p.meetings.ui.create_meeting.data.CheckBoxType.*
import com.nowiwr01p.meetings.ui.main.Category
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun CreateMeetingMainScreen(
    navigator: Navigator,
    viewModel: CreateMeetingVewModel = getViewModel()
) {
    val listener = object : Listener {
        override fun onBackClick() {
            navigator.navigateUp()
        }
        override fun setCheckBoxState(type: CheckBoxType, value: Boolean) {
            viewModel.setEvent(Event.SetCheckBoxState(type, value))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setEvent(Event.Init)
    }

    EffectObserver(viewModel.effect) {

    }

    CreateMeetingScreenContent(
        state = viewModel.viewState.value,
        listener = listener
    )
}

@Composable
private fun CreateMeetingScreenContent(
    state: State,
    listener: Listener?
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
) {
    Toolbar(listener)
    Content(state, listener)
}

@Composable
private fun Content(
    state: State,
    listener: Listener?
) = ConstraintLayout(
    modifier = Modifier.fillMaxWidth()
) {
    val (fieldsContainer, createButton) = createRefs()

    val fieldsContainerModifier = Modifier
        .fillMaxSize()
        .constrainAs(fieldsContainer) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
        }
    FieldsContainer(
        state = state,
        listener = listener,
        modifier = fieldsContainerModifier
    )

    val createButtonModifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
        .clip(RoundedCornerShape(24.dp))
        .constrainAs(createButton) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
        }
    StateButton(
        text = "Создать",
        modifier = createButtonModifier
    )
}

/**
 * TOOLBAR
 */
@Composable
private fun Toolbar(listener: Listener?) = ToolbarTop(
    title = {
        ToolbarTitle(title = "Создание митинга")
    },
    backIcon = {
        ToolbarBackButton { listener?.onBackClick() }
    },
)

/**
 * FIELDS TO FILL
 */
@Composable
private fun FieldsContainer(
    state: State,
    listener: Listener?,
    modifier: Modifier
) = LazyColumn(
    modifier = modifier.padding(horizontal = 16.dp)
) {
    item { CustomTextField(hint = "Ссылка на картинку") }
    item { Categories(state) }
    item { CustomTextField(hint = "Название") }
    item { CustomTextField(hint = "Описание") }
    item { Date(state, listener) }
    item { OpenDate(state, listener) }
    item { CustomTextField(hint = "Что взять с собой") }
    item { Posters(state, listener) }
    item { CustomTextField(hint = "Цели") }
    item { CustomTextField(hint = "Лозунги") }
    item { CustomTextField(hint = "Стратегия") }
    item { Spacer(modifier = Modifier.height(120.dp)) }
}

/**
 * TEXT FIELD
 */
@Preview(showBackground = true)
@Composable
private fun CustomTextField(
    hint: String = "Тестик",
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    showSubItemSlash: Boolean = false,
    trailingIconCallback: (() -> Unit)? = null
) = Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
) {
    if (showSubItemSlash) {
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "—",
            color = Color.Black,
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.width(24.dp))
    }
    TextField(
        value = "",
        onValueChange = {

        },
        readOnly = readOnly,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        label = {
            Text(
                text = hint,
                modifier = Modifier.padding(top = 3.dp)
            )
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
                    color = MaterialTheme.colors.graphicsSecondary
                ),
                shape = RoundedCornerShape(12.dp)
            ),
        trailingIcon = {
            if (trailingIconCallback != null) {
                Box(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { trailingIconCallback.invoke() }
                ) {
                    Icon(
                        painter = rememberVectorPainter(Icons.Default.Close),
                        contentDescription = "Toolbar back icon",
                        tint = MaterialTheme.colors.graphicsTertiary,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }
    )
}

/**
 * CATEGORIES
 */
@Composable
private fun Categories(
    state: State
) {
    val expanded = remember { mutableStateOf(false) }
    val rotateDegrees by animateFloatAsState(
        targetValue = if (expanded.value) 180f else 0f,
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                border = BorderStroke(
                    width = 1.25.dp,
                    color = MaterialTheme.colors.graphicsSecondary
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = state.categories.isEmpty()) {
                expanded.value = !expanded.value
            }
    ) {
        if (state.categories.isEmpty()) {
            CategoriesStub(expanded, rotateDegrees)
        } else {
            CategoriesList(state)
        }
    }
}

@Composable
private fun CategoriesList(state: State) = LazyRow(
    modifier = Modifier.fillMaxSize(),
    verticalAlignment = Alignment.CenterVertically
) {
    item { Spacer(modifier = Modifier.width(16.dp)) }
    items(state.categories) {
        Category(it)
    }
    item { Spacer(modifier = Modifier.width(16.dp)) }
}

@Composable
private fun CategoriesStub(
    expanded: MutableState<Boolean>,
    rotateDegrees: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Категории",
            color = Color(0x99000000),
            style = MaterialTheme.typography.subHeadlineRegular,
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier.clip(RoundedCornerShape(16.dp))
        ) {
            Icon(
                painter = rememberVectorPainter(Icons.Default.KeyboardArrowDown),
                contentDescription = "Drop down icon",
                tint = MaterialTheme.colors.graphicsTertiary,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(28.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .rotate(rotateDegrees)
                    .clickable { expanded.value = !expanded.value }

            )
        }
    }
}

/**
 * DATE
 */
@Composable
private fun Date(
    state: State,
    listener: Listener?
) {
    if (!state.isOpenDateCheckBoxChecked) {
        Column {
            val checked = remember { mutableStateOf(false) }
            ReversedRow {
                DateCheckBox(DATE, checked, listener)
                Spacer(modifier = Modifier.width(16.dp))
                CustomTextField(hint = "Дата", readOnly = true)
            }
            AnimatedVisibility(visible = checked.value) {
                Column {
                    CustomTextField(hint = "Дата", showSubItemSlash = true)
                    CustomTextField(hint = "Место встречи", showSubItemSlash = true)
                    CustomTextField(hint = "Название места встречи", showSubItemSlash = true)
                    CustomTextField(hint = "Детали места встречи", showSubItemSlash = true)
                    CustomTextField(hint = "Путь (не будет показан до начала митинга)", showSubItemSlash = true)
                }
            }
        }
    }
}

@Composable
private fun RowScope.DateCheckBox(
    type: CheckBoxType,
    checked: MutableState<Boolean>,
    listener: Listener?
) = Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
        .width(72.dp)
        .height(72.dp)
        .padding(top = 16.dp)
        .border(
            border = BorderStroke(
                width = 1.25.dp,
                color = MaterialTheme.colors.graphicsSecondary
            ),
            shape = RoundedCornerShape(12.dp)
        )
) {
    Checkbox(
        checked = checked.value,
        onCheckedChange = {
            checked.value = !checked.value
            listener?.setCheckBoxState(type, checked.value)
        },
        colors = CheckboxDefaults.colors(
            checkedColor = Color.Black,
            checkmarkColor = Color.White
        )
    )
}

/**
 * OPEN DATE
 */
@Composable
private fun OpenDate(
    state: State,
    listener: Listener?
) {
    if (!state.isDateCheckBoxChecked) {
        Column {
            val checked = remember { mutableStateOf(false) }
            ReversedRow {
                DateCheckBox(OPEN_DATE, checked, listener)
                Spacer(modifier = Modifier.width(16.dp))
                CustomTextField(hint = "Открытая дата", readOnly = true)
            }
            AnimatedVisibility(visible = checked.value) {
                Column {
                    CustomTextField(
                        hint = "Дата, до которой нужно будет собрать народ",
                        showSubItemSlash = true
                    )
                    CustomTextField(
                        hint = "Сколько людей должно присоединиться",
                        keyboardType = KeyboardType.Number,
                        showSubItemSlash = true
                    )
                }
            }
        }
    }
}

/**
 * POSTERS
 */
@Composable
private fun Posters(
    state: State,
    listener: Listener?
) {
    val scope = rememberCoroutineScope()
    Column {
        val checked = remember { mutableStateOf(false) }
        ReversedRow {
            DateCheckBox(POSTERS, checked, listener)
            Spacer(modifier = Modifier.width(16.dp))
            CustomTextField(hint = "Ссылки на плакаты", readOnly = true)
        }
        AnimatedVisibility(visible = checked.value) {
            val showFourth = remember { mutableStateOf(true) }
            val showFifth = remember { mutableStateOf(true) }
            Column {
                CustomTextField(hint = "1", showSubItemSlash = true)
                CustomTextField(hint = "2", showSubItemSlash = true)
                CustomTextField(hint = "3", showSubItemSlash = true)
                AnimatedVisibility(visible = showFourth.value) {
                    CustomTextField(hint = "4", showSubItemSlash = true) {
                        scope.launch {
                            delay(300)
                            showFourth.value = false
                        }
                    }
                }
                AnimatedVisibility(visible = showFifth.value) {
                    CustomTextField(hint = "5", showSubItemSlash = true) {
                        scope.launch {
                            delay(300)
                            showFifth.value = false
                        }
                    }
                }
            }
        }
    }
}

/**
 * PREVIEW
 */
@Preview(showBackground = true)
@Composable
private fun Preview() = MeetingsTheme {
    CreateMeetingScreenContent(
        state = State(),
        listener = null
    )
}