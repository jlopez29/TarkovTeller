package jlapps.support.tarkovteller.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jlapps.support.tarkovteller.R
import jlapps.support.tarkovteller.data.ItemsDB
import jlapps.support.tarkovteller.utilities.Util
import android.content.Intent
import android.net.Uri
import android.view.View.GONE
import com.squareup.picasso.Picasso
import jlapps.support.tarkovteller.databinding.FragTarkovMarketItemBinding
import androidx.appcompat.app.AppCompatActivity

class FragmentTarkovMarketItem  : Fragment(R.layout.frag_tarkov_market_item){
    private var _binding: FragTarkovMarketItemBinding? = null
    private val binding get() = _binding!!
    private val item = ItemsDB.currentMarketItem

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragTarkovMarketItemBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }
    private fun initViews(){
        Picasso
            .get()
            .load(item.imgBig)
            .placeholder(R.drawable.tarkov_plexi_bg)
            .into(binding.ivItemImg,object: com.squareup.picasso.Callback {
                override fun onSuccess() {
                    //set animations here
                    binding.pbImageLoad.visibility = GONE

                }

                override fun onError(e: java.lang.Exception?) {
                    //do smth when there is picture loading error
                }
            })


        binding.tvItemName.text = item.name
        binding.tvItemShortName.text = item.shortName
        binding.tvItemPrice.text = Util.toCurrency(item.price.toString(),item.getCurrency())
        binding.tvItemPrice24.text = Util.toCurrency(item.avg24hPrice.toString(),item.getCurrency())
        binding.tvItemPrice24Diff.text = item.diff24h.toString()
        binding.tvItemPrice7.text = Util.toCurrency(item.avg7daysPrice.toString(),item.getCurrency())
        binding.tvItemPrice7Diff.text = item.diff7days.toString()
        binding.tvItemSlots.text = item.slots.toString()
        binding.tvItemTrader.text = item.traderName
        binding.tvItemTraderPrice.text = Util.toCurrency(item.traderPrice.toString(),item.getCurrency())
        binding.tvLinkTm.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.link))
            startActivity(browserIntent)
        }
        binding.tvLinkTw.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.wikiLink))
            startActivity(browserIntent)
        }
    }


}