/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package shub39.momentum.analytics

import com.posthog.PostHog
import kotlin.time.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import shub39.momentum.BuildConfig
import shub39.momentum.billing.domain.BillingHandler
import shub39.momentum.core.data_classes.AnalyticsEvent
import shub39.momentum.core.interfaces.AnalyticsWrapper

class AnalyticsImpl : AnalyticsWrapper, KoinComponent {
    private fun getDefaultProperties() =
        mapOf(
            "app_name" to "Momentum",
            "app_version" to BuildConfig.VERSION_NAME,
            "time_stamp" to Clock.System.now().toEpochMilliseconds().div(1000),
            "is_pro" to get<BillingHandler>().isPlus.value,
        )

    override fun trackEvent(event: AnalyticsEvent, properties: Map<String, Any>) {
        PostHog.capture(event = event.name, properties = getDefaultProperties() + properties)
    }
}
