package shub39.momentum.billing

import android.content.Context
import org.koin.core.annotation.Single

@Single(binds = [BillingInitializer::class])
class BillingInitializerImpl : BillingInitializer {
    override fun initialize(context: Context) {}
}