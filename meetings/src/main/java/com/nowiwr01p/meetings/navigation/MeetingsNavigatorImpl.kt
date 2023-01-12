package com.nowiwr01p.meetings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation
import com.nowiwr01p.core.datastore.location.data.Meeting
import com.nowiwr01p.core_ui.navigators.MeetingsNavigator
import com.nowiwr01p.core_ui.navigators.main.Navigator
import com.nowiwr01p.meetings.MeetingsScreen.*

class MeetingsNavigatorImpl: MeetingsNavigator {

    private lateinit var navController: NavController

    override fun setNavController(curNavController: NavController) {
        navController = curNavController
    }

    override fun navigateToMeetingInfo(meeting: Meeting) {
        MeetingMainScreen.navigate(meeting, navController)
    }

    override fun navigateToCreateMeeting() {
        CreateMeetingScreen.navigate(Unit, navController)
    }

    override fun navigateToMapDrawPath() {
        MapDrawPathScreen.navigate(Unit, navController)
    }

    override fun graph(builder: NavGraphBuilder, navigator: Navigator) {
        builder.navigation(MeetingsMainScreen.route, MeetingsMainScreen.rootRoute) {
            MeetingsMainScreen.createScreen(builder, navigator)
            MeetingMainScreen.createScreen(this, navigator)
            CreateMeetingScreen.createScreen(this, navigator)
            MapDrawPathScreen.createScreen(this, navigator)
        }
    }
}