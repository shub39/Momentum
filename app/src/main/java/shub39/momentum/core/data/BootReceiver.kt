package shub39.momentum.core.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent

@Single
class BootReceiver : BroadcastReceiver(), KoinComponent {
    override fun onReceive(context: Context?, intent: Intent?) {
        TODO("Not yet implemented")
    }
}