package com.nowiwr01p.meetings.ui.create_meeting

import com.google.android.gms.maps.model.LatLng
import com.nowiwr01p.core.datastore.location.data.*
import com.nowiwr01p.core.model.Category
import com.nowiwr01p.core_ui.ui.bottom_sheet.ShowBottomSheetHelper
import com.nowiwr01p.core_ui.view_model.BaseViewModel
import com.nowiwr01p.domain.cteate_meeting.GetCachedCategoriesUseCase
import com.nowiwr01p.domain.execute
import com.nowiwr01p.domain.map.GetLocalUserUseCase
import com.nowiwr01p.meetings.ui.create_meeting.CreateMeetingContract.*
import com.nowiwr01p.meetings.ui.create_meeting.data.CustomTextFieldType
import com.nowiwr01p.meetings.ui.create_meeting.data.CustomTextFieldType.*
import com.nowiwr01p.meetings.ui.create_meeting.data.DetailsItemType
import com.nowiwr01p.meetings.ui.create_meeting.data.DetailsItemType.*

class CreateMeetingVewModel(
    private val getCachedCategoriesUseCase: GetCachedCategoriesUseCase,
    private val getLocalUserUseCase: GetLocalUserUseCase,
    private val showBottomSheetHelper: ShowBottomSheetHelper
): BaseViewModel<Event, State, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> init()
            is Event.OnAddDetailsItemClick -> addRemoveDetailsItem(event.type)
            is Event.OnEditDetailsItemClick -> editDetailsItem(event.type, event.index, event.value)
            is Event.OnRemoveDetailsItemClick -> addRemoveDetailsItem(event.type, event.index)
            is Event.OnEditCustomTextField -> editCustomTextField(event.type, event.value)
            is Event.OnSelectedCategoryClick -> selectCategory(event.category)
            is Event.ShowCategoriesBottomSheet -> showBottomSheetHelper.showBottomSheet(event.content)
            is Event.NavigateToMapDrawPath -> setEffect { Effect.NavigateToMapDrawPath }
            is Event.NavigateToChooseStartLocation -> setEffect { Effect.NavigateToChooseStartLocation }
            is Event.ShowDateTimePicker -> showDateTimePicker()
            is Event.SelectDate -> selectDate(event.date)
            is Event.SelectTime -> selectTime(event.time)
            is Event.SetDrawnPath -> setPath(event.path)
            is Event.SetStartLocationPath -> setStartLocation(event.position)
            is Event.NavigateToPreview -> setEffect { Effect.NavigateToPreview(buildMeeting()) }
        }
    }

    private fun init() = io {
        runCatching {
            getCategories()
            getUserData()
        }
    }

    /**
     * CATEGORIES
     */
    private suspend fun getCategories() {
        val categories = getCachedCategoriesUseCase.execute()
        setState { copy(categories = categories) }
    }

    /**
     * USER
     */
    private suspend fun getUserData() {
        val user = getLocalUserUseCase.execute()
        setState { copy(user = user) }
    }

    /**
     * SELECTED CATEGORIES
     */
    private fun selectCategory(category: Category) = with(viewState.value) {
        val updated = selectedCategories.toMutableSet().apply {
            if (selectedCategories.contains(category)) remove(category) else add(category)
        }
        setState { copy(selectedCategories = updated) }
    }

    /**
     * DETAILS (POSTER LINKS, GOALS, SLOGANS, STRATEGY)
     */
    private fun addRemoveDetailsItem(type: DetailsItemType, index: Int = -1) = with(viewState.value) {
        val updated = getDetailsList(type).toMutableList().apply {
            if (index == -1) add("") else removeAt(index)
        }
        updateDetailsList(type, updated)
    }

    private fun editDetailsItem(type: DetailsItemType, index: Int, value: String) = with(viewState.value) {
        val updated = getDetailsList(type).mapIndexed { curIndex, item ->
            if (index == curIndex) value else item
        }
        updateDetailsList(type, updated)
    }

    private fun getDetailsList(type: DetailsItemType) = with(viewState.value) {
        when (type) {
            GOALS -> goals
            SLOGANS -> slogans
            STRATEGY -> strategy
            POSTER_LINKS -> posters
        }
    }

    private fun updateDetailsList(type: DetailsItemType, list: List<String>) = setState {
        when (type) {
            GOALS -> copy(goals = list)
            SLOGANS -> copy(slogans = list)
            STRATEGY -> copy(strategy = list)
            POSTER_LINKS -> copy(posters = list)
        }
    }

    /**
     * CUSTOM TEXT FIELD (IMAGE, TITLE, DESCRIPTION, OPEN DATE, LOCATION...)
     */
    private fun editCustomTextField(type: CustomTextFieldType, value: String) = setState {
        when (type) {
            TOP_IMAGE -> copy(imageLink = value)
            TITLE -> copy(title = value)
            DESCRIPTION -> copy(description = value)
            OPEN_DATE -> copy(requiresPeopleCount = value)
            TELEGRAM -> copy(telegram = value)
            POSTER_MOTIVATION -> copy(postersMotivation = value)
            LOCATION -> copy(location = value)
            else -> copy(locationDetails = value)
        }
    }

    /**
     * DATE TIME PICKER
     */
    private fun showDateTimePicker() {
        setState { copy(showDatePicker = true) }
    }

    private fun selectDate(date: String) = setState {
        copy(selectedDate = date, showDatePicker = false, showTimePicker = true)
    }

    private fun selectTime(time: String) = setState {
        copy(selectedTime = time, showTimePicker = false)
    }

    /**
     * LOCATION
     */
    private fun setPath(path: List<LatLng>) = setState {
        copy(path = path)
    }

    private fun setStartLocation(position: LatLng) = setState {
        copy(startLocation = position)
    }

    /**
     * BUILD MEETING
     */
    private fun buildMeeting() = with(viewState.value) {
        Meeting(
            id = "", // TODO
            cityName = user.city.name,
            creatorId = user.id,
            image = imageLink,
            date = "$selectedDate $selectedTime",
            requiredPeopleCount = requiresPeopleCount.toIntOrNull() ?: 0,
            categories = selectedCategories.toList(),
            title = title,
            description = description,
            locationInfo = LocationInfo(
                locationName = location,
                locationStartPoint = Coordinate(startLocation.latitude, startLocation.longitude),
                locationDetails = locationDetails,
                path = path.map { Coordinate(it.latitude, it.longitude) }
            ),
            takeWithYouInfo = TakeWithYouInfo(
                postersMotivation = postersMotivation,
                posters = posters
            ),
            details = Details(
                goals =  goals,
                slogans = slogans,
                strategy = strategy
            ),
            telegram = telegram
        )
    }
}