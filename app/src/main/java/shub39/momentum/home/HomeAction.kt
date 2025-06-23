package shub39.momentum.home

import shub39.momentum.core.domain.data_classes.Project

interface HomeAction {
    data class OnChangeNotificationPref(val pref: Boolean) : HomeAction
    data class OnChangeProject(val project: Project) : HomeAction
}