package jlapps.support.tarkovteller.data

import org.json.JSONArray

object ItemsDB {
    var items = HashMap<String, TarkovItem>()
    lateinit var currentItem:TarkovItem
    lateinit var currentMarketItem:TarkovMarketItem
    fun setItems(array: JSONArray){
        for(i in 0 until array.length()){

            items[array.getJSONObject(i).getString("name")] = TarkovItem(
                array.getJSONObject(i).getString("_id"),
                array.getJSONObject(i).getString("name"),
                array.getJSONObject(i).getString("price"),
                array.getJSONObject(i).getString("avg24hPrice"),
                array.getJSONObject(i).getString("avg7dPrice"),
                array.getJSONObject(i).getString("trader"),
                array.getJSONObject(i).getString("buyBack"),
                array.getJSONObject(i).getString("currency")
            )
        }
    }
}