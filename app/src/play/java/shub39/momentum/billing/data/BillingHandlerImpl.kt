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
package shub39.momentum.billing.data

import com.revenuecat.purchases.CacheFetchPolicy
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.awaitCustomerInfo
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import shub39.momentum.billing.domain.BillingHandler
import shub39.momentum.billing.domain.SubscriptionResult

@Single(binds = [BillingHandler::class])
class BillingHandlerImpl : BillingHandler {
    private val purchases by lazy { Purchases.sharedInstance }

    override val isPlus = MutableStateFlow(false)

    init {
        purchases.updatedCustomerInfoListener = UpdatedCustomerInfoListener { customerInfo ->
            isPlus.update { customerInfo.entitlements.all[ENTITLEMENT_PLUS]?.isActive == true }
        }
    }

    override suspend fun isPlusUser(): Boolean {
        isPlus.update { userResult() is SubscriptionResult.Subscribed }
        return isPlus.value
    }

    override suspend fun userResult(): SubscriptionResult {
        try {
            val userInfo =
                withContext(Dispatchers.IO) {
                    purchases.awaitCustomerInfo(
                        fetchPolicy = CacheFetchPolicy.NOT_STALE_CACHED_OR_CURRENT
                    )
                }
            val entitlement = userInfo.entitlements.all[ENTITLEMENT_PLUS]
            val subscribed = entitlement?.isActive
            if (subscribed == true) {
                isPlus.update { true }
                return SubscriptionResult.Subscribed
            }
        } catch (e: Exception) {
            return SubscriptionResult.Error(e)
        }

        isPlus.update { false }
        return SubscriptionResult.NotSubscribed
    }

    companion object {
        private const val ENTITLEMENT_PLUS = "plus"
    }

    override suspend fun isFoss(): Boolean = false
}
