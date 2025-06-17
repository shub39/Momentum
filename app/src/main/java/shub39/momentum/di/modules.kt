package shub39.momentum.di

import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import shub39.momentum.core.data.DatastoreFactory
import shub39.momentum.core.data.SettingsPrefsImpl
import shub39.momentum.core.domain.interfaces.SettingsPrefs
import shub39.momentum.viewmodels.OnboardingViewModel
import shub39.momentum.viewmodels.SettingsViewModel
import shub39.momentum.viewmodels.StateLayer

val modules = module {
    // datastore
    singleOf(::DatastoreFactory)
    single(named("settings_datastore")) { get<DatastoreFactory>().getSettingsDatastore() }
    single { SettingsPrefsImpl(get(named("settings_datastore"))) }.bind<SettingsPrefs>()

    // states and viewmodels
    singleOf(::StateLayer)
    singleOf(::SettingsViewModel)
    singleOf(::OnboardingViewModel)
}