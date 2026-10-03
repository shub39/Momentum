package shub39.momentum.analytics

import com.posthog.PostHog
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import shub39.momentum.BuildConfig
import shub39.momentum.billing.domain.BillingHandler
import shub39.momentum.core.interfaces.AnalyticsWrapper
import kotlin.time.Clock

class AnalyticsImpl: AnalyticsWrapper, KoinComponent {
    private fun getDefaultProperties() =
        mapOf(
            "app_name" to "Momentum",
            "app_version" to BuildConfig.VERSION_NAME,
            "time_stamp" to Clock.System.now().toEpochMilliseconds().div(1000),
            "is_pro" to get<BillingHandler>().isPlus.value,
        )


    override fun trackEvent(
        event: String,
        properties: Map<String, Any>
    ) {
        PostHog.capture(event = event, properties = getDefaultProperties() + properties)
    }
}