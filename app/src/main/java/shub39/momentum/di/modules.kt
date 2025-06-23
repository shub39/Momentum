package shub39.momentum.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import shub39.momentum.core.data.ProjectRepositoryImpl
import shub39.momentum.core.data.database.ProjectDBFactory
import shub39.momentum.core.data.database.ProjectDatabase
import shub39.momentum.core.data.datastore.DatastoreFactory
import shub39.momentum.core.data.datastore.SettingsPrefsImpl
import shub39.momentum.core.domain.interfaces.SettingsPrefs
import shub39.momentum.viewmodels.HomeViewmodel
import shub39.momentum.viewmodels.OnboardingViewModel
import shub39.momentum.viewmodels.SettingsViewModel
import shub39.momentum.viewmodels.StateLayer

val modules = module {
    // database
    singleOf(::ProjectDBFactory)
    single {
        get<ProjectDBFactory>()
            .create()
            .fallbackToDestructiveMigration(true)
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<ProjectDatabase>().projectDao }
    single { get<ProjectDatabase>().daysDao }

    // datastore
    singleOf(::DatastoreFactory)
    single(named("settings_datastore")) { get<DatastoreFactory>().getSettingsDatastore() }
    single { SettingsPrefsImpl(get(named("settings_datastore"))) }.bind<SettingsPrefs>()

    // repositories and use cases
    singleOf(::ProjectRepositoryImpl)

    // states and viewmodels
    singleOf(::StateLayer)
    singleOf(::SettingsViewModel)
    singleOf(::HomeViewmodel)
    singleOf(::OnboardingViewModel)
}