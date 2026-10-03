package shub39.momentum.core.interfaces

interface AnalyticsWrapper {
    fun trackEvent(event: String, properties: Map<String, Any>)
}