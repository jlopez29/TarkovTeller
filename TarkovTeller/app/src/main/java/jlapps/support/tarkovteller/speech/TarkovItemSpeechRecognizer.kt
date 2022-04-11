package jlapps.support.tarkovteller.speech

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import jlapps.support.tarkovteller.R
import jlapps.support.tarkovteller.data.ItemsDB
import jlapps.support.tarkovteller.data.TarkovItem
import jlapps.support.tarkovteller.databinding.ActivityMainBinding
import jlapps.support.tarkovteller.utilities.ItemProbabilityMatcher
import jlapps.support.tarkovteller.utilities.ItemProbabilityMatcher.checkPossibleMatch
import jlapps.support.tarkovteller.utilities.MicrophoneHelper
import jlapps.support.tarkovteller.utilities.Util

class TarkovItemSpeechRecognizer(val context: Context, binder: ActivityMainBinding) : RecognitionListener {

    var LOG_TAG = "Item Speech Recognition"
    var speech: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    var recognizerIntent: Intent
    private var binding: ActivityMainBinding = binder

    init {
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(context))
        speech.setRecognitionListener(this)
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "US-en")
        recognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
    }

    fun startListening(){
        binding.flTopData.visibility = GONE
        binding.flBottomData.visibility = GONE
        speech.startListening(recognizerIntent)
    }

    fun stopListening(){
        speech.stopListening()
    }

    override fun onError(p0: Int) {
        Log.e(LOG_TAG,"error")
    }

    override fun onResults(results: Bundle?) {
        Log.i(LOG_TAG, "onResults")
        binding.ivMic.setImageResource(R.drawable.ic_mic)
        MicrophoneHelper.isListening = false
        val matches = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (matches != null) {
            var item = ItemProbabilityMatcher.checkExactMatch(matches)
            if (item == null) {
                item = checkPossibleMatch(matches)
                if (item != null)
                    setItemViewData(item)
                else
                    setErrorData(matches[0])
            } else
                setItemViewData(item)
        }
    }

    private fun setItemViewData(item: TarkovItem?){
        Log.e(LOG_TAG,"set item data " + item.toString())
        if(item != null) {
            ItemsDB.currentItem = item
            binding.tvName.text = item.name
            binding.tvName.visibility = VISIBLE
            binding.flTopData.visibility = VISIBLE
            binding.tvPrice.text = "${Util.toCurrency(item.avg24hPrice, item.currency)}"
            binding.tvTrader.text = "${Util.toCurrency(item.buyBack,item.currency)}"
            binding.flBottomData.visibility = VISIBLE
        }
    }

    private fun setErrorData(topResult:String){
        binding.flTopData.visibility = VISIBLE
        binding.tvName.text = ""
        binding.tvName.visibility = GONE
        binding.tvNameHeader.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null)
        binding.tvNameHeader.setTextColor(context.resources.getColor(android.R.color.holo_red_light,null))
        binding.tvNameHeader.text = "Error finding $topResult"
    }


    /*
    *************************************************************
    **************NOT NECESSARY but still implement**************
    *************************************************************
    */

    override fun onPartialResults(p0: Bundle?) {
    }

    override fun onEvent(p0: Int, p1: Bundle?) {
    }

    override fun onReadyForSpeech(p0: Bundle?) {
    }

    override fun onBeginningOfSpeech() {
    }

    override fun onRmsChanged(p0: Float) {
    }

    override fun onBufferReceived(p0: ByteArray?) {
    }

    override fun onEndOfSpeech() {
    }
}

