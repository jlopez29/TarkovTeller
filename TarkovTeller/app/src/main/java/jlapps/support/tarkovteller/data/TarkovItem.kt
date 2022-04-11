package jlapps.support.tarkovteller.data

import com.google.gson.annotations.SerializedName
import jlapps.support.tarkovteller.utilities.Util

data class TarkovItem(
    @SerializedName("_id")
    val uid: String,
    val name: String,
    val price: String,
    val avg24hPrice: String,
    val avg7dPrice: String,
    val trader: String,
    val buyBack: String,
    val currency: String
){
    override fun toString(): String =
            "\nuid: "+ uid + "\n"+
            "name: "+name + "\n"+
            "price: "+ Util.toCurrency(price,currency) + "\n"+
            "avg24Price: "+ Util.toCurrency(avg24hPrice, currency) + "\n"+
            "avg7Price: "+ Util.toCurrency(avg7dPrice, currency) + "\n"+
            "trader: "+trader + "\n"+
            "buyBack: "+buyBack + "\n"+
            "currency: "+currency + "\n"

}