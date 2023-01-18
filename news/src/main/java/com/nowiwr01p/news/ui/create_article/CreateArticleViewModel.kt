package com.nowiwr01p.news.ui.create_article

import com.nowiwr01p.core.model.*
import com.nowiwr01p.core_ui.ui.bottom_sheet.BottomSheetParams
import com.nowiwr01p.core_ui.ui.bottom_sheet.ShowBottomSheetHelper
import com.nowiwr01p.core_ui.view_model.BaseViewModel
import com.nowiwr01p.news.ui.create_article.CreateArticleContract.*
import com.nowiwr01p.news.ui.create_article.data.CreateArticleBottomSheetType
import com.nowiwr01p.news.ui.create_article.data.CreateArticleBottomSheetType.*
import com.nowiwr01p.news.ui.create_article.data.CreateArticleBottomSheetType.SUBTITLE
import com.nowiwr01p.news.ui.create_article.data.StaticFields
import com.nowiwr01p.news.ui.create_article.data.StaticFields.*

class CreateArticleViewModel(
    private val showBottomSheetHelper: ShowBottomSheetHelper
): BaseViewModel<Event, State, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.NavigateBack -> setEffect { Effect.NavigateBack }
            is Event.ShowBottomSheet -> showBottomSheet(event.params)
            is Event.OnBottomSheetTypeClick -> addField(event.type)
            is Event.OnStaticFieldChanged -> changeStaticField(event.type, event.value)
        }
    }

    private fun showBottomSheet(params: BottomSheetParams) {
        showBottomSheetHelper.showBottomSheet(params)
    }

    /**
     * ADD CUSTOM TEXT FIELD ON THE SCREEN
     */
    private fun addField(type: CreateArticleBottomSheetType) = with(viewState.value) {
        val item = when (type) {
            SUBTITLE -> SubTitle(subTitles.size).also {
                setState { copy(subTitles = update(subTitles, it)) }
            }
            TEXT -> Description(descriptions.size).also {
                setState { copy(descriptions = update(descriptions, it)) }
            }
            IMAGE -> ImageList(images.size).also {
                setState { copy(images = update(images, it)) }
            }
            ORDERED_LIST -> OrderedList(orderedLists.size).also {
                setState { copy(orderedLists = update(orderedLists, it)) }
            }
        }
        setState { copy(content = update(content, item)) }
    }

    private inline fun <reified T: ArticleData> update(list: List<T>, value: T) = list
        .toMutableList()
        .apply { add(value) }


    /**
     * CHANGE TOP-3 ITEMS VALUES
     */
    private fun changeStaticField(type: StaticFields, value: String) = setState {
        when (type) {
            TOP_IMAGE_FIELD -> copy(image = TopImage(link = value))
            TITLE_FIELD -> copy(title = Title(text = value))
            DESCRIPTION_FIELD -> copy(description = Description(text = value))
        }
    }
}