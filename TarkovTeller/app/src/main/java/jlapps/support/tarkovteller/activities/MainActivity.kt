package jlapps.support.tarkovteller.activities

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import jlapps.support.tarkovteller.R
import jlapps.support.tarkovteller.data.TarkovItem
import jlapps.support.tarkovteller.speech.TarkovItemSpeechRecognizer
import jlapps.support.tarkovteller.data.ItemsDB
import jlapps.support.tarkovteller.utilities.MicrophoneHelper.isListening
import jlapps.support.tarkovteller.databinding.ActivityMainBinding
import jlapps.support.tarkovteller.utilities.Util
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.HashMap
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentTransaction
import androidx.transition.AutoTransition
import androidx.transition.Scene
import androidx.transition.Transition
import androidx.transition.TransitionManager
import jlapps.support.tarkovteller.api.API
import jlapps.support.tarkovteller.fragments.FragmentLoadoutRandomizer
import jlapps.support.tarkovteller.fragments.FragmentTarkovMarketItem
import jlapps.support.tarkovteller.fragments.FragmentTraderResets
import jlapps.support.tarkovteller.service.TraderReset
import jlapps.support.tarkovteller.utilities.LoadingAnimation
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Build
import android.os.StrictMode
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import jlapps.support.tarkovteller.BuildConfig
import jlapps.support.tarkovteller.data.TarkovDataStore
import jlapps.support.tarkovteller.data.TraderResetTimes
import jlapps.support.tarkovteller.data.TraderResetTimes.traderResetInterval
import jlapps.support.tarkovteller.receivers.TraderResetReceiver
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import java.lang.IllegalArgumentException


//todo
//bullet search
//randomize loadout
//gun priority

class MainActivity : AppCompatActivity(){
    var LOG_TAG = "Main"
    private lateinit var binding: ActivityMainBinding
    private lateinit var itemSpeechRecognizer: TarkovItemSpeechRecognizer
    private val speechPermission = 100
    private val internetPermission = 101
    private lateinit var textColor:ColorStateList
    private lateinit var itemsLoaded:MutableLiveData<Boolean>
    private lateinit var itemLoaded:MutableLiveData<Boolean>
    private lateinit var loading:LoadingAnimation
    private lateinit var traderResetReceiver: TraderResetReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_TarkovTeller)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        supportActionBar!!.hide()
        setContentView(view)

        initialize(view)

        traderResetReceiver = TraderResetReceiver()

    }

    private fun initialize(view:View){


        textColor= binding.tvNameHeader.textColors
        itemSpeechRecognizer = TarkovItemSpeechRecognizer(this,binding)

        loading = LoadingAnimation(this,binding.includeLoading.rlLoading)
        ActivityCompat.requestPermissions(this@MainActivity,
            arrayOf(Manifest.permission.INTERNET),
            internetPermission)

//        binding.includeLoading.rlLoading.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
//                override fun onGlobalLayout() {
//                    binding.includeLoading.rlLoading.getViewTreeObserver().removeOnGlobalLayoutListener(this)
//                    loading.animateLinearReveal(binding.includeLoading.ivEftLogo,"right")
//                }
//            })
        if(BuildConfig.DEBUG)
            StrictMode.enableDefaults()

        API.init(this)

        setupListeners()

        lifecycleScope.launch {
            whenCreated {
                TraderResetTimes.getResetTimes(view.context)
                traderResetInterval = TarkovDataStore.getTraderInterval(view.context).first()
                Log.e(LOG_TAG,"interval $traderResetInterval")
            }

            API.getTraderResets(view,view.context, traderResetInterval)
        }

        //Local data from spreadsheet
        loadItemData()


    }

    private fun setupListeners(){
        binding.tvNameHeader.setOnClickListener {
            loading.animateLinearReveal(binding.includeLoading.ivEftLogo,"right")
            API.getItem(this,itemLoaded)
        }
        binding.ivMic.setOnClickListener {
            if (isListening) {
                binding.ivMic.setImageResource(R.drawable.ic_mic)
                isListening = false
                itemSpeechRecognizer.stopListening()
            }
            else {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    speechPermission)
                binding.flTopData.visibility = GONE
                binding.ivMic.setImageResource(R.drawable.ic_mic_on)
                isListening = true
                binding.tvNameHeader.setTextColor(textColor)
                binding.tvNameHeader.text = getText(R.string.item)
                binding.tvNameHeader.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(resources,R.drawable.ic_link,null),null,null,null)
            }
        }

        binding.fabRandomizer.setOnClickListener {
            showFragment(supportFragmentManager.beginTransaction(),"rando",Bundle())
            binding.fabMenu.performClick()
        }

        binding.fabTraders.setOnClickListener {
            showFragment(supportFragmentManager.beginTransaction(),"traders",Bundle())
            binding.fabMenu.performClick()
        }
        var expanded = false
        binding.fabMenu.setOnClickListener{

            binding.fabTraders.visibility = VISIBLE
            binding.fabRandomizer.visibility = VISIBLE
//            TransitionManager.beginDelayedTransition(binding.containerMain)
            var constraintSet = ConstraintSet()
            constraintSet.clone(binding.containerMain)
            if(!expanded) {
                constraintSet.connect(
                    binding.fabTraders.id,
                    ConstraintSet.END,
                    binding.fabMenu.id,
                    ConstraintSet.START,
                    32
                )
                constraintSet.connect(
                    binding.fabRandomizer.id,
                    ConstraintSet.BOTTOM,
                    binding.fabMenu.id,
                    ConstraintSet.TOP,
                    32
                )
                binding.fabMenu.setImageDrawable(resources.getDrawable(R.drawable.ic_close,null))
                expanded = true
            }
            else{
                constraintSet.connect(
                    binding.fabTraders.id,
                    ConstraintSet.END,
                    binding.fabMenu.id,
                    ConstraintSet.END,
                    0
                )
                constraintSet.connect(
                    binding.fabRandomizer.id,
                    ConstraintSet.BOTTOM,
                    binding.fabMenu.id,
                    ConstraintSet.BOTTOM,
                    0
                )
                binding.fabMenu.setImageDrawable(resources.getDrawable(R.drawable.ic_menu,null))
                expanded = false

            }

            constraintSet.applyTo(binding.containerMain)
            val autoTransition = AutoTransition()
            autoTransition.addListener(object: Transition.TransitionListener{
                override fun onTransitionStart(transition: Transition) {

                }

                override fun onTransitionEnd(transition: Transition) {
                    if(!expanded){
                        binding.fabTraders.visibility = GONE
                        binding.fabRandomizer.visibility = GONE
                    }
                }

                override fun onTransitionCancel(transition: Transition) {
                }

                override fun onTransitionPause(transition: Transition) {
                }

                override fun onTransitionResume(transition: Transition) {
                }

            })

            TransitionManager.go(Scene(binding.containerMain),autoTransition)
        }

        itemsLoaded = MutableLiveData<Boolean>()
        itemLoaded = MutableLiveData<Boolean>()

        itemsLoaded.observe(this, {
            if(it != null){
                if(it){
                    Log.e(LOG_TAG,"data loaded")
                }
                else
                    Log.e(LOG_TAG,"error loading data")

                loading.stop()
            }
        })
        itemLoaded.observe(this, {
            if(it != null){
                if(it){
                    Log.e(LOG_TAG,"data loaded")
                }
                else
                    Log.e(LOG_TAG,"error loading data")

                loading.stop()

                showFragment(supportFragmentManager.beginTransaction(),"item",Bundle())
            }
        })

    }

    private fun loadItemData(){
        var jsonFile = Util.getJsonFromAssets(this,"data_items.json")
//        Log.e("data", jsonFile.toString())
        val gson = Gson()
        val itemType: Type = object : TypeToken<List<TarkovItem?>?>() {}.getType()

        val items: List<TarkovItem> = gson.fromJson(jsonFile, itemType)
        var itemMap = HashMap<String, TarkovItem>()
        for(item in items){
            val name = item.name.lowercase(Locale.getDefault()).trim()
            itemMap[name] = item
//            Log.e(LOG_TAG,"item $name")
        }
        ItemsDB.items = itemMap
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            speechPermission ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager
                    .PERMISSION_GRANTED) {
                itemSpeechRecognizer.startListening()
                } else {
                    Toast.makeText(this@MainActivity, "Permission Denied!",Toast.LENGTH_SHORT).show()
                }
            internetPermission ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
//                    API.getAllItems(this,itemsLoaded)
                } else {
                    Toast.makeText(this@MainActivity, "Permission Denied!",Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        Log.v(LOG_TAG, intent.action.toString())
        val bundleExtras = intent.extras
        if (bundleExtras != null) {
            for (key in bundleExtras.keySet()) {
                Log.v(LOG_TAG, key)
            }
        }
        if (intent.action.toString().equals("android.intent.action.MAIN"))
        {
            return
        }else if (intent.hasExtra("find_price"))
        {
            var item = intent.getStringExtra("item_name").toString()
            Log.d(LOG_TAG, "Looking up $item")

        }
    }

    private fun showFragment(fragTransaction: FragmentTransaction, frag:String, bundle: Bundle){
        fragTransaction.setCustomAnimations(
            R.anim.fade_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.fade_out
        )
        fragTransaction.setReorderingAllowed(true)
        fragTransaction.addToBackStack(frag)
        when(frag){
            "item"->fragTransaction.replace(R.id.container_main,FragmentTarkovMarketItem()).commit()
            "rando"->fragTransaction.replace(R.id.container_main,FragmentLoadoutRandomizer()).commit()
            "traders"->fragTransaction.replace(R.id.container_main, FragmentTraderResets()).commit()
        }

    }
}