package jlapps.support.tarkovteller.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

object TarkovDataStore {
    private val LOG_TAG = "Tarkov Data Store"
    private val RESET_PRAPOR = booleanPreferencesKey("reset_prapor")
    private val RESET_THERAPIST = booleanPreferencesKey("reset_therapist")
    private val RESET_SKIER = booleanPreferencesKey("reset_skier")
    private val RESET_PEACEKEEPER = booleanPreferencesKey("reset_peacekeeper")
    private val RESET_MECHANIC = booleanPreferencesKey("reset_mechanic")
    private val RESET_RAGMAN = booleanPreferencesKey("reset_ragman")
    private val RESET_JAEGER = booleanPreferencesKey("reset_jaeger")
    private val RESET_INTERVAL = intPreferencesKey("reset_interval")
    private val RESET_LAST_NOTIF_PRAPOR = stringPreferencesKey("reset_notif_prapor")
    private val RESET_LAST_NOTIF_THERAPIST = stringPreferencesKey("reset_notif_therapist")
    private val RESET_LAST_NOTIF_SKIER = stringPreferencesKey("reset_notif_skier")
    private val RESET_LAST_NOTIF_PEACEKEEPER = stringPreferencesKey("reset_notif_peacekeeper")
    private val RESET_LAST_NOTIF_MECHANIC = stringPreferencesKey("reset_notif_mechanic")
    private val RESET_LAST_NOTIF_RAGMAN = stringPreferencesKey("reset_notif_ragman")
    private val RESET_LAST_NOTIF_JAEGER = stringPreferencesKey("reset_notif_jaeger")

    private val Context.resetDataStore: DataStore<Preferences> by preferencesDataStore(
        name = "resets"
    )
    suspend fun saveTraderInterval(context:Context,interval:String){

        context.resetDataStore.edit { preferences ->
            preferences[RESET_INTERVAL] = interval.toInt()
        }
    }
    fun getTraderInterval(context: Context)=
        context.resetDataStore.data.map { preferences ->
            preferences[RESET_INTERVAL]?:10
        }


    suspend fun saveTraderReset(context: Context,trader:String){
        context.resetDataStore.edit{preferences ->
            when(trader){
                "prapor"->{ preferences[RESET_PRAPOR] = !(preferences[RESET_PRAPOR] ?: false) }
                "therapist"->{ preferences[RESET_THERAPIST] = !(preferences[RESET_THERAPIST] ?: false) }
                "skier"->{ preferences[RESET_SKIER] = !(preferences[RESET_SKIER] ?: false) }
                "peacekeeper"->{ preferences[RESET_PEACEKEEPER] = !(preferences[RESET_PEACEKEEPER] ?: false) }
                "mechanic"->{ preferences[RESET_MECHANIC] = !(preferences[RESET_MECHANIC] ?: false) }
                "ragman"->{ preferences[RESET_RAGMAN] = !(preferences[RESET_RAGMAN] ?: false) }
                "jaeger"->{ preferences[RESET_JAEGER] = !(preferences[RESET_JAEGER] ?: false) }
            }
        }
    }
    fun getTraderReset(context:Context,trader:String)=
        context.resetDataStore.data.map{preferences ->
            when(trader){
                "prapor"->{ preferences[RESET_PRAPOR]?:false }
                "therapist"->{ preferences[RESET_THERAPIST]?:false}
                "skier"->{ preferences[RESET_SKIER]?:false}
                "peacekeeper"->{ preferences[RESET_PEACEKEEPER]?:false}
                "mechanic"->{ preferences[RESET_MECHANIC]?:false}
                "ragman"->{ preferences[RESET_RAGMAN]?:false}
                else ->{ preferences[RESET_JAEGER]?:false}
            }
        }
    suspend fun saveTraderNotif(context: Context,trader:String,notif:String){
        context.resetDataStore.edit{preferences ->
            when(trader){
                "prapor"->{ preferences[RESET_LAST_NOTIF_PRAPOR] =  notif }
                "therapist"->{ preferences[RESET_LAST_NOTIF_THERAPIST] = notif}
                "skier"->{ preferences[RESET_LAST_NOTIF_SKIER] = notif}
                "peacekeeper"->{ preferences[RESET_LAST_NOTIF_PEACEKEEPER] = notif}
                "mechanic"->{ preferences[RESET_LAST_NOTIF_MECHANIC] = notif}
                "ragman"->{ preferences[RESET_LAST_NOTIF_RAGMAN] = notif}
                "jaeger"->{ preferences[RESET_LAST_NOTIF_JAEGER] = notif}
            }
        }
    }
    fun getTraderNotif(context:Context,trader:String)=
        context.resetDataStore.data.map{preferences ->
            when(trader){
                "prapor"->{ preferences[RESET_LAST_NOTIF_PRAPOR]?:""}
                "therapist"->{ preferences[RESET_LAST_NOTIF_THERAPIST]?:""}
                "skier"->{ preferences[RESET_LAST_NOTIF_SKIER]?:""}
                "peacekeeper"->{ preferences[RESET_LAST_NOTIF_PEACEKEEPER]?:""}
                "mechanic"->{ preferences[RESET_LAST_NOTIF_MECHANIC]?:""}
                "ragman"->{ preferences[RESET_LAST_NOTIF_RAGMAN]?:""}
                else ->{ preferences[RESET_LAST_NOTIF_JAEGER]?:""}
            }
        }
}