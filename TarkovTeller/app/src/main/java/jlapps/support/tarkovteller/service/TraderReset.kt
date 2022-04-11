package jlapps.support.tarkovteller.service

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log

class TraderReset : Service() {
    var bi = Intent(COUNTDOWN_BR)
    var cdt: CountDownTimer? = null
    lateinit var trader:String

    override fun onCreate() {
        super.onCreate()

        Log.e(TAG, "Starting timer...")
    }


    override fun onDestroy() {
        cdt!!.cancel()
        Log.i(TAG, "Timer cancelled")
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val time = intent.extras?.get("time") as Long
        trader = intent.extras?.get("trader") as String
        Log.e(TAG, "Starting timer... $time")
        cdt = object : CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var diff = millisUntilFinished
                val secondsInMilli: Long = 1000
                val minutesInMilli = secondsInMilli * 60
                val hoursInMilli = minutesInMilli * 60

                val elapsedHours = diff / hoursInMilli
                diff %= hoursInMilli

                val elapsedMinutes = diff / minutesInMilli
                diff %= minutesInMilli

                val elapsedSeconds = diff / secondsInMilli
                var time = ""
                if(elapsedHours <= 9L)
                    time = "0$elapsedHours:"
                else
                    time = "$elapsedHours:"
                if(elapsedMinutes <= 9L)
                    time += "0$elapsedMinutes:"
                else
                    time += "$elapsedMinutes:"
                if(elapsedSeconds <=9L)
                    time += "0$elapsedSeconds"
                else
                    time += "$elapsedSeconds"
            }

            override fun onFinish() {
                Log.i(TAG, "Timer finished")
                bi.putExtra("trader","$trader")
                sendBroadcast(bi);
            }
        }
        (cdt as CountDownTimer).start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    companion object {
        private const val TAG = "ResetService"
        const val COUNTDOWN_BR = "jlapps.countdown_br"
    }
}