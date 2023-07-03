package com.octopus.task.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.octopus.task.ui.MainActivity

class BootUpReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        p0?.let { context ->
            p1?.let { intent ->
                if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
                    val i = Intent(
                        context,
                        MainActivity::class.java
                    )
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(i)
                }
            }
        }

    }
}
