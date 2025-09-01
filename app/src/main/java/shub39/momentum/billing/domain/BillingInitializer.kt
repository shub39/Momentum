package shub39.momentum.billing.domain

import android.content.Context

interface BillingInitializer {
    fun initialize(context: Context)
}