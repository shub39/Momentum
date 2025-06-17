package shub39.momentum.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import shub39.momentum.di.modules

class MomentumApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MomentumApp)
            modules(modules)
        }
    }
}