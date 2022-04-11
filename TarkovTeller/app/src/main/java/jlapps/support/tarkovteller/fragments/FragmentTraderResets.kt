package jlapps.support.tarkovteller.fragments

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import jlapps.support.tarkovteller.R
import jlapps.support.tarkovteller.data.TarkovDataStore
import jlapps.support.tarkovteller.data.TraderResetTimes
import jlapps.support.tarkovteller.databinding.FragTraderResetsBinding
import jlapps.support.tarkovteller.utilities.Util
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import android.app.Dialog
import android.content.DialogInterface
import android.widget.Button

import android.widget.NumberPicker

import jlapps.support.tarkovteller.activities.MainActivity
import jlapps.support.tarkovteller.api.API
import jlapps.support.tarkovteller.data.TraderResetTimes.traderResetInterval
import kotlinx.coroutines.flow.first


class FragmentTraderResets  : Fragment(R.layout.frag_trader_resets){
    private var _binding: FragTraderResetsBinding? = null
    private val binding get() = _binding!!
    private val LOG_TAG = "Trader Resets"
    lateinit var d: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragTraderResetsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }
    private fun initViews(view:View){
        createCounters()
        lifecycleScope.launch {
            initNotifIcons()
        }
        initNotifListeners()

        binding.fabResetTime.setOnClickListener{
            show()
        }

    }
    private fun createCounters(){
        createCounter(convertTime(TraderResetTimes.praporReset),binding.tvPraporReset,"Prapor").start()
        createCounter(convertTime(TraderResetTimes.therapistReset),binding.tvTherapistReset,"Therapist").start()
        createCounter(convertTime(TraderResetTimes.skierReset),binding.tvSkierReset,"Skier").start()
        createCounter(convertTime(TraderResetTimes.mechanicReset),binding.tvMechanicReset,"Mechanic").start()
        createCounter(convertTime(TraderResetTimes.peacekeeperReset),binding.tvPeacekeeperReset,"Peacekeeper").start()
        createCounter(convertTime(TraderResetTimes.ragmanReset),binding.tvRagmanReset,"Ragman").start()
        createCounter(convertTime(TraderResetTimes.jaegerReset),binding.tvJaegerReset,"Jaeger").start()
    }

    private suspend fun initNotifIcons(){
        coroutineScope {
            launch {
                TarkovDataStore.getTraderReset(requireContext(),"prapor").catch { e->e.printStackTrace() }
                .collect {
                    if(it){
                        binding.ivNotifPrapor.setImageResource(R.drawable.ic_notif_active)
                        binding.ivNotifPrapor.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(resources.getColor(android.R.color.holo_green_light,null), BlendModeCompat.SRC_ATOP)

                    }else{
                        binding.ivNotifPrapor.setImageResource(R.drawable.ic_notif_off)
                        binding.ivNotifPrapor.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(resources.getColor(android.R.color.holo_red_light,null), BlendModeCompat.SRC_ATOP)
                    }
                    TraderResetTimes.setupNotification(requireContext(),TarkovDataStore.getTraderInterval(requireContext()).first(),"prapor")
                }
            }
            launch{
                TarkovDataStore.getTraderReset(requireContext(),"therapist").catch { e->e.printStackTrace() }
                .collect {
                    if(it){
                        binding.ivNotifTherapist.setImageResource(R.drawable.ic_notif_active)
                        binding.ivNotifTherapist.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(resources.getColor(android.R.color.holo_green_light,null), BlendModeCompat.SRC_ATOP)
                    }else{
                        binding.ivNotifTherapist.setImageResource(R.drawable.ic_notif_off)
                        binding.ivNotifTherapist.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(resources.getColor(android.R.color.holo_red_light,null), BlendModeCompat.SRC_ATOP)
                    }
                }
                TraderResetTimes.setupNotification(requireContext(),TarkovDataStore.getTraderInterval(requireContext()).first(),"therapist")
            }

            launch{
                TarkovDataStore.getTraderReset(requireContext(),"skier").catch { e->e.printStackTrace() }
                .collect {
                    if(it){
                        binding.ivNotifSkier.setImageResource(R.drawable.ic_notif_active)
                        binding.ivNotifSkier.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(resources.getColor(android.R.color.holo_green_light,null), BlendModeCompat.SRC_ATOP)
                    }else{
                        binding.ivNotifSkier.setImageResource(R.drawable.ic_notif_off)
                        binding.ivNotifSkier.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(resources.getColor(android.R.color.holo_red_light,null), BlendModeCompat.SRC_ATOP)
                    }
                }
                TraderResetTimes.setupNotification(requireContext(),TarkovDataStore.getTraderInterval(requireContext()).first(),"skier")
            }
            launch {
                TarkovDataStore.getTraderReset(requireContext(),"peacekeeper").catch { e->e.printStackTrace() }
                .collect {
                    if(it){
                        binding.ivNotifPeacekeeper.setImageResource(R.drawable.ic_notif_active)
                        binding.ivNotifPeacekeeper.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(resources.getColor(android.R.color.holo_green_light,null), BlendModeCompat.SRC_ATOP)
                    }else{
                        binding.ivNotifPeacekeeper.setImageResource(R.drawable.ic_notif_off)
                        binding.ivNotifPeacekeeper.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(resources.getColor(android.R.color.holo_red_light,null), BlendModeCompat.SRC_ATOP)
                    }
                    TraderResetTimes.setupNotification(requireContext(),TarkovDataStore.getTraderInterval(requireContext()).first(),"peacekeeper")
                }
            }
            launch {
                TarkovDataStore.getTraderReset(requireContext(),"mechanic").catch { e->e.printStackTrace() }
                .collect {
                    if(it){
                        binding.ivNotifMechanic.setImageResource(R.drawable.ic_notif_active)
                        binding.ivNotifMechanic.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(resources.getColor(android.R.color.holo_green_light,null), BlendModeCompat.SRC_ATOP)
                    }else{
                        binding.ivNotifMechanic.setImageResource(R.drawable.ic_notif_off)
                        binding.ivNotifMechanic.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(resources.getColor(android.R.color.holo_red_light,null), BlendModeCompat.SRC_ATOP)
                    }
                    TraderResetTimes.setupNotification(requireContext(),TarkovDataStore.getTraderInterval(requireContext()).first(),"mechanic")
                }
            }

            launch {
                TarkovDataStore.getTraderReset(requireContext(),"ragman").catch { e->e.printStackTrace() }
                .collect {
                    if(it){
                        binding.ivNotifRagman.setImageResource(R.drawable.ic_notif_active)
                        binding.ivNotifRagman.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(resources.getColor(android.R.color.holo_green_light,null), BlendModeCompat.SRC_ATOP)
                    }else{
                        binding.ivNotifRagman.setImageResource(R.drawable.ic_notif_off)
                        binding.ivNotifRagman.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(resources.getColor(android.R.color.holo_red_light,null), BlendModeCompat.SRC_ATOP)
                    }
                    TraderResetTimes.setupNotification(requireContext(),TarkovDataStore.getTraderInterval(requireContext()).first(),"ragman")
                }
            }
            launch {
                TarkovDataStore.getTraderReset(requireContext(),"jaeger").catch { e->e.printStackTrace() }
                    .collect {
                        if(it){
                            binding.ivNotifJaeger.setImageResource(R.drawable.ic_notif_active)
                            binding.ivNotifJaeger.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(resources.getColor(android.R.color.holo_green_light,null), BlendModeCompat.SRC_ATOP)
                        }else{
                            binding.ivNotifJaeger.setImageResource(R.drawable.ic_notif_off)
                            binding.ivNotifJaeger.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(resources.getColor(android.R.color.holo_red_light,null), BlendModeCompat.SRC_ATOP)
                        }
                        TraderResetTimes.setupNotification(requireContext(),TarkovDataStore.getTraderInterval(requireContext()).first(),"jaeger")
                    }
            }
        }
    }

    private fun initNotifListeners(){

        binding.ivNotifPrapor.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                TarkovDataStore.saveTraderReset(requireView().context,"prapor")
            }
        }
        binding.ivNotifTherapist.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                TarkovDataStore.saveTraderReset(requireView().context,"therapist")
            }
        }
        binding.ivNotifSkier.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                TarkovDataStore.saveTraderReset(requireView().context,"skier")
            }
        }
        binding.ivNotifPeacekeeper.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                TarkovDataStore.saveTraderReset(requireView().context,"peacekeeper")
            }
        }
        binding.ivNotifMechanic.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                TarkovDataStore.saveTraderReset(requireView().context,"mechanic")
            }
        }
        binding.ivNotifRagman.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                TarkovDataStore.saveTraderReset(requireView().context,"ragman")
            }
        }
        binding.ivNotifJaeger.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                TarkovDataStore.saveTraderReset(requireView().context,"jaeger")
            }
        }

    }
    private fun createCounter(time:Long,txt_timeleft:TextView,trader:String):CountDownTimer{
        return object : CountDownTimer(time, 1000) {

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
                txt_timeleft.text = time
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onFinish() {
                txt_timeleft.text = "Restocked"
            }
        }
    }

    private fun convertTime(time:String):Long{
        if(time.isEmpty())
            return (7200000)
        val dateStr = time
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH)
        df.timeZone = TimeZone.getTimeZone("UTC")
        val date: Date = df.parse(dateStr)
        df.timeZone = TimeZone.getDefault()
        val formattedDate: String = df.format(date)

        val diff: Long = (date.time - 3000) - Date().time
        var time = TimeUnit.MILLISECONDS.toSeconds(diff)
        val hours: Long = time / 3600
        val minutes: Long = time % 3600 / 60
        val seconds: Long = time % 3600 % 60

//        Log.e(LOG_TAG,"date " + formattedDate)
        Log.e(Util.LOG_TAG,"h/m/s: $hours:$minutes:$seconds")
        return TimeUnit.MILLISECONDS.toMillis(diff)
    }

    fun show() {
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_reset_timer,null)
        d = Dialog(requireActivity())
        d.setContentView(dialogView)
        val numberPicker = dialogView.findViewById<NumberPicker>(R.id.np_reset_time)
        numberPicker.maxValue = 6
        numberPicker.minValue = 1
        numberPicker.value = traderResetInterval / 5
        numberPicker.displayedValues = arrayOf("5","10","15","20","25","30")
        numberPicker.wrapSelectorWheel = false

        val accept = dialogView.findViewById<Button>(R.id.btn_reset_accept)
        val cancel = dialogView.findViewById<Button>(R.id.btn_reset_cancel)

        accept.setOnClickListener{
            Log.e(LOG_TAG,"val ${numberPicker.value}")
            var value = numberPicker.value * 5
            viewLifecycleOwner.lifecycleScope.launch {
                TarkovDataStore.saveTraderInterval(requireView().context,value.toString())
            }
            d.cancel()
        }
        cancel.setOnClickListener {
            d.cancel()
        }
        d.show()
    }
}