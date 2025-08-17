package shub39.momentum.billing

interface BillingHandler {
    suspend fun isPlusUser(): Boolean
    suspend fun userResult(): SubscriptionResult
}