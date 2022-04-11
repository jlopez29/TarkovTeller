package jlapps.support.tarkovteller.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import jlapps.support.tarkovteller.R
import jlapps.support.tarkovteller.data.TarkovDataStore
import jlapps.support.tarkovteller.utilities.Util.getUriToResource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class TraderResetReceiver(): BroadcastReceiver() {
    private val LOG_TAG = "Trader Reset Receiver"
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        val trader = intent?.extras?.get("trader").toString()
        val interval = intent?.extras?.get("time").toString()
        val notifTime = intent?.extras?.get("notif_time").toString()
        Log.e(LOG_TAG,"notif $interval minutes until $trader resets")
        if(context != null){
            var builder = NotificationCompat.Builder(context,"Trader Reset")
                .setSmallIcon(R.drawable.ic_warning)
                .setContentTitle("$trader Reset")
                .setContentText("$interval minutes until $trader resets!")
                .setSound(getUriToResource(context,R.raw.notif))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate((longArrayOf(1000, 1000, 1000)))
            with(NotificationManagerCompat.from(context)) {
                var channel = NotificationChannel("Trader Reset","Trader Reset",
                    NotificationManager.IMPORTANCE_HIGH)
                var audio = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
                channel.setSound(getUriToResource(context,R.raw.notif),audio)
                channel.vibrationPattern = (longArrayOf(1000, 1000, 1000))
                createNotificationChannel(
                    channel
                )
                // notificationId is a unique int for each notification that you must define
                CoroutineScope(Dispatchers.IO).launch {
                    var lastNotif = TarkovDataStore.getTraderNotif(context,trader.lowercase()).first()
                    Log.e(LOG_TAG,"last $lastNotif")
                    launch{
                        if(saveNotif(context,trader.lowercase(),interval,notifTime,lastNotif))
                            notify(SystemClock.uptimeMillis().toInt(), builder.build())
                    }
                }
            }
        }

    }
    private suspend fun saveNotif(context:Context,trader:String, interval:String, notifTime:String,lastNotif:String?):Boolean{

        val notif = trader + "_" + notifTime + "_" + interval
        if(notif != lastNotif) {
            Log.e(LOG_TAG, "save notif $notif --- last notif $lastNotif")
            TarkovDataStore.saveTraderNotif(context, trader, notif)
            return true
        }
        return false
    }
}