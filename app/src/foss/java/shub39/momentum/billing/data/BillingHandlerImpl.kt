package shub39.momentum.billing.data

import org.koin.core.annotation.Single
import shub39.momentum.billing.domain.BillingHandler
import shub39.momentum.billing.domain.SubscriptionResult

@Single(binds = [BillingHandler::class])
class BillingHandlerImpl : BillingHandler {
    override suspend fun isPlusUser(): Boolean = true
    override suspend fun userResult(): SubscriptionResult = SubscriptionResult.Subscribed
}