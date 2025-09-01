package shub39.momentum.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module
import shub39.momentum.billing.data.BillingInitializerImpl
import shub39.momentum.di.AppModule

class MomentumApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MomentumApp)
            modules(AppModule().module)
        }

        BillingInitializerImpl().initialize(this)
    }
}