package com.testlabx.mashle.fragments

import android.graphics.drawable.Animatable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import com.testlabx.mashle.App
import com.testlabx.mashle.MainActivity
import com.testlabx.mashle.R
import com.testlabx.mashle.helpers.AdControler
import com.testlabx.mashle.helpers.Download
import com.testlabx.mashle.helpers.FirebaseRC
import com.testlabx.mashle.utils.*
import kotlinx.android.synthetic.main.ctn_ctrls_mn.*
import kotlinx.android.synthetic.main.custom_controller.*
import kotlinx.android.synthetic.main.fragment_show.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ShowFragment : Fragment(R.layout.fragment_show) {

    var vidCurrent = ""

    var pos = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.takeIf { it.containsKey("pgx") }?.apply {
            pos = getInt("pgx") - 1
        }


        if ((context as MainActivity).imgList[pos] != null){
            imgPh.setImageBitmap((context as MainActivity).imgList[pos])
        }


        shTlSing.isSelected = true


        exo_play_pause.setOnClickListener {
            playPause()
        }


        shrLink2.setOnClickListener {
            it.AnimView()
            activity?.sharedApp("https://m.youtube.com/watch?v=${vidCurrent}")
        }

        dwVid2.setOnClickListener { it ->
            it.AnimView()
            dwClick("video")
        }


        dwAud2.setOnClickListener {
            it.AnimView()
            dwClick("audio")
        }


        /*Glide.with(this@ShowFragment)
            .load("https://assets.mixkit.co/videos/preview/mixkit-aerial-landscape-of-a-city-at-night-41542-large.mp4")
            .apply(RequestOptions().frame(1))
            .into(imgPh)*/

        setNewVid()
    }

    private fun playPause() {
        if ((activity as MainActivity).simpleExoPlayer.playWhenReady){
            (activity as MainActivity).simpleExoPlayer.playWhenReady = false

        }else{
            (activity as MainActivity).simpleExoPlayer.playWhenReady = true

        }

    }


    fun animPlayPause(st:Boolean){
        if (st){
            exo_play_pause.setImageResource(R.drawable.anim_pause_play)
            (exo_play_pause.drawable as? Animatable)?.start()
        }else{
            exo_play_pause.setImageResource(R.drawable.anim_play_pause)
            (exo_play_pause.drawable as? Animatable)?.start()
        }
    }



    fun setNewVid(){
        //Log.i(TAG,DtList.database!!.vidDao().getAllUser().toString())

        val vid = DtList.getVidFromPos(pos)
        val id = vid.idVid
        Log.i("tsVidx", vid.toString())
        val tl = DtList.getNmFromId(id)
        val ch = DtList.getChFromId(id)
        shTlSing.text = tl
        shChSing.text = ch


        vidCurrent = id

    }


    fun setPlayer(){
        playerView.player = null
        playerView.player = (activity as MainActivity).simpleExoPlayer
    }




    fun hideImg(){
        imgPh.visibility = View.GONE
        exo_play_pause.setImageResource(R.drawable.anim_pause_play)
        (exo_play_pause.drawable as? Animatable)?.start()
    }


    private fun dwClick(tp:String) {

        val uri = if (tp == "video") DtList.getSvVid(vidCurrent) else DtList.getSvAud(vidCurrent)
        if (uri != "") {
            //v2 if file existe, ya q el usuario lo puede eliminar
            Download.sharedFile(requireContext(), Uri.parse(uri), tp)
        } else {

            dwAds()

            App.getMainActivity()?.dwAnim(true)
            val idSv = vidCurrent
            val urlDw = if (tp == "video") DtList.getVidFromId(idSv).urlVid else DtList.getVidFromId(vidCurrent).urlAud

            var nm = DtList.getVidFromId(idSv).nmVid //Utilsx.nmRandom(20)
            if (!nm.contains(" - ")) nm = "$nm - ${DtList.getVidFromId(idSv).chVid}"

            Download.downloadVideo(requireContext(), urlDw, tp, nm) {
                if (it != null) {
                    if (tp == "video") DtList.setSvVid(idSv, it.toString()) else DtList.setSvAud(idSv, it.toString())
                }

                App.getMainActivity()?.dwAnim(false)
            }
        }

    }





    fun dwAds(){

        Varss.dwCount += 1
        Log.i("tsCountDq", Varss.dwCount.toString())
        if (Varss.dwCount == 3 || Varss.frstDw){
            Varss.frstDw = false

            if (!FirebaseRC.dsAdWeb) Varss.pdAdWeb = true

            AdControler.showAdWeb()
            AdControler.showAdItr()

        }
    }


    override fun onPause() {
        super.onPause()

        if (Varss.crntFg != pos){
            imgPh.visibility = View.VISIBLE
            imgPh.setImageBitmap((context as MainActivity).imgList[pos])
            exo_play_pause.setImageResource(R.drawable.anim_pause_play)
        }

        Log.i("tsStx","onPauseFg")

        //stFocusVideo(false)
    }

    override fun onStop() {
        super.onStop()
        Log.i("tsStx","onStopFg")
        //stFocusVideo(false)
    }

    override fun onResume() {
        super.onResume()
        Log.i("tsStx","resumenFg")
    //stFocusVideo(true)
    }

    override fun onStart() {
        super.onStart()
        Log.i("tsStx","onStarFg")
    }




}