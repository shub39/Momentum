package shub39.momentum.billing

import android.content.Context
import org.koin.core.annotation.Single
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import shub39.momentum.BuildConfig

@Single(binds = [BillingInitializer::class])
class BillingInitializerImpl : BillingInitializer {
    override fun initialize(context: Context) {
        Purchases.logLevel = if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.WARN
        Purchases.configure(
            PurchasesConfiguration.Builder(
                context,
                PURCHASES_KEY
            ).build()
        )
    }

    companion object {
        private const val PURCHASES_KEY = "goog_lyxKVVwSslbODvQBmxtYQoSaweL"
    }
}