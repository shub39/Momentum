package shub39.momentum.billing.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.revenuecat.purchases.ui.revenuecatui.Paywall
import com.revenuecat.purchases.ui.revenuecatui.PaywallOptions
import com.revenuecat.purchases.ui.revenuecatui.customercenter.CustomerCenter

@Composable
fun PaywallPage(
    isPlusUser: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val paywallOptions = remember {
        PaywallOptions.Builder(dismissRequest = onDismissRequest)
            .setShouldDisplayDismissButton(true)
            .build()
    }

    Box(modifier = modifier) {
        if (!isPlusUser) {
            Paywall(options = paywallOptions)
        } else {
            CustomerCenter(onDismiss = onDismissRequest)
        }
    }
}