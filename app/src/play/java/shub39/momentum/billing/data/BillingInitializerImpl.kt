package shub39.momentum.billing.data

import android.content.Context
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import shub39.momentum.billing.domain.BillingInitializer

class BillingInitializerImpl : BillingInitializer {
    override fun initialize(context: Context) {
        Purchases.logLevel = LogLevel.WARN
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