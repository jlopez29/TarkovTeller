package jlapps.support.tarkovteller.api

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import jlapps.support.tarkovteller.R
import jlapps.support.tarkovteller.data.ItemsDB
import jlapps.support.tarkovteller.data.TarkovDataStore
import jlapps.support.tarkovteller.data.TarkovMarketItem
import jlapps.support.tarkovteller.data.TraderResetTimes
import jlapps.support.tarkovteller.fragments.FragmentTraderResets
import jlapps.support.tarkovteller.receivers.TraderResetReceiver
import jlapps.support.tarkovteller.service.TraderReset
import jlapps.support.tarkovteller.utilities.Util.convertTime
import kotlinx.coroutines.flow.collect
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.closeQuietly
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

object API {
    private var LOG_TAG = "API"
    lateinit var requestQueue:RequestQueue
    val gson = Gson()
    fun init(context:Context){
        requestQueue = Volley.newRequestQueue(context)
        requestQueue.start()
    }

    fun getAllItems(context:Context,success:MutableLiveData<Boolean>){
        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET,context.getString(R.string.getItemsEndpoint),null,
            { response ->

                Log.e(LOG_TAG,"response: $response")
                ItemsDB.setItems(response)
                success.value = true

            },
            {

                Log.e(LOG_TAG,"error: ${it.printStackTrace()}")
                success.value = false

            })

        requestQueue.add(jsonArrayRequest)
    }

    fun getItem(context:Context, success: MutableLiveData<Boolean>? = null, img:MutableLiveData<String> = MutableLiveData(),itemUrl:String = ""){
        var url = if(itemUrl.isEmpty())
            context.getString(R.string.getItemEndpoint,ItemsDB.currentItem.uid)
        else
            context.getString(R.string.getItemEndpoint,itemUrl)

        val jsonArrayRequest = object:JsonArrayRequest(Method.GET,url,null,
            { response ->

                Log.e(LOG_TAG,"response: $response")
                if(success != null) {
                    ItemsDB.currentMarketItem = gson.fromJson(
                        response.getJSONObject(0).toString(),
                        TarkovMarketItem::class.java
                    )
                    success.value = true
                }else{
                    var item = gson.fromJson(
                        response.getJSONObject(0).toString(),
                        TarkovMarketItem::class.java)
                    img.value = item.imgBig
                }

            },
            {

                Log.e(LOG_TAG,"error: ${it.printStackTrace()}")

                if(success != null) {
                    success.value = false
                }

            }){
            // Providing Request Headers

            override fun getHeaders(): Map<String, String> {
                // Create HashMap of your Headers as the example provided below

                val headers = HashMap<String, String>()
                headers["x-api-key"] = context.getString(R.string.tarkov_api_key)

                return headers
            }
        }

        requestQueue.add(jsonArrayRequest)
    }

    fun getTraderResets(view: View, context:Context, interval:Int){

        val query = "{traderResetTimes{name resetTimestamp}}"

        val json = JSONObject()
        json.put("query",query)

        val reqBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        var req = okhttp3.Request.Builder()
            .addHeader("Content-Type","application/json")
            .addHeader("Accept","application/json")
            .url("https://tarkov-tools.com/graphql")
            .post(reqBody)
            .build()
        val client = OkHttpClient()
        Thread {
            var response = client.newCall(req).execute()
//            Log.e(LOG_TAG, "response " + )
            var name = response.body?.string()
            response.closeQuietly()
            var jsonObject = JSONObject(name!!)
            var dataObject = jsonObject.getJSONObject("data")
            var jsonArray = dataObject.getJSONArray("traderResetTimes")
            for (i in 0 until jsonArray.length()) {
                var obj = jsonArray.getJSONObject(i)
                Log.e(LOG_TAG, "name : " + obj.getString("name"))
                Log.e(LOG_TAG, "reset : " + obj.getString("resetTimestamp"))
                val time = obj.getString("resetTimestamp")
                when(obj.getString("name")){
                    "prapor"-> {
                        TraderResetTimes.praporReset = time
                        TraderResetTimes.setupNotification(context,interval,"prapor")

                    }
                    "therapist"-> {
                        TraderResetTimes.therapistReset = time
                        TraderResetTimes.setupNotification(context,interval,"therapist")

                    }
                    "skier"-> {
                        TraderResetTimes.skierReset = time
                        TraderResetTimes.setupNotification(context,interval,"skier")

                    }
                    "peacekeeper"-> {
                        TraderResetTimes.peacekeeperReset = time
                        TraderResetTimes.setupNotification(context,interval,"peacekeeper")
                    }
                    "mechanic"-> {
                        TraderResetTimes.mechanicReset = time
                        TraderResetTimes.setupNotification(context,interval,"mechanic")

                    }
                    "ragman"-> {
                        TraderResetTimes.ragmanReset = time
                        TraderResetTimes.setupNotification(context,interval,"ragman")
                    }
                    "jaeger"-> {
                        TraderResetTimes.jaegerReset = time
                        TraderResetTimes.setupNotification(context,interval,"jaeger")

                    }
                }
                showSnackBar(view)
            }
        }.start()
    }

    private fun showSnackBar(view: View){
        if(TraderResetTimes.praporEnabled ||
                TraderResetTimes.therapistEnabled ||
                TraderResetTimes.skierEnabled ||
                TraderResetTimes.mechanicEnabled ||
                TraderResetTimes.peacekeeperEnabled ||
                TraderResetTimes.ragmanEnabled ||
                TraderResetTimes.jaegerEnabled)
        Snackbar.make(view,"Notifications set for trader reset",Snackbar.LENGTH_LONG).show()
    }
}