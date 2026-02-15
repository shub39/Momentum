package shub39.momentum.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import shub39.momentum.BuildConfig
import shub39.momentum.app.MainAppState
import shub39.momentum.billing.domain.BillingHandler
import shub39.momentum.data.ChangelogManager
import shub39.momentum.domain.interfaces.SettingsPrefs

@KoinViewModel
class MainAppViewModel(
    private val datastore: SettingsPrefs,
    private val billingHandler: BillingHandler,
    private val changelogManager: ChangelogManager
) : ViewModel() {
    private var observerJob: Job? = null

    private val _state = MutableStateFlow(MainAppState())
    val state = _state.asStateFlow()
        .onStart {
            checkSubscription()
            checkChangelog()
            observeData()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainAppState()
        )

    fun checkSubscription() {
        viewModelScope.launch {
            val result = billingHandler.isPlusUser()

            _state.update {
                it.copy(isPlusUser = result)
            }
        }
    }

    fun dismissChangelog() {
        _state.update { it.copy(currentChangelog = null) }
    }

    private fun checkChangelog() {
        viewModelScope.launch {
            val changeLogs = changelogManager.changelogs.first()
            val lastShownChangelog = datastore.getLastChangelogShown().first()

            if (BuildConfig.DEBUG || lastShownChangelog.isBlank() || lastShownChangelog != BuildConfig.VERSION_NAME) {
                _state.update {
                    it.copy(currentChangelog = changeLogs.firstOrNull())
                }
                datastore.updateLastChangelogShown(BuildConfig.VERSION_NAME)
            }
        }
    }

    private fun observeData() {
        observerJob?.cancel()
        observerJob = viewModelScope.launch {
            datastore.getMaterialYouFlow()
                .onEach { pref ->
                    _state.update {
                        it.copy(
                            theme = it.theme.copy(
                                isMaterialYou = pref
                            )
                        )
                    }
                }
                .launchIn(this)

            combine(
                datastore.getPaletteStyle(),
                datastore.getFontFlow(),
                datastore.getSeedColorFlow(),
                datastore.getAppThemePrefFlow(),
                datastore.getAmoledPrefFlow()
            ) { style, font, seedColor, appTheme, amoled ->
                _state.update {
                    it.copy(
                        theme = it.theme.copy(
                            paletteStyle = style,
                            font = font,
                            seedColor = seedColor,
                            appTheme = appTheme,
                            isAmoled = amoled
                        )
                    )
                }
            }.launchIn(this)

            datastore.getOnboardingDoneFlow()
                .onEach { pref ->
                    _state.update {
                        it.copy(isOnboardingDone = pref)
                    }
                }
                .launchIn(this)
        }
    }
}