package shub39.momentum.billing

interface BillingHandler {
    suspend fun isProUser(): Boolean
    suspend fun userResult(): SubscriptionResult
}