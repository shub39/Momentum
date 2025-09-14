package shub39.momentum.billing.data

import com.revenuecat.purchases.CacheFetchPolicy
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.awaitCustomerInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import shub39.momentum.BuildConfig
import shub39.momentum.billing.domain.BillingHandler
import shub39.momentum.billing.domain.SubscriptionResult

@Single(binds = [BillingHandler::class])
class BillingHandlerImpl : BillingHandler {
    private val purchases by lazy { Purchases.sharedInstance }

    override suspend fun isPlusUser(): Boolean {
        return userResult() is SubscriptionResult.Subscribed
    }

    override suspend fun userResult(): SubscriptionResult {
        if (BuildConfig.DEBUG) return SubscriptionResult.Subscribed

        try {
            val userInfo = withContext(Dispatchers.IO) {
                purchases.awaitCustomerInfo(fetchPolicy = CacheFetchPolicy.NOT_STALE_CACHED_OR_CURRENT)
            }
            val entitlement = userInfo.entitlements.all[ENTITLEMENT_PLUS]
            val isPlus = entitlement?.isActive
            if (isPlus == true) {
                return SubscriptionResult.Subscribed
            }
        } catch (e: Exception) {
            return SubscriptionResult.Error(e)
        }

        return SubscriptionResult.NotSubscribed
    }

    companion object {
        private const val ENTITLEMENT_PLUS = "plus"
    }
}