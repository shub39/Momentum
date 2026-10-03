package shub39.momentum.billing.analytics

import shub39.momentum.core.interfaces.AnalyticsWrapper

class AnalyticsImpl : AnalyticsWrapper {
    override fun trackEvent(event: String, properties: Map<String, Any>) {}
}