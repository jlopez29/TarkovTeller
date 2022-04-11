package jlapps.support.tarkovteller.utilities

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import jlapps.support.tarkovteller.data.ItemsDB
import jlapps.support.tarkovteller.data.TarkovCategories.armors
import jlapps.support.tarkovteller.data.TarkovCategories.backpacks
import jlapps.support.tarkovteller.data.TarkovCategories.eyewear
import jlapps.support.tarkovteller.data.TarkovCategories.faceCovers
import jlapps.support.tarkovteller.data.TarkovCategories.headsets
import jlapps.support.tarkovteller.data.TarkovCategories.headwear
import jlapps.support.tarkovteller.data.TarkovCategories.rigs
import jlapps.support.tarkovteller.data.TarkovCategories.weapons
import jlapps.support.tarkovteller.data.TarkovItem
import org.json.JSONObject
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

class LoadoutRandomizer(context: Context) {
    private val LOG_TAG = "Loadout Randomizer"
    lateinit var headset:TarkovItem
    lateinit var head:TarkovItem
    lateinit var faceCover:TarkovItem
    lateinit var rig:TarkovItem
    lateinit var armor:TarkovItem
    lateinit var eyes:TarkovItem
    lateinit var weapon:TarkovItem
    lateinit var backpack:TarkovItem

    val variations = JSONObject()

    init{
        var jsonFile = Util.getJsonFromAssets(context,"data_armor_vests.json")
//        Log.e("data", jsonFile.toString())
        val gson = Gson()
        val itemType: Type = object : TypeToken<ArrayList<JsonObject?>?>() {}.getType()

        armors = gson.fromJson(jsonFile, itemType)
//        Log.e(LOG_TAG, "armors: $armors")

        jsonFile = Util.getJsonFromAssets(context,"data_backpacks.json")
        backpacks = gson.fromJson(jsonFile,itemType)
//        Log.e(LOG_TAG, "backpacks: $backpacks")

        jsonFile = Util.getJsonFromAssets(context,"data_chest_rigs.json")
        rigs = gson.fromJson(jsonFile,itemType)
//        Log.e(LOG_TAG, "rigs: $rigs")

        jsonFile = Util.getJsonFromAssets(context,"data_eyewear.json")
        eyewear = gson.fromJson(jsonFile,itemType)
//        Log.e(LOG_TAG, "eyewear: $eyewear")

        jsonFile = Util.getJsonFromAssets(context,"data_face_cover.json")
        faceCovers = gson.fromJson(jsonFile,itemType)
//        Log.e(LOG_TAG, "faceCovers: $faceCovers")

        jsonFile = Util.getJsonFromAssets(context,"data_headsets.json")
        headsets = gson.fromJson(jsonFile,itemType)
//        Log.e(LOG_TAG, "headsets: $headsets")

        jsonFile = Util.getJsonFromAssets(context,"data_headwear.json")
        headwear = gson.fromJson(jsonFile,itemType)
//        Log.e(LOG_TAG, "headwear: $headwear")

        jsonFile = Util.getJsonFromAssets(context,"data_weapons.json")
        weapons = gson.fromJson(jsonFile,itemType)
//        Log.e(LOG_TAG, "weapons: $weapons")

        variations.put("usec baseball cap","usec baseball cap (tan)")
        variations.put("team wendy exfil ballistic helmet",Pair("team wendy exfil ballistic helmet (black)","team wendy exfil ballistic helmet (tan)"))
        variations.put("ops core fast mt super high cut helmet",Pair("ops core fast mt super high cut helmet (coyote brown)","ops core fast mt super high cut helmet (black)"))
        variations.put("highcom striker achhc iiia helmet",Pair("highcom striker achhc iiia helmet (olive)","highcom striker achhc iiia helmet (black)"))
        variations.put("highcom striker ulach iiia helmet",Pair("highcom striker ulach iiia helmet (tan)","highcom striker ulach iiia helmet (black)"))
        variations.put("azimut ss zhuk chest harness",Pair("azimut ss zhuk chest harness (black)","azimut ss zhuk chest harness (surpat)"))
        variations.put("blackhawk! commando chest harness",Pair("blackhawk! commando chest harness (coyote tan)","blackhawk! commando chest harness (black)"))
        variations.put("6b13 assault armor",Pair("6b13 assault armor (digital flora)","6b13 assault armor (flora)"))
        variations.put("hazard 4 drawbridge backpack","hazard 4 drawbridge backpack (coyote tan)")
        variations.put("hazard 4 takedown sling backpack",Pair("hazard 4 takedown sling backpack (multicam)","hazard 4 takedown sling backpack (black)"))
    }
    fun randomizeLoadout(){
        getRandomItem("headset")
        getRandomItem("headwear")
        getRandomItem("faceCover")
        getRandomItem("rig")
        getRandomItem("armor")
        getRandomItem("eyewear")
        getRandomItem("weapon")
        getRandomItem("backpack")

        Log.e(LOG_TAG,"\n ****** LOADOUT *******\n")
        Log.e(LOG_TAG,headset.name)
        Log.e(LOG_TAG,head.name)
        Log.e(LOG_TAG,eyes.name)
        Log.e(LOG_TAG,rig.name)
        Log.e(LOG_TAG,armor.name)
        Log.e(LOG_TAG,faceCover.name)
        Log.e(LOG_TAG,weapon.name)
        Log.e(LOG_TAG,backpack.name)
    }

    fun getRandomItem(key:String){
        when(key){
            "headset"->{
                val headsetObj= headsets[(0 until headsets.size).random()].get("name").asString
                Log.e(LOG_TAG,"loadout headset " + cleanName(headsetObj))
                val loadoutHeadset = ItemsDB.items[cleanName(headsetObj)]
                if(loadoutHeadset != null)
                    headset = loadoutHeadset
                else
                    Log.e(LOG_TAG,"null headset")
            }
            "headwear"->{
                val headwearObj = cleanName(headwear[(0 until headwear.size).random()].get("name").asString.trim())
                Log.e(LOG_TAG, "loadout headwear $headwearObj")
                var loadoutHeadwear = ItemsDB.items[headwearObj]
                if(loadoutHeadwear != null)
                    head = loadoutHeadwear
                else {
                    loadoutHeadwear = ItemsDB.items[nameVariation(headwearObj)]
                    if(loadoutHeadwear != null)
                        head = loadoutHeadwear
                    else
                        Log.e(LOG_TAG,"null head")
                }
            }
            "faceCover"->{
                val faceCoverObj = faceCovers[(0 until faceCovers.size).random()].get("name").asString
                Log.e(LOG_TAG,"loadout faceCovers " + cleanName(faceCoverObj))
                val loadoutFaceCover = ItemsDB.items[cleanName(faceCoverObj)]
                if(loadoutFaceCover != null)
                    faceCover = loadoutFaceCover
                else
                    Log.e(LOG_TAG,"null face")
            }
            "rig"->{
                val rigObj = cleanName(rigs[(0 until rigs.size).random()].get("name").asString)
                Log.e(LOG_TAG, "loadout rig $rigObj")
                var loadoutRig = ItemsDB.items[rigObj]
                if(loadoutRig != null)
                    rig = loadoutRig
                else {
                    loadoutRig = ItemsDB.items[nameVariation(rigObj)]
                    if(loadoutRig != null)
                        rig = loadoutRig
                    else
                        Log.e(LOG_TAG,"null rig")
                }
            }
            "armor"->{
                val armorObj = armors[(0 until armors.size).random()].get("name").asString
                Log.e(LOG_TAG,"loadout armor " + cleanName(armorObj))
                val loadoutArmor = ItemsDB.items[cleanName(armorObj)]
                if(loadoutArmor != null)
                    armor = loadoutArmor
                else
                    Log.e(LOG_TAG,"null armor")
            }
            "eyewear"->{
                val eyewearObj = eyewear[(0 until eyewear.size).random()].get("name").asString
                Log.e(LOG_TAG,"loadout eyewear " + cleanName(eyewearObj))
                val loadoutEyewear = ItemsDB.items[cleanName(eyewearObj)]
                if(loadoutEyewear != null)
                    eyes = loadoutEyewear
                else
                    Log.e(LOG_TAG,"null eyes")
            }
            "weapon"->{
                val weaponObj = weapons[(0 until weapons.size).random()].get("name").asString
                Log.e(LOG_TAG,"loadout weapon " + cleanName(weaponObj))
                val loadoutWeapon = ItemsDB.items[cleanName(weaponObj)]
                if(loadoutWeapon != null)
                    weapon = loadoutWeapon
                else
                    Log.e(LOG_TAG,"null weapon")
            }
            "backpack"->{
                var backpackObj = backpacks[(0 until backpacks.size).random()].get("name").asString
                Log.e(LOG_TAG,"loadout backpack " + cleanName(backpackObj))
                var loadoutBackpack = ItemsDB.items[cleanName(backpackObj)]
                if(loadoutBackpack != null)
                    backpack = loadoutBackpack
                else {
                    loadoutBackpack = ItemsDB.items[nameVariation(backpackObj)]
                    if(loadoutBackpack != null)
                        backpack = loadoutBackpack
                    else
                        Log.e(LOG_TAG,"null backpack")
                }


            }

        }
    }

     fun testItems(){
//         for(heads in 0 until headwear.size){
//             var headwearObj = cleanName(headwear[heads].get("name").asString.trim())
//             Log.e(LOG_TAG, "loadout headwear $headwearObj")
//             var loadoutHeadwear = ItemsDB.items[cleanName(headwearObj)]
//             if(loadoutHeadwear == null) {
//                 headwearObj = nameVariation(headwearObj)
//                 loadoutHeadwear = ItemsDB.items[headwearObj]
//                 Log.e(LOG_TAG, "null - variation - loadout headwear $headwearObj")
//             }
//         }

//         for(face in 0 until faceCovers.size){
//             val faceCoverObj = faceCovers[face].get("name").asString
//             Log.e(LOG_TAG,"loadout faceCovers " + cleanName(faceCoverObj))
//             val loadoutFaceCover = ItemsDB.items[cleanName(faceCoverObj)]
//             if(loadoutFaceCover != null)
//                 faceCover = loadoutFaceCover
//             else
//                 Log.e(LOG_TAG,"null face")
//         }

//         for(rig in 0 until rigs.size){
//             val rigObj = rigs[rig].get("name").asString
//             Log.e(LOG_TAG,"loadout rig " + cleanName(rigObj))
//             val loadoutRig = ItemsDB.items[cleanName(rigObj)]
//             if(loadoutRig == null)
//                 Log.e(LOG_TAG,"null rig")
//         }

//         for(armor in 0 until armors.size){
//             val armorObj = armors[armor].get("name").asString
//             Log.e(LOG_TAG,"loadout armor " + cleanName(armorObj))
//             val loadoutArmor = ItemsDB.items[cleanName(armorObj)]
//             if(loadoutArmor == null)
//                 Log.e(LOG_TAG,"null armor")
//         }

//         for(eye in 0 until eyewear.size){
//             val eyewearObj = eyewear[eye].get("name").asString
//             Log.e(LOG_TAG,"loadout eyewear " + cleanName(eyewearObj))
//             val loadoutEyewear = ItemsDB.items[cleanName(eyewearObj)]
//             if(loadoutEyewear == null)
//                 Log.e(LOG_TAG,"null eyes")
//         }


//         for(wep in 0 until weapons.size){
//             val weaponObj = weapons[wep].get("name").asString
//             Log.e(LOG_TAG,"loadout weapon " + cleanName(weaponObj))
//             val loadoutWeapon = ItemsDB.items[cleanName(weaponObj)]
//             if(loadoutWeapon == null)
//                 Log.e(LOG_TAG,"null weapon")
//         }


//         for(backpack in 0 until backpacks.size){
//             var backpackObj = backpacks[backpack].get("name").asString
//             Log.e(LOG_TAG,"loadout backpack " + cleanName(backpackObj))
//             var loadoutBackpack = ItemsDB.items[cleanName(backpackObj)]
//             if(loadoutBackpack == null)
//                 Log.e(LOG_TAG, "null backpack")
//         }
    }

    private fun cleanName(name:String):String{
        return name.replace("-"," ").replace(" +".toRegex()," ").lowercase(Locale.getDefault())
    }
    private fun nameVariation(name:String):String{
        when(variations.get(name)){
            is String -> return variations.getString(name)
            is Pair<*, *> -> {
                return if((0..10).random() %2 ==0)
                    (variations.get(name) as Pair<*, *>).first as String
                else
                    (variations.get(name) as Pair<*, *>).second as String
            }
        }
        return "null"
    }
}