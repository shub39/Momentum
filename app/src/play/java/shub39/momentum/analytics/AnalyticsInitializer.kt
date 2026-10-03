package shub39.momentum.analytics

import android.content.Context
import com.posthog.PostHog
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig
import com.revenuecat.purchases.Purchases
import shub39.momentum.BuildConfig

class AnalyticsInitializer {
    private val config =
        PostHogAndroidConfig(apiKey = BuildConfig.POSTHOG_API_KEY, host = BuildConfig.POSTHOG_HOST)

    fun setup(context: Context) {
        PostHogAndroid.setup(context, config)

        val rcId = Purchases.sharedInstance.appUserID
        PostHog.identify("momentum:$rcId")
    }
}