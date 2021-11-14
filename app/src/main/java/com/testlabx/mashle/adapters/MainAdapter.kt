package com.testlabx.mashle.adapters
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.startapp.sdk.ads.nativead.NativeAdDetails
import com.testlabx.mashle.App
import com.testlabx.mashle.R
import com.testlabx.mashle.helpers.FirebaseRC
import com.testlabx.mashle.utils.PlsMn
import com.testlabx.mashle.utils.Varss
import com.testlabx.mashle.utils.fromUrl

import java.util.*


class MainAdapter(var context: Context?, lsIdx: ArrayList<PlsMn>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val lsId = lsIdx

    val TAG = "tsMainAdapter"
    var nwAd = true

    var countAds = 0

    private var POS_AD = 5
    private val CONTENT = 0
    private val AD = 1

    private var nativeAd: List<NativeAdDetails>? = null

    init {
        Log.i(TAG, lsId.toString())

        if (lsId.size < 5){
            POS_AD = lsId.size - 1
        }

        if (FirebaseRC.dsNtv == 1) POS_AD = 1000
    }

    fun setNativeAd(nativeAdx: List<NativeAdDetails>) {
        nativeAd = nativeAdx
        notifyDataSetChanged()
    }


    override fun getItemViewType(position: Int): Int {
        return if (position > 0 && position % POS_AD == 0) {
            AD
        } else CONTENT
        //return 0
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder{
        return if(viewType == CONTENT){
            val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.native_ad_item_ls, parent, false)
            MyViewNormal(itemView)
        }else{
            val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.native_ad_item_mn, parent, false)
            MyViewAds(itemView)
        }

    }


    override fun getItemCount(): Int {
        return lsId.size
    }


    class MyViewNormal(view: View) : RecyclerView.ViewHolder(view) {
    }


    class MyViewAds(view: View) : RecyclerView.ViewHolder(view) {
        //var ctnItmAd: FrameLayout = view.findViewById(R.id.ctnItmSecAdd)
        var imgAd: ImageView= view.findViewById(R.id.mnImgAd)
        var icAd: ImageView= view.findViewById(R.id.mnIcAd)
        var tlAd: TextView = view.findViewById(R.id.mnTlAd)
        var desAd: TextView = view.findViewById(R.id.mnDesAd)
        var btnAd: Button = view.findViewById(R.id.mnBtnAd)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder){
            is MyViewNormal ->{
                //Log.i(TAG,"viewholder $position + ${lsId[position].urlImg}")
                //holder.tx.text = lsId[position].title
                //holder.img.fromUrl(lsId[position].urlImg)

                //eror glide tamaño de la imagen //el carrd layout ya se ve como un error glide

                //Glide.with(context!!).load("https://img.youtube.com/vi/${lsId[position].urlImg}/sddefault.jpg").error(R.drawable.er_glide).into(holder.img)


                //holder.img.clipToOutline = true
                /*holder.img.setOnClickListener {
                    //App.getMainActivity()?.searchQr(lsId[position].url,lsId[position].title)
                }*/


            }
            is MyViewAds -> {
                Log.i(TAG, " Vungle Merc Init $position")
                if (nwAd) {

                    if (nativeAd != null && nativeAd!!.isNotEmpty()) {

                        val item:NativeAdDetails = when(position){
                            6 -> nativeAd!![0]
                            12 -> if (nativeAd!!.size > 1) nativeAd!![1] else nativeAd!![0]
                            18 -> if (nativeAd!!.size > 2) nativeAd!![2] else nativeAd!![0]
                            else -> nativeAd!![0]
                        }

                        holder.btnAd.setOnClickListener {
                            holder.itemView.performClick()
                        }

                        val ad = item as NativeAdDetails?

                        if (ad != null) {
                            //holder.icon.setImageBitmap(ad.imageBitmap)
                            holder.icAd.clipToOutline = true
                            Glide.with(holder.imgAd).load(ad.imageUrl).error(R.drawable.er_glide).into(holder.imgAd)
                            Glide.with(holder.icAd).load(ad.secondaryImageUrl).error(R.drawable.er_glide).into(holder.icAd)
                            holder.tlAd.text = ad.title
                            holder.desAd.text = ad.description
                            holder.btnAd.text = if (ad.callToAction != "") ad.callToAction else if (ad.isApp) "Install" else "Open"

                            ad.registerViewForInteraction(holder.itemView)

                        }

                    }else{
                        //holder.itemView.visibility = View.GONE
                    }


                }



            }
        }

    }


    fun changeSt(){
        nwAd = true
    }

    fun noAds(){
        POS_AD = 1000
    }

    fun destroy(){
    }




}