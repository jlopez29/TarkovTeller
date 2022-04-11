package jlapps.support.tarkovteller.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.CheckBox
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import jlapps.support.tarkovteller.R
import jlapps.support.tarkovteller.api.API
import jlapps.support.tarkovteller.data.ItemsDB
import jlapps.support.tarkovteller.data.TarkovDataStore
import jlapps.support.tarkovteller.data.TraderResetTimes
import jlapps.support.tarkovteller.databinding.FragLoadoutRandomizerBinding
import jlapps.support.tarkovteller.utilities.LoadoutRandomizer
import kotlinx.coroutines.launch
import java.lang.Exception


class FragmentLoadoutRandomizer  : Fragment(R.layout.frag_loadout_randomizer){
    private var _binding: FragLoadoutRandomizerBinding? = null
    private val binding get() = _binding!!
    private lateinit var loadoutRandomizer: LoadoutRandomizer

    private val headsetCallback=MutableLiveData<String>()
    private val headwearCallback=MutableLiveData<String>()
    private val faceCallback=MutableLiveData<String>()
    private val rigCallback=MutableLiveData<String>()
    private val armorCallback=MutableLiveData<String>()
    private val eyesCallback=MutableLiveData<String>()
    private val weaponCallback=MutableLiveData<String>()
    private val backpackCallback=MutableLiveData<String>()

    private lateinit var mView: View
    private val LOG_TAG = "Randomizer"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragLoadoutRandomizerBinding.inflate(inflater, container, false)
        mView = binding.root
        return mView
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

    }

    private fun initViews(){

        loadoutRandomizer = LoadoutRandomizer(mView.context)
        loadoutRandomizer.randomizeLoadout()

        getHeadset(mView)
        getHeadwear(mView)
        getFacecover(mView)
        getRig(mView)
        getArmor(mView)
        getEyes(mView)
        getWeapon(mView)
        getBackpack(mView)

        binding.fabReroll.setOnClickListener {
            openRerollDialog(mView)
        }


    }
    private fun getHeadset(view:View){
        headsetCallback.observe(viewLifecycleOwner, {
            Picasso
                .get()
                .load(it)
                .placeholder(R.drawable.tarkov_plexi_bg)
                .into(binding.ivHeadset)
            binding.tvHeadsetName.text = loadoutRandomizer.headset.name
        })
        API.getItem(view.context, img = headsetCallback, itemUrl = loadoutRandomizer.headset.uid)
    }
    private fun getHeadwear(view:View){
        headwearCallback.observe(viewLifecycleOwner, {
            Picasso
                .get()
                .load(it)
                .placeholder(R.drawable.tarkov_plexi_bg)
                .into(binding.ivHeadwear)
            binding.tvHeadwearName.text = loadoutRandomizer.head.name
        })
        API.getItem(view.context, img = headwearCallback, itemUrl = loadoutRandomizer.head.uid)
    }
    private fun getFacecover(view:View){
        faceCallback.observe(viewLifecycleOwner, {
            Picasso
                .get()
                .load(it)
                .placeholder(R.drawable.tarkov_plexi_bg)
                .into(binding.ivFaceCover)
            binding.tvFaceCoverName.text = loadoutRandomizer.faceCover.name
        })
        API.getItem(view.context, img = faceCallback, itemUrl = loadoutRandomizer.faceCover.uid)
    }
    private fun getRig(view:View){
        rigCallback.observe(viewLifecycleOwner, {
            Picasso
                .get()
                .load(it)
                .placeholder(R.drawable.tarkov_plexi_bg)
                .into(binding.ivRig)
            binding.tvRigName.text = loadoutRandomizer.rig.name
        })
        API.getItem(view.context, img = rigCallback, itemUrl = loadoutRandomizer.rig.uid)
    }
    private fun getArmor(view:View){
        armorCallback.observe(viewLifecycleOwner, {
            Picasso
                .get()
                .load(it)
                .placeholder(R.drawable.tarkov_plexi_bg)
                .into(binding.ivArmor)
            binding.tvArmorName.text = loadoutRandomizer.armor.name
        })
        API.getItem(view.context, img = armorCallback, itemUrl = loadoutRandomizer.armor.uid)
    }
    private fun getEyes(view:View){
        eyesCallback.observe(viewLifecycleOwner, {
            Picasso
                .get()
                .load(it)
                .placeholder(R.drawable.tarkov_plexi_bg)
                .into(binding.ivEyewear)
            binding.tvEyewearName.text = loadoutRandomizer.eyes.name
        })
        API.getItem(view.context, img = eyesCallback, itemUrl = loadoutRandomizer.eyes.uid)
    }
    private fun getWeapon(view:View){
        weaponCallback.observe(viewLifecycleOwner, {
            Picasso
                .get()
                .load(it.replace("_not_func_not_func",""))
                .placeholder(R.drawable.tarkov_plexi_bg)
                .into(binding.ivWeapon, object: com.squareup.picasso.Callback{
                    override fun onSuccess() {

                    }

                    override fun onError(e: Exception?) {
                        Log.e(LOG_TAG,"Error loading weapon")
                        Picasso
                            .get()
                            .load(it)
                            .placeholder(R.drawable.tarkov_plexi_bg)
                            .into(binding.ivWeapon, object: com.squareup.picasso.Callback{
                                override fun onSuccess() {

                                }

                                override fun onError(e: Exception?) {
                                    Log.e(LOG_TAG,"Error loading weapon")
                                }

                            })
                    }

                })
            binding.tvWeaponName.text = loadoutRandomizer.weapon.name
        })
        API.getItem(view.context, img = weaponCallback, itemUrl = loadoutRandomizer.weapon.uid)
    }
    private fun getBackpack(view:View){
        backpackCallback.observe(viewLifecycleOwner, {
            Picasso
                .get()
                .load(it)
                .placeholder(R.drawable.tarkov_plexi_bg)
                .into(binding.ivBackpack)
            binding.tvBackpackName.text = loadoutRandomizer.backpack.name
        })
        API.getItem(view.context, img = backpackCallback, itemUrl = loadoutRandomizer.backpack.uid)
    }
    private fun openRerollDialog(view:View){
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_reroll,null)
        val d = Dialog(requireActivity())
        d.setContentView(dialogView)

        val headset = dialogView.findViewById<CheckBox>(R.id.cb_headset)
        val headwear = dialogView.findViewById<CheckBox>(R.id.cb_headwear)
        val eyewear = dialogView.findViewById<CheckBox>(R.id.cb_eyewear)
        val rig = dialogView.findViewById<CheckBox>(R.id.cb_rig)
        val armor = dialogView.findViewById<CheckBox>(R.id.cb_armor)
        val facecover = dialogView.findViewById<CheckBox>(R.id.cb_facecover)
        val weapon = dialogView.findViewById<CheckBox>(R.id.cb_weapon)
        val backpack = dialogView.findViewById<CheckBox>(R.id.cb_backpack)

        val accept = dialogView.findViewById<Button>(R.id.btn_reroll_accept)
        val cancel = dialogView.findViewById<Button>(R.id.btn_reroll_cancel)

        accept.setOnClickListener{
            if(headset.isChecked) {
                loadoutRandomizer.getRandomItem("headset")
                getHeadset(view)
            }
            if(headwear.isChecked) {
                loadoutRandomizer.getRandomItem("headwear")
                getHeadwear(view)
            }
            if(eyewear.isChecked) {
                loadoutRandomizer.getRandomItem("eyewear")
                getEyes(view)
            }
            if(rig.isChecked) {
                loadoutRandomizer.getRandomItem("rig")
                getRig(view)
            }
            if(armor.isChecked) {
                loadoutRandomizer.getRandomItem("armor")
                getArmor(view)
            }
            if(facecover.isChecked) {
                loadoutRandomizer.getRandomItem("facecover")
                getFacecover(view)
            }
            if(weapon.isChecked) {
                loadoutRandomizer.getRandomItem("weapon")
                getWeapon(view)
            }
            if(backpack.isChecked) {
                loadoutRandomizer.getRandomItem("backpack")
                getBackpack(view)
            }
            d.cancel()
        }
        cancel.setOnClickListener {
            d.cancel()
        }
        d.show()
    }
}