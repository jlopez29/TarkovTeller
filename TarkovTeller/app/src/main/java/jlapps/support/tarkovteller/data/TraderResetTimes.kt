package jlapps.support.tarkovteller.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import jlapps.support.tarkovteller.api.API
import jlapps.support.tarkovteller.receivers.TraderResetReceiver
import jlapps.support.tarkovteller.utilities.Util
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

object TraderResetTimes {
    private val LOG_TAG = "Trader Reset"
    var praporReset = ""
    var praporEnabled = false
    var therapistReset = ""
    var therapistEnabled = false
    var skierReset = ""
    var skierEnabled = false
    var peacekeeperReset = ""
    var peacekeeperEnabled = false
    var mechanicReset = ""
    var mechanicEnabled = false
    var ragmanReset = ""
    var ragmanEnabled = false
    var jaegerReset = ""
    var jaegerEnabled = false

    var traderResetInterval = 10
    
    suspend fun getResetTimes(context: Context){

        coroutineScope {
            launch {
//                TarkovDataStore.getTraderReset(context,"Prapor").catch { e->e.printStackTrace() }
//                    .collect {
//                        praporEnabled = it
//                    }
                praporEnabled = TarkovDataStore.getTraderReset(context,"prapor").first()
            }
            launch{
                therapistEnabled = TarkovDataStore.getTraderReset(context,"therapist").first()
            }

            launch{

                skierEnabled = TarkovDataStore.getTraderReset(context,"skier").first()
            }
            launch {
                peacekeeperEnabled = TarkovDataStore.getTraderReset(context,"peacekeeper").first()
            }
            launch {
                mechanicEnabled = TarkovDataStore.getTraderReset(context,"mechanic").first()
            }

            launch {
                ragmanEnabled = TarkovDataStore.getTraderReset(context,"ragman").first()
            }
            launch {
                jaegerEnabled = TarkovDataStore.getTraderReset(context,"jaeger").first()
            }
        }
    }

    fun setupNotification(context: Context,interval:Int,trader:String){
        var manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val traderInterval = TimeUnit.MINUTES.toMillis(interval.toLong())
        Log.e(LOG_TAG,"trader interval ms: $traderInterval")

        when(trader){
            "prapor"->{
                var intent = Intent(context, TraderResetReceiver::class.java)
                intent.putExtra("time",interval)
                intent.putExtra("trader","Prapor")
                intent.putExtra("notif_time",praporReset)

                if(praporEnabled){

                    Log.e(LOG_TAG, "************************************")
                    Log.e(LOG_TAG, "************** PRAPOR ************** ENABLED")
                    Log.e(LOG_TAG, "************************************")
                    val pendingIntent = PendingIntent.getBroadcast(context,1,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    manager.setExact(AlarmManager.RTC_WAKEUP,
                        Util.convertTime(praporReset, traderInterval), pendingIntent)
                }else {

                    Log.e(LOG_TAG, "************************************")
                    Log.e(LOG_TAG, "************** PRAPOR ************** DISABLED")
                    Log.e(LOG_TAG, "************************************")
                    val pendingIntent = PendingIntent.getBroadcast(context,1,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    manager.cancel(pendingIntent)
                    pendingIntent.cancel()
                }
            }
            "therapist"->{
                var intent = Intent(context, TraderResetReceiver::class.java)
                intent.putExtra("time",interval)
                intent.putExtra("trader","Therapist")
                intent.putExtra("notif_time", therapistReset)
                if(therapistEnabled){

                    Log.e(LOG_TAG, "************************************")
                    Log.e(LOG_TAG, "************** THERAPIST ************** ENABLED")
                    Log.e(LOG_TAG, "************************************")
                    val pendingIntent = PendingIntent.getBroadcast(context,2,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    manager.setExact(AlarmManager.RTC_WAKEUP,
                        Util.convertTime(therapistReset, traderInterval), pendingIntent)
                }else {

                    Log.e(LOG_TAG, "************************************")
                    Log.e(LOG_TAG, "************ THERAPIST ************* DISABLED")
                    Log.e(LOG_TAG, "************************************")

                    val pendingIntent = PendingIntent.getBroadcast(context,2,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    manager.cancel(pendingIntent)
                    pendingIntent.cancel()
                }

            }
            "skier"->{

                var intent = Intent(context, TraderResetReceiver::class.java)
                intent.putExtra("time",interval)
                intent.putExtra("trader","Skier")
                intent.putExtra("notif_time", skierReset)
                if(skierEnabled){

                    Log.e(LOG_TAG, "************************************")
                    Log.e(LOG_TAG, "************** SKIER *************** ENABLED")
                    Log.e(LOG_TAG, "************************************")
                    val pendingIntent = PendingIntent.getBroadcast(context,3,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    manager.setExact(AlarmManager.RTC_WAKEUP,
                        Util.convertTime(skierReset, traderInterval), pendingIntent)
                }else {

                    Log.e(LOG_TAG, "************************************")
                    Log.e(LOG_TAG, "************** SKIER *************** DISABLED")
                    Log.e(LOG_TAG, "************************************")
                    val pendingIntent = PendingIntent.getBroadcast(context,3,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    manager.cancel(pendingIntent)
                    pendingIntent.cancel()
                }

            }
            "peacekeeper"->{
                var intent = Intent(context, TraderResetReceiver::class.java)
                intent.putExtra("time",interval)
                intent.putExtra("trader","Peacekeeper")
                intent.putExtra("notif_time", peacekeeperReset)
                if(peacekeeperEnabled){
                    Log.e(LOG_TAG, "************************************")
                    Log.e(LOG_TAG, "*********** PEACEKEEPER ************ ENABLED")
                    Log.e(LOG_TAG, "************************************")
                    val pendingIntent = PendingIntent.getBroadcast(context,4,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    manager.setExact(AlarmManager.RTC_WAKEUP,
                        Util.convertTime(peacekeeperReset, traderInterval), pendingIntent)
                }else {

                    Log.e(LOG_TAG, "************************************")
                    Log.e(LOG_TAG, "*********** PEACEKEEPER ************ DISABLED")
                    Log.e(LOG_TAG, "************************************")
                    val pendingIntent = PendingIntent.getBroadcast(context,4,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    manager.cancel(pendingIntent)
                    pendingIntent.cancel()
                }
            }
            "mechanic"->{
                var intent = Intent(context, TraderResetReceiver::class.java)
                intent.putExtra("time",interval)
                intent.putExtra("trader","Mechanic")
                intent.putExtra("notif_time", mechanicReset)
                if(mechanicEnabled){

                    Log.e(LOG_TAG, "************************************")
                    Log.e(LOG_TAG, "************ MECHANIC ************** ENABLED")
                    Log.e(LOG_TAG, "************************************")

                    val pendingIntent = PendingIntent.getBroadcast(context,5,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    manager.setExact(AlarmManager.RTC_WAKEUP,
                        Util.convertTime(mechanicReset, traderInterval), pendingIntent)
                }else {

                    Log.e(LOG_TAG, "************************************")
                    Log.e(LOG_TAG, "************ MECHANIC ************** DISABLED")
                    Log.e(LOG_TAG, "************************************")
                    val pendingIntent = PendingIntent.getBroadcast(context,5,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    manager.cancel(pendingIntent)
                    pendingIntent.cancel()
                }
            }
            "ragman"->{
                var intent = Intent(context, TraderResetReceiver::class.java)
                intent.putExtra("time",interval)
                intent.putExtra("trader","Ragman")
                intent.putExtra("notif_time", ragmanReset)
                if(ragmanEnabled){

                    Log.e(LOG_TAG, "************************************")
                    Log.e(LOG_TAG, "************* RAGMAN *************** ENABLED")
                    Log.e(LOG_TAG, "************************************")

                    val pendingIntent = PendingIntent.getBroadcast(context,6,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    manager.setExact(AlarmManager.RTC_WAKEUP,
                        Util.convertTime(ragmanReset, traderInterval), pendingIntent)
                }else {

                    Log.e(LOG_TAG, "************************************")
                    Log.e(LOG_TAG, "************* RAGMAN *************** DISABLED")
                    Log.e(LOG_TAG, "************************************")
                    val pendingIntent = PendingIntent.getBroadcast(context,6,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    manager.cancel(pendingIntent)
                    pendingIntent.cancel()
                }
            }
            "jaeger"->{
                var intent = Intent(context, TraderResetReceiver::class.java)
                intent.putExtra("time",interval)
                intent.putExtra("trader","Jaeger")
                intent.putExtra("notif_time", jaegerReset)
                if(jaegerEnabled){

                    Log.e(LOG_TAG, "************************************")
                    Log.e(LOG_TAG, "************* JAEGER *************** ENABLED")
                    Log.e(LOG_TAG, "************************************")
                    val pendingIntent = PendingIntent.getBroadcast(context,7,intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

                    manager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        Util.convertTime(jaegerReset, traderInterval), pendingIntent)
                }else {
                    Log.e(LOG_TAG, "************************************")
                    Log.e(LOG_TAG, "************* JAEGER *************** DISABLED")
                    Log.e(LOG_TAG, "************************************")
                    val pendingIntent = PendingIntent.getBroadcast(context,7,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    manager.cancel(pendingIntent)
                    pendingIntent.cancel()
                }
            }
        }
    }
}