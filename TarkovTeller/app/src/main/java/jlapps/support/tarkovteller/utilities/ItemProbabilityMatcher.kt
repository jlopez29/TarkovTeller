package jlapps.support.tarkovteller.utilities

import android.util.Log
import jlapps.support.tarkovteller.data.ItemsDB
import jlapps.support.tarkovteller.data.TarkovItem
import java.util.ArrayList

object ItemProbabilityMatcher {
    var LOG_TAG = "Item Probability Matcher"
     fun checkExactMatch(matches: ArrayList<String>):TarkovItem?{

        for (result in matches) {
            Log.e(LOG_TAG, "result " + result.lowercase().trim())
            if(ItemsDB.items.containsKey(result.lowercase().trim())){
                val item = ItemsDB.items[result]
                return item
            }
        }
        return null
    }

     fun checkPossibleMatch(matches: ArrayList<String>):TarkovItem?{
        val closestMatches = findClosestMatches(matches)

        val probabilityMatches = findProbableMatches(closestMatches,matches)
        val probMatch = getFinalProbabilityMatch(probabilityMatches)

        return ItemsDB.items[probMatch.second]

    }

    private fun findClosestMatches(matches: ArrayList<String>): ArrayList<Pair<Int, String>> {
        val closestMatches = arrayListOf<Pair<Int,String>>()
        var mostMatching = 0

        for (result in matches) {
            for (item in ItemsDB.items.keys.toList()) {
                val matched = Util.matcher(result.lowercase().trim(), item.trim())
                if (matched >= mostMatching && matched != 0) {
                    Log.e(LOG_TAG, "new closest match $item")
                    mostMatching = matched
                    if(!closestMatches.contains(Pair(matched,item)))
                        closestMatches.add(Pair(matched,item))
                }
            }
        }

        Log.e(LOG_TAG, "matches $closestMatches")
        return closestMatches
    }

    private fun findProbableMatches(closestMatches: ArrayList<Pair<Int, String>>, matches: ArrayList<String>): ArrayList<Pair<Double, String>> {
        val probabilityMatches = ArrayList<Pair<Double,String>>()

        for (result in matches) {
            Log.e(LOG_TAG, "result " + result.lowercase().trim())
            probabilityMatches.add(Util.probabilityMatch(result, closestMatches))
        }
        return probabilityMatches
    }

    private fun getFinalProbabilityMatch(probabilityMatches: ArrayList<Pair<Double, String>>):Pair<Double,String>{
        var probableMatch = Pair(0.0,"")
        for(match in probabilityMatches){
            if(match.first > probableMatch.first)
                probableMatch = match
        }
        Log.e(LOG_TAG,"probability match " + probableMatch.second + " w/ : " + probableMatch.first+ " %")
        return probableMatch
    }

}