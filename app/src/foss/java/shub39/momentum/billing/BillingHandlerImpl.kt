package shub39.momentum.billing

import org.koin.core.annotation.Single

@Single(binds = [BillingHandler::class])
class BillingHandlerImpl : BillingHandler {
    override suspend fun isPlusUser(): Boolean = true
    override suspend fun userResult(): SubscriptionResult = SubscriptionResult.Subscribed
}