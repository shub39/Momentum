package shub39.momentum.billing.domain

interface BillingHandler {
    suspend fun isPlusUser(): Boolean
    suspend fun userResult(): SubscriptionResult
}