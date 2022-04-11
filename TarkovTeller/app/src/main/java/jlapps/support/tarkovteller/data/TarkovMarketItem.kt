package jlapps.support.tarkovteller.data

import jlapps.support.tarkovteller.utilities.Util

data class TarkovMarketItem(
    val uid: String,
    val name: String,
    val tags: ArrayList<String>,
    val shortName: String,
    val price: Int,
    val basePrice: Int,
    val avg24hPrice: Int,
    val avg7daysPrice: Int,
    val traderName: String,
    val traderPrice: Int,
    val traderPriceCur: String,
    val updated: String,
    val slots: Int,
    val diff24h: Float,
    val diff7days: Float,
    val icon: String,
    val link: String,
    val wikiLink: String,
    val img: String,
    val imgBig: String,
    val bsgId: String,
    val isFunctional: Boolean,
    val reference:String
){
    override fun toString(): String =
            "uid: "+ uid + "\n"+
            "name: "+name + "\n"+
            "price: "+ Util.toCurrency(price.toString(),getCurrency()) + "\n"+
            "avg24Price: "+ Util.toCurrency(avg24hPrice.toString(), getCurrency()) + "\n"+
            "avg7Price: "+ Util.toCurrency(avg7daysPrice.toString(), getCurrency()) + "\n"+
            "trader: "+traderName + "\n"+
            "buyBack: "+traderPrice + "\n"+
            "currency: "+getCurrency() + "\n"

    fun getCurrency():String{
        when(traderPriceCur) {
            "₽" -> return "roubles"
            "$" -> return "dollars"
            "€" -> return "euros"


        }
        return "roubles"
    }
}