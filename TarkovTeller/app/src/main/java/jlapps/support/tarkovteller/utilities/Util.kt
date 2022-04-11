package jlapps.support.tarkovteller.utilities

import android.content.Context
import android.util.Log
import android.util.TypedValue
import androidx.annotation.AttrRes
import java.io.IOException
import java.io.InputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import android.content.ContentResolver
import android.content.res.Resources
import android.net.Uri

import androidx.annotation.AnyRes


object Util {
    val LOG_TAG = "UTIL"
    fun getJsonFromAssets(context: Context,fileName:String): String? {
        val jsonString: String = try {
            val inputStream: InputStream = context.getAssets().open(fileName)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, charset("UTF-8"))
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return jsonString
    }
    fun matcher(stringOne: String, stringTwo: String): Int {
        // degenerate cases
        if (stringOne == stringTwo)  return stringOne.length

        val sOne = stringOne.split(" ")
        val sTwo = stringTwo.split(" ")
        var matches = 0
        for(part in sOne){
            for(piece in sTwo){
                if(part == piece)
                    matches++
            }
        }
        return matches
    }
    fun probabilityMatch(input:String,list:ArrayList<Pair<Int,String>>):Pair<Double,String>{
        var match = ""
        var highestPercent = 0.0
        var inputParts = input.split(" ").size
        for(item in list){
            var itemParts = item.second.split(" ")
            var initialPercent = item.first/itemParts.size.toDouble()
            var percent = initialPercent * item.first/inputParts
            if(percent > highestPercent){
                match = item.second
                highestPercent = percent
            }
        }
        return Pair(highestPercent,match)
    }
    fun toCurrency(amount:String,currency:String):String{
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 0
        var convert = numberFormat.format(amount.toDouble())
        when (currency) {
            "dollars" -> convert = "$ $convert"
            "roubles" -> convert = "$convert \u20BD"
            "euros" -> convert = "$convert \u20AC"
        }
        return convert
    }

    private fun resolveThemeAttr(context: Context, @AttrRes attrRes: Int): TypedValue {
        val theme = context.theme
        val typedValue = TypedValue()
        theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue
    }


    fun convertTime(time:String,warning:Long):Long{
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH)
        df.timeZone = TimeZone.getTimeZone("UTC")
        val date: Date = df.parse(time)
        df.timeZone = TimeZone.getDefault()

        val formattedDate = df.format(date)
        Log.e(LOG_TAG,"reset time: $formattedDate")
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MINUTE,-(TimeUnit.MILLISECONDS.toMinutes(warning).toInt()))
        val formattedWarningDate = df.format(calendar.time)

        Log.e(LOG_TAG,"reset warning time: $formattedWarningDate")

        val diff: Long = (date.time - warning) - Date().time
        var convertedTime = TimeUnit.MILLISECONDS.toSeconds(date.time - Date().time)
        val hours: Long = convertedTime / 3600
        val minutes: Long = convertedTime % 3600 / 60
        val seconds: Long = convertedTime % 3600 % 60

        Log.e(LOG_TAG,"diff $diff")
        Log.e(LOG_TAG,"h/m/s: $hours:$minutes:$seconds")

        Log.e(LOG_TAG,"ms: ${date.time - warning}")
        return (date.time - warning)
    }

    @Throws(Resources.NotFoundException::class)
    fun getUriToResource(context: Context, @AnyRes resId: Int): Uri? {
        val res: Resources = context.resources
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + res.getResourcePackageName(resId)
                    + '/' + res.getResourceTypeName(resId)
                    + '/' + res.getResourceEntryName(resId)
        )
    }
}