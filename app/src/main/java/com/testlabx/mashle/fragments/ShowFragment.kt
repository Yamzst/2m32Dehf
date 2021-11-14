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


    val TAG = "tsFinx"
    val TAGIMG = "tsimgx"

    var vidCurrent = ""
    var vidEnque = ""

    var pos = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.takeIf { it.containsKey("pgx") }?.apply {
            pos = getInt("pgx") - 1
        }



        Log.i("tsStxPos","habra imagen")

        if ((context as MainActivity).imgList[pos] != null){
            Log.i("tsStxPos","set image ${(context as MainActivity).imgList.size}")
            imgPh.setImageBitmap((context as MainActivity).imgList[pos])


        }



        shTlSing.isSelected = true

        //playerView.player = (activity as MainActivity).simpleExoPlayer
        //(activity as MainActivity).simpleExoPlayer.prepare()
        //playerView.requestFocus()


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

            exo_play_pause.setImageResource(R.drawable.anim_play_pause)
            (exo_play_pause.drawable as? Animatable)?.start()

        }else{
            (activity as MainActivity).simpleExoPlayer.playWhenReady = true

            exo_play_pause.setImageResource(R.drawable.anim_pause_play)
            (exo_play_pause.drawable as? Animatable)?.start()
        }

    //(activity as MainActivity).simpleExoPlayer.playWhenReady = !(activity as MainActivity).simpleExoPlayer.isPlaying


    }





    fun urlAdd(id:String,url:String){
        if (vidEnque == id){

            vidEnque = ""
            //setNewVid(url,id)

        }

    }

    fun getUrlVid() {
        Log.i("tsBonsel","sgetUrlVid || $pos")
        val vid = DtList.getVidFromPos(pos)
        vidCurrent = vid.idVid
        val nexUrl = vid.urlVid

        Log.i("tsBonsel","$vidCurrent || $nexUrl")
        if (nexUrl == ""){
            vidEnque = vidCurrent
        }else{
            //setNewVid(nexUrl,vidCurrent)

        }


    }



    fun setNewVid(){
        Log.i(TAG,DtList.database!!.vidDao().getAllUser().toString())
        Log.i(TAG, "fg" + pos.toString())
        val vid = DtList.getVidFromPos(pos)
        val id = vid.idVid

        val tl = DtList.getNmFromId(id)
        val ch = DtList.getChFromId(id)
        shTlSing.text = tl
        shChSing.text = ch


        vidCurrent = id

        GlobalScope.launch(Dispatchers.Main) {
            /*getColorBtns(id,requireContext(),mn_sing_img) { clrFg, clrBg ->
                tintBtns(clrFg,clrBg)
            }*/

            //if primer video gone o alpha boton prev
            //que pasa si hay un error en el video o url caida
        }


    }


    fun setPlayer(){
        playerView.player = null
        playerView.player = (activity as MainActivity).simpleExoPlayer
    }



    fun logx(msj:String){
        Log.i(TAG,msj)
    }


    fun hideImg(){
        imgPh.visibility = View.GONE
        Log.i("tslistenr","leegoooooo")
        exo_play_pause.setImageResource(R.drawable.anim_pause_play)
        (exo_play_pause.drawable as? Animatable)?.start()
    }


    private fun dwClick(tp:String) {

        val uri = if (tp == "video") DtList.getSvVid(vidCurrent) else DtList.getSvAud(vidCurrent)
        if (uri != "") {
            //si el archivo se leimina no se va a encontrar...entoncees en v2 tendria q comprobarse si el archivo exite
            Toast.makeText(activity, "Ya Descargado", Toast.LENGTH_SHORT).show()
            Download.sharedFile(requireContext(), Uri.parse(uri), tp)
        } else {

            dwAds()
            App.getMainActivity()?.dwAnim(true)
            Toast.makeText(activity, "Iniciando Descarga", Toast.LENGTH_SHORT).show()
            val idSv = vidCurrent
            val urlDw = if (tp == "video") DtList.getVidFromId(idSv).urlVid else DtList.getVidFromId(vidCurrent).urlAud
            var nm = DtList.getVidFromId(idSv).nmVid//Utilsx.nmRandom(20)
            //
            if (!nm.contains(" - ")) nm = "$nm - ${DtList.getVidFromId(idSv).chVid}"
            //
            Download.downloadVideo(requireContext(), urlDw, tp, nm) {
                if (it != null) {
                    if (tp == "video") DtList.setSvVid(idSv, it.toString()) else DtList.setSvAud(idSv, it.toString())
                }

                //if descarga falla tambien embiar completion con uri fallida
                //if no hay videos en cola para
                App.getMainActivity()?.dwAnim(false)
            }
        }

    }





    fun dwAds(){

        Varss.dwCount += 1
        Toast.makeText(activity, Varss.dwCount.toString() , Toast.LENGTH_SHORT).show()
        if (Varss.dwCount == 3 || Varss.frstDw){   //para el primero de cada busqueda
            Varss.frstDw = false

            Varss.dwCount = 0
            App.getMainActivity()?.stopTmrItrs()
            Varss.pdIrts = false
            App.getMainActivity()?.initItrs(FirebaseRC.dsIrt)

            App.getMainActivity()?.showIrts()
        }
    }


    override fun onPause() {
        //if se pausa y esta en otro fragment
        super.onPause()
        if (Varss.crntFg != pos){
            imgPh.visibility = View.VISIBLE
            imgPh.setImageBitmap((context as MainActivity).imgList[pos])
            //exo_play_pause.setImageResource(R.drawable.ic_play)
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



    fun tintBtns(clFg:Int,clBg:Int) {
        //dwVid2.ImageTint(clFg)
        //dwVid2.BgTint(clBg)

        //dwAud2.ImageTint(clFg)
        //dwAud2.BgTint(clBg)


        //shrLink2.ImageTint(clFg)
        //shrLink2.BgTint(clBg)

        //progressBar.indeterminateTintList = ColorStateList.valueOf(clFg)

        //exo_progress.setBufferedColor(clBg)
        //exo_progress.setUnplayedColor(clBg)

        //exo_progress.setScrubberColor(clFg)
        //exo_progress.setPlayedColor(clFg)

        /*exo_pause.ImageTint(clFg)
        exo_play.ImageTint(clFg)
        exo_prevx.ImageTint(clFg)
        exo_nextx.ImageTint(clFg)*/


    }


}