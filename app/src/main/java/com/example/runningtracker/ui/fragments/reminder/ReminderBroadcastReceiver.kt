package com.example.runningtracker.ui.fragments.reminder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.runningtracker.R
import com.example.runningtracker.ui.activities.MainActivity

class ReminderBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_REMINDER_NAME = "CHANNEL_REMINDER"
        private const val CHANNEL_REMINDER_ID = "DEFAULT_CHANNEL_REMINDER_ID"
        const val REMINDER_BROADCAST_RECEIVER = "REMINDER_BROADCAST_RECEIVER"
        private const val DEFAULT_REQUEST_CODE = 1
    }

    override fun onReceive(context: Context, intent: Intent) {
        createNotificationChanel(context)
        val requestCode = intent.getIntExtra(ReminderFragment.REQUEST_ID, DEFAULT_REQUEST_CODE)
        val intentActivity = Intent(context, MainActivity::class.java)
        intentActivity.putExtra(REMINDER_BROADCAST_RECEIVER, true)
        intentActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val resultPendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            intentActivity,
            0
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = createNotification(context, resultPendingIntent)
            NotificationManagerCompat.from(context).notify(requestCode, notification)
        }
    }

    private fun createNotification(
        context: Context,
        resultPendingIntent: PendingIntent
    ): Notification {

        return NotificationCompat.Builder(context, CHANNEL_REMINDER_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getText(R.string.its_time_for_jogging))
            .setContentText(context.getText(R.string.its_time_to_pull_yourself_together_and_run))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()
    }

    private fun createNotificationChanel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val reminderChannel = NotificationChannel(
                CHANNEL_REMINDER_ID,
                CHANNEL_REMINDER_NAME,
                IMPORTANCE_DEFAULT
            )
            reminderChannel.description = context.getString(R.string.reminders_from)
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(reminderChannel)
        }
    }

}