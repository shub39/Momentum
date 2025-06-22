package shub39.momentum.home

interface HomeAction {
    data class OnChangeNotificationPref(val pref: Boolean) : HomeAction
}