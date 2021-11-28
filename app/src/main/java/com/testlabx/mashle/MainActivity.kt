package com.testlabx.mashle


import android.content.*

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.media.MediaMetadataRetriever
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.startapp.sdk.ads.banner.Banner
import com.startapp.sdk.ads.banner.BannerListener
import com.startapp.sdk.adsbase.adlisteners.AdEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collect
import org.schabi.newpipe.extractor.NewPipe
import java.util.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.startapp.sdk.adsbase.*
import com.testlabx.mashle.activitys.*
import com.testlabx.mashle.helpers.*
import com.testlabx.mashle.utils.*
import androidx.viewpager2.widget.ViewPager2
import com.google.android.exoplayer2.util.Util
import com.startapp.sdk.ads.nativead.NativeAdPreferences
import com.startapp.sdk.ads.nativead.StartAppNativeAd
import com.testlabx.mashle.fragments.ShowFragment
import com.testlabx.mashle.Pager.PagerAdapter
import com.testlabx.mashle.dataB.Vid
import kotlinx.android.synthetic.main.ctn_ctrls_mn.*
import kotlinx.android.synthetic.main.fragment_show.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import android.widget.Toast
import android.widget.RelativeLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.native_ad.*
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory
import com.google.android.exoplayer2.audio.AudioAttributes
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener
import com.startapp.sdk.adsbase.model.AdPreferences
import com.testlabx.mashle.service.MainService


class MainActivity : AppCompatActivity() {

    val TAGFN = "tsFinalx"

    val TAGVIDS = "tsVidsx"
    var adList: ArrayList<String> = ArrayList()
    var idsN = 0
    var tempList: ArrayList<InfoVid> = ArrayList()
    val imgList: HashMap<Int, Bitmap> = HashMap()
    var bcCdPl = 0

    var lstGetPos = -1

    private var pagerAdapter: PagerAdapter? = null
    lateinit var simpleExoPlayer: SimpleExoPlayer

    private lateinit var storage: SharedPreferences
    private var svFrzCnt = 1

    var startAppBanner:Banner? = null
    val startAppAd = StartAppAd(this)
    private val adPrederence = AdPreferences().muteVideo()

    val tmrCntAdWb = Handler(Looper.getMainLooper())
    val tmrAdWb = Handler(Looper.getMainLooper())

    var searchError = false
    var idTx = 0

    private var serviceToMainActivityCallback: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val methodName = intent.getStringExtra(Constants.serivceNotificationMethod)
            if (methodName != null) {
                when (methodName) {
                    Constants.PLAY_VIDEO_IF_PAUSED_FROM_NOTIFICATION -> {
                        Log.i(TAGNT,"playVid - Main")
                        playVideo()
                    }
                    Constants.PAUSE_VIDEO_IF_PLAYING_FROM_NOTIFICATION -> {
                        pauseVideo()
                    }
                    Constants.STOP_FROM_NOTIFICATION -> {
                        onDestroy()
                    }

                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Mashle)
        super.onCreate(savedInstanceState)

        StartAppAd.disableAutoInterstitial()
        StartAppAd.disableSplash()
        StartAppSDK.setTestAdsEnabled(true)

        setContentView(R.layout.activity_main)


        Toast.makeText(this, "createee", Toast.LENGTH_SHORT).show()
        tintBarNv(Color.BLACK)
        tintBarSt(Color.BLACK)


        Varss.restartVars()
        FirebaseRC.resetVars()

        //NewPipe.init(CustomDownloader.getInstance(),Localization("es","PE"))
        NewPipe.init(CustomDownloader.getInstance())

        simpleExoPlayer = SimpleExoPlayer.Builder(App.AppContext).build() //this
        simpleExoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        simpleExoPlayer.playWhenReady = true
        //simpleExoPlayer.setWakeMode(C.WAKE_MODE_NETWORK)
        //playerView.requestFocus()
        //playerView.hideController()
        //playerView.controllerAutoShow = false


        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()
        simpleExoPlayer.setAudioAttributes(audioAttributes,true)


        //cuidado se valla a a llamar a end iniciando ...colocar despues de prepare
        simpleExoPlayer.addListener(playbackStateListener())

        storage = getSharedPreferences(Constants.DATOS_NAME, Context.MODE_PRIVATE)
        svFrzCnt = storage.getInt(Constants.SV_FRZ_CNT,1)


        DtList.initDataBase(this)
        Download.initHttp3()
        FirebaseEvent.init(this)


        when (svFrzCnt){
            0 ->{}
            1 ->{
                Utilsx.createNotificationChannel(this,Constants.CHANNELID)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Utilsx.createNtfChPlayer(this)
                }
                FirebaseRC.settingsRmtCnfg()
                storage.edit().putInt(Constants.SV_FRZ_CNT,2).apply()
            }
            2 ->{}
        }


        FirebaseRC.svLstMsj = storage.getString(Constants.SV_LAST_MSJ,"").toString()

        //INTENTS
        Intentsx.New(intent)

        //GET CONFIG CLOUD
        internetState()


        btnCfg.setOnClickListener {
            val intent_more = Intent(this, MoreActivity::class.java)
            intent_more.putExtra("updt",FirebaseRC.updt)
            intent_more.putExtra("updtUrl",FirebaseRC.urlUpdt)
            intent_more.putExtra("urlStorex",FirebaseRC.urlStore)
            startActivity(intent_more)
        }

        icUp.visibility = View.GONE
        icUp.setOnClickListener {
            viewPager.currentItem = viewPager.currentItem - 1
        }


        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                //simpleExoPlayer.playWhenReady = false

                showBg(true)

                Varss.crntFg = position
                val fg = pagerAdapter!!.hashMap[viewPager.currentItem] as ShowFragment?
                fg?.setPlayer()

                simpleExoPlayer.playWhenReady = true
                simpleExoPlayer.seekTo(position,0)

                GlobalScope.launch(Dispatchers.IO) {
                    if (position % 4 == 0 && tempList.size != 0) {
                        logx("obtener mas items")
                        if (position > lstGetPos) {
                            lstGetPos = position
                            logx("obtener mas items - si pudo")
                            //var n = if (idsList.size % 7 == 0) 7 else idsList.size % 7
                            for (x in 0..7) { //position  //idsList.size
                                if (x < tempList.size) getNewUrl(tempList[x], Varss.crtnPlst)
                                logx("getNewUrl $x || ${Varss.crtnPlst}")
                                //puede que se remueva
                                //pienso q ..tempList.size == 3 entonces como esta en segundo plano
                                //se elimino aqui entonces  cuando se va a buscar ya no esta y da error
                            }

                        }
                    }
                }


                AdControler.showAdWeb()


                if (position == idsN - 1){
                    icUp.visibility = View.VISIBLE
                }else{
                    icUp.visibility = View.GONE
                }

            }
        })

        tabLayout.removeAllTabs()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                AdControler.showAdWeb()
                AdControler.showAdNat()


                var cdPl = (1..50).random() //50
                while(cdPl == bcCdPl){
                    cdPl = (1..50).random()
                }
                bcCdPl = cdPl
                Varss.crtnPlst = cdPl //random number
                logx("$bcCdPl || $cdPl")
                logx("newQuerry ${tab.position}")
                newQuery(Varss.mnList[tab.position].url,cdPl)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        showBg(true)


        GlobalScope.launch(Dispatchers.IO) {
            val idVid = "INEbRe0MWf4"
            try {
                Log.i(TAGFN,"item ts1 finishAAA")
                val st = StreamInfo.getInfo("https://www.youtube.com/watch?v=${idVid}")
                Log.i(TAGFN,"item ts1 finishBBB")
                val vdSt = st.name

                Log.i(TAGFN,"item ts1 $vdSt")

            }catch (e:Exception){
                launch(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "errorr", Toast.LENGTH_SHORT).show()
                }
                return@launch

            }finally {
                //VID ya no enquee
            }
        }


        startNotify()


    }


    fun addTabs(){
        Varss.mnList.forEach {
            tabLayout.addTab(tabLayout.newTab().setText(it.title))
        }
    }

    fun newQuery(qrx:String,pls: Int) {

        var tp = ""
        val qrSearch = if (qrx.contains("srch:")){
            tp = "search"
            qrx.replace("srch:","")
        }else{
            tp = "playlist"
            "https://www.youtube.com/playlist?list=$qrx"
        }

        searchQr(qrSearch,tp,pls)

        viewPager.visibility = View.GONE
        showBg(true)

        if (searchError){
            removeTxEr(idTx)
        }
        searchError = false

    }



    fun searchQr(qrx:String,tp:String,pls: Int){
        logx("searchQr ${Varss.crtnPlst}")

        GlobalScope.launch(Dispatchers.IO) {

            logx("clear vars init ${Varss.crtnPlst}")
            DtList.database?.vidDao()?.deleteAll()
            imgList.clear()
            tempList.clear()
            idsN = 0
            lstGetPos = -1
            launch(Dispatchers.Main) {
                simpleExoPlayer.clearMediaItems()
            }
            logx("clear vars init ${Varss.crtnPlst}")

            try {

                 if (tp == "search"){
                     val extractor = ServiceList.YouTube.getSearchExtractor(qrx,
                         Collections.singletonList(YoutubeSearchQueryHandlerFactory.MUSIC_VIDEOS), "")
                     extractor.fetchPage()

                     if (pls != Varss.crtnPlst) return@launch
                     logx("searchQr llego data ${Varss.crtnPlst}")

                     for (song in extractor.initialPage.items) {

                         val ex = song as StreamInfoItem
                         val n = Utilsx.getIdFromLink(ex.url)

                         idsN ++

                         tempList.add(InfoVid(n,ex.name,ex.uploaderName))
                     }

                }else{
                     val extractor = ServiceList.YouTube.getPlaylistExtractor(qrx)
                     extractor.fetchPage()

                     if (pls != Varss.crtnPlst) return@launch
                     logx("searchQr llego data ${Varss.crtnPlst}")

                     for (song in extractor.initialPage.items) {

                         val ex = song as StreamInfoItem
                         val n = Utilsx.getIdFromLink(ex.url)

                         idsN ++

                         tempList.add(InfoVid(n,ex.name,ex.uploaderName))

                     }
                }


                logx("searchQr termino ${Varss.crtnPlst}")
                getNewUrl(tempList[0],pls)
                getNewUrl(tempList[1],pls)
                //lamar a mas aqui


            } catch (e: Exception) {
                showTxNoFound()
                searchError = true

            }

            if (!searchError){

                launch(Dispatchers.Main) {
                    viewPager.visibility = View.VISIBLE
                    pagerAdapter = PagerAdapter(this@MainActivity)
                    viewPager.adapter = pagerAdapter
                    Varss.frstDw = true
                }

            }

        }

    }



    fun getNewUrl(infoVid: InfoVid,pls:Int){

        logx("getNewUrl ${Varss.crtnPlst} - $pls")
        GlobalScope.launch(Dispatchers.IO) {

            if (tempList.indexOf(infoVid) != -1){
                tempList.remove(infoVid)
                val urlAu:String
                val nwUrl:String
                try {

                    val st = StreamInfo.getInfo("https://www.youtube.com/watch?v=${infoVid.id}")
                    logx("$pls obtuvo StreamInfo crnPls${Varss.crtnPlst}")
                    if (pls != Varss.crtnPlst) return@launch
                    val vdSt = st.videoStreams

                    val auSt = st.audioStreams
                    val lsTmp: ArrayList<String> = ArrayList()
                    auSt.forEach {
                        if (it.codec.toString().contains("mp4a")) {
                            lsTmp.add(it.url)
                        }
                    }

                    urlAu = lsTmp.last()
                    nwUrl = vdSt.last().url
                    logx(nwUrl)


                }catch (e:Exception){
                    launch(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "No se Pudo Descargar", Toast.LENGTH_SHORT).show()
                    }
                    return@launch

                }finally {
                    //
                }


                if (pls == Varss.crtnPlst) {

                    launch(Dispatchers.Main) {


                        dwImg(nwUrl, simpleExoPlayer.mediaItemCount)

                        logx("$pls setNewVid en DtBs ${simpleExoPlayer.mediaItemCount}")
                        DtList.setNewVid(Vid(infoVid.id,simpleExoPlayer.mediaItemCount,infoVid.nm,infoVid.ch,nwUrl,urlAu,"",""))

                        val mediaItem = MediaItem.fromUri(nwUrl)
                        simpleExoPlayer.addMediaItem(simpleExoPlayer.mediaItemCount, mediaItem)//
                        simpleExoPlayer.prepare()


                        //pagerAdapter?.notifyDataSetChanged()
                        pagerAdapter?.notifyItemInserted(simpleExoPlayer.mediaItemCount)

                    }

                }


            }


        }
    }

    private fun dwImg(url:String,pos:Int) {
        GlobalScope.launch(Dispatchers.IO) {

            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(url, HashMap())
            val bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            retriever.release()

            imgList[pos] = bitmap!!
            Log.i("tsStxPos",imgList.size.toString())

            /*launch(Dispatchers.Main) {
            //Toast.makeText(this@MainActivity, "oooooooo", Toast.LENGTH_SHORT).show()
            // importante no se puede lanza toas preguntar para despejar dudas
            }*/

        }
    }


    fun logx(msj:String){
        Log.i(TAGVIDS,msj)
    }

    //Internet
    private fun internetState() {
        var fv = true
        var initStApp = false
        NetworkConnection.initialize(this)
        lifecycleScope.launchWhenStarted {
            NetworkConnection.isConnected.collect {isConnected ->
                if (isConnected) {

                    if (fv){
                        if (!FirebaseRC.obDt){
                            getConfigCloud()
                        }
                        fv = false
                    }

                    if (!initStApp){
                        initStarApp()
                        initStApp = true
                    }


                }else{

                    if (pagerAdapter != null){
                        simpleExoPlayer.playWhenReady = false
                    }

                    val intent = Intent(this@MainActivity, WifiActivity::class.java)
                    startActivity(intent)

                }
            }
        }
    }






    //ADSX

    fun initStarApp() {
        /*val ageUser = storage.getInt(Constants.SV_AGE,22)
        val genUser = storage.getInt(Constants.SV_GEN,0)

        val gen = if (genUser == 0){
            SDKAdPreferences.Gender.MALE
        }else{
            SDKAdPreferences.Gender.FEMALE
        }
        val sdkAdPreferences = SDKAdPreferences().setAge(ageUser).setGender(gen)
         */

        StartAppSDK.init(this,Constants.ID_APP_ST,false)


    }



    fun initAdWb() {
        tmrCntAdWb.postDelayed({
            setAdWeb()
        }, Constants.TM_AD_ITR)
    }



    fun setAdWeb() {
        tmrAdWb.post(object : Runnable {
            override fun run() {
                Varss.pdAdWeb = true
                tmrAdWb.postDelayed(this, Constants.TM_AD_ITR)
            }
        })
    }


    fun showAdWeb(){
        if (NetworkConnection.isConnected.value){
            val intent= Intent(this@MainActivity, WebAdActivity::class.java)
            intent.putExtra("pgAd",adList.random())
            startActivity(intent)
        }
    }



    fun stopTmrAdWeb(){
        tmrAdWb.removeCallbacksAndMessages(null)
        tmrCntAdWb.removeCallbacksAndMessages(null)
    }



    fun initStarAppBanner(){
        clrBnrSt()
        //MANGER VISIBILIDAD CUANDO se va a onstop y onnresumen

        val bannerParameters = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL)
        bannerParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

        startAppBanner = Banner(this, object:BannerListener{
            override fun onReceiveAd(p0: View?) {
                Log.i("testAds","receive")
                bnrTop.addView(p0,bannerParameters)
            }

            override fun onFailedToReceiveAd(p0: View?) {
                Log.i("testAds", "StartApp failed Receive Bnr ${startAppBanner?.errorMessage.toString()}")
                FirebaseEvent.send("stBnrLdEr","er", startAppBanner?.errorMessage.toString())
            }

            override fun onImpression(p0: View?) {
                Log.i("testAds","Impresion")
                FirebaseEvent.send("stBnrIm","", "")
            }

            override fun onClick(p0: View?) {
                Log.i("testAds","click")
                FirebaseEvent.send("stBnrCl","", "")

            }

        })

        //startAppBanner.loadAd(320,50)
        startAppBanner!!.loadAd()

    }


    private fun clrBnrSt(){
        if (startAppBanner != null){
            startAppBanner!!.hideBanner()
            startAppBanner = null
            bnrTop.removeAllViews()
        }
    }


    fun hideBnr(){
        bnrTop.visibility = View.GONE
    }



    fun loadNativeAd() {
        if (FirebaseRC.dsNtv != 1 && FirebaseRC.dsNtv != 3) {
            val nativePrefs = NativeAdPreferences()
                .setAdsNumber(1)
                .setPrimaryImageSize(4)
                .setSecondaryImageSize(0)

            val nativeAd = StartAppNativeAd(this)
            nativeAd.loadAd(nativePrefs, object : AdEventListener {
                override fun onReceiveAd(ad: Ad) {

                    if (simpleExoPlayer.isPlaying) return

                    ctnNtvAd.visibility = View.VISIBLE
                    val nativeAds = nativeAd.nativeAds
                    if (nativeAds != null && nativeAds.isNotEmpty()) {

                        val ad = nativeAds[0]

                        adNtvBtn.setOnClickListener {
                            ctnNtvAd.performClick()
                        }

                        //holder.icon.setImageBitmap(ad.imageBitmap)
                        adNtvIc.clipToOutline = true
                        Glide.with(this@MainActivity).load(ad.imageUrl).error(R.drawable.er_glide).into(adNtvImg)
                        Glide.with(this@MainActivity).load(ad.secondaryImageUrl).into(adNtvIc)
                        adNtvTl.text = ad.title
                        adNtvDes.text = ad.description
                        adNtvBtn.text = if (ad.callToAction != "") ad.callToAction else if (ad.isApp) "Install" else "Open"

                        ad.registerViewForInteraction(ctnNtvAd)


                    } else {
                        ctnNtvAd.visibility = View.GONE
                    }
                }

                override fun onFailedToReceiveAd(ad: Ad) {
                    FirebaseEvent.send("stNtvLdEr","er", ad.errorMessage.toString())
                }


            })
        }
    }


    fun deleteNtvAd(){
        ctnNtvAd.visibility = View.GONE
    }


    fun initStartAppInterstitial(){
        startAppAd.loadAd(adPrederence,object : AdEventListener {
            override fun onReceiveAd(ad: Ad) {
                Varss.stBfAd = simpleExoPlayer.playWhenReady
                simpleExoPlayer.playWhenReady = false
                startAppAd.showAd(startAppAdCallback())
                Log.i("testAds", "\n StartApp Receive Interstitial")

            }
            override fun onFailedToReceiveAd(ad: Ad) {
                Log.i("testAds", " StartApp failed Interstitial ${ad.errorMessage.toString()}")
                FirebaseEvent.send("stItrLdEr","er", ad.errorMessage.toString())
            }
        })


    }

    private fun startAppAdCallback() = object : AdDisplayListener{
        override fun adHidden(p0: Ad?) {
            if (Varss.stBfAd){
                simpleExoPlayer.playWhenReady = true
            }

            Log.i("testAds", "\n StartApp Itrs adHidden ")
        }

        override fun adDisplayed(p0: Ad?) {
            Log.i("testAds", "\n StartAppItrs  adDisplayed ")
            Toast.makeText(this@MainActivity, "termino adds", Toast.LENGTH_SHORT).show()
            FirebaseEvent.send("stItrDs","", "")
        }

        override fun adClicked(p0: Ad?) {
            Log.i("testAds", "\n StartApp Itrs adClicked ${p0?.extraData}")
            FirebaseEvent.send("stItrCl","", "")
        }

        override fun adNotDisplayed(p0: Ad?) {
            Log.i("testAds", "\n StartApp Itrs adNotDisplayed ${p0?.errorMessage}")
            FirebaseEvent.send("stItrNtDs","er", p0?.errorMessage.toString())
        }


    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.i("NewIntxa1", intent?.data.toString())
        Log.i("NewIntxa2", intent?.getStringExtra(Intent.EXTRA_TEXT).toString())
        Intentsx.New(intent)
    }



    fun showAcMsj(msjx:String,urlMsj:String){
        storage.edit().putString(Constants.SV_LAST_MSJ, msjx).apply()
        val intent_msj = Intent(this, MensajeActivity::class.java)
        intent_msj.putExtra("msjx",msjx)
        intent_msj.putExtra("urlMsjx",urlMsj)
        startActivity(intent_msj)

    }

    private fun removeTxEr(idTx:Int){
        ctnLA.removeView(findViewById(idTx))
    }


    fun getUpdMsSh():Int {
        return storage.getInt(Constants.SV_UPD_MSJ_SH,0)
    }


    fun putUpdMsSh(vs:Int){
        storage.edit().putInt(Constants.SV_UPD_MSJ_SH, vs).apply()
    }


    private fun getConfigCloud() {
        FirebaseRC.fetch {}
    }


    fun updt(tp: String, url: String) {
        val intn_updt = Intent(this, UpdateActivity::class.java)
        intn_updt.putExtra("updtUrl", url)
        when (tp) {
            "A" -> intn_updt.putExtra("updtTp", "A")
            "B" -> intn_updt.putExtra("updtTp", "B")
        }
        startActivity(intn_updt)
    }


    fun showBg(state:Boolean){
        if (state){
            bgls.visibility = View.VISIBLE
            bgls.setImageResource(R.drawable.badge_load)
            (bgls.drawable as? Animatable)?.start()
        }else{
            bgls.visibility = View.GONE
            (bgls.drawable as? Animatable)?.stop()
        }
    }




    fun dwAnim(st:Boolean){

        GlobalScope.launch(Dispatchers.Main) {
            if (st) {
                prgsDw.visibility = View.VISIBLE
            } else {
                if (Download.dwsEnqueue == 0){
                    prgsDw.visibility = View.GONE
                }
            }
        }
    }


    private fun releasePlayer() {
        simpleExoPlayer.release()
    }



    private fun playbackStateListener() = object : Player.Listener {

        override fun onPlayerError(error: PlaybackException) {
            Log.i("tsErrorPlayer","onPlayerError")
            super.onPlayerError(error)
        }

        override fun onPlayerErrorChanged(error: PlaybackException?) {
            Log.i("tsErrorPlayer","onPlayerErrorChanged")
            super.onPlayerErrorChanged(error)
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, reason)
        }
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {}


        override fun onPlaybackStateChanged(playbackState: Int) {
            Log.i("tspkayer", "bbb  " + playbackState)
            when (playbackState) {
                ExoPlayer.STATE_READY -> {

                    //una vez al iniciar
                    showBg(false)
                    val fg = pagerAdapter!!.hashMap[viewPager.currentItem] as ShowFragment?
                    fg?.hideImg()

                    deleteNtvAd()
                }
                ExoPlayer.STATE_IDLE -> {}
                ExoPlayer.STATE_BUFFERING -> {}
                ExoPlayer.STATE_ENDED -> {}
                else -> {}
            }

        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {

            if (isPlaying){
                val fg = pagerAdapter!!.hashMap[viewPager.currentItem] as ShowFragment?
                fg?.animPlayPause(true)
            }else{
                val fg = pagerAdapter!!.hashMap[viewPager.currentItem] as ShowFragment?
                fg?.animPlayPause(false)
            }

            updateNotificaton()

            super.onIsPlayingChanged(isPlaying)



            Log.i("tspkayer","isplaying $isPlaying" )
        }

    }



    private fun showTxNoFound() {


        GlobalScope.launch(Dispatchers.Main) {
            showBg(false)

            val tv = TextView(this@MainActivity)
            tv.text = getString(R.string.no_fnd)
            //tv.setTextColor(Color.parseColor("#1e1e1e"))
            tv.setTextColor(Color.WHITE)
            tv.textSize = 20f
            tv.alpha = 0.5f

            val params = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT, // width
                ConstraintLayout.LayoutParams.WRAP_CONTENT // height
            )

            tv.layoutParams = params

            params.bottomToBottom = ConstraintSet.PARENT_ID
            params.endToEnd = ConstraintSet.PARENT_ID
            params.startToStart = ConstraintSet.PARENT_ID
            params.topToTop = ConstraintSet.PARENT_ID

            idTx = View.generateViewId()
            tv.id = idTx

            ctnLA.addView(tv)
        }
    }






    override fun onResume() {
        super.onResume()
        Log.i("tsStx","resumenAc")
    }

    override fun onStop() {
        super.onStop()
        Log.i("tsStx","stopAc")
    }


    override fun onPause() {
        super.onPause()
        Log.i("tsStx","pauseAc")
    }




    val TAGNT = "tsNotifyx"
    //CONTROLS MEDIA VIDEO
    fun pauseVideo() {
        Log.i(TAGNT,"pauseVid")
        simpleExoPlayer.pause()
        simpleExoPlayer.playWhenReady = false
    }

    fun playVideo() {
        Log.i(TAGNT,"playVid")
        simpleExoPlayer.play()
        simpleExoPlayer.playWhenReady = true
    }
    

    //Notify
    fun startNotify() {
        registerRecievers()
        startService()
    }


    private fun registerRecievers() {
        registerReceiver(
            serviceToMainActivityCallback,
            IntentFilter(Constants.SERVICE_TO_MAIN_ACTIVITY_CALLBACK_ID)
        )
    }


    private fun unregisterRecievers() {
        try {
            LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(serviceToMainActivityCallback)
        } catch (e: IllegalArgumentException) {
        }
        try {
            unregisterReceiver(serviceToMainActivityCallback)
        } catch (e: java.lang.Exception) {
        }
    }




    fun updateNotificaton() {
        Intent(App.AppContext, MainService::class.java).apply {
            this.putExtra(Constants.serivceNotificationMethod, Constants.UPDATE_NOTIFICATION)
        }.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                App.AppContext.startForegroundService(it)
            } else {
                App.AppContext.startService(it)
            }
        }
    }


    private fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(applicationContext, MainService::class.java))
        } else {
            startService(Intent(applicationContext, MainService::class.java))
        }
    }



    override fun onDestroy() {
        unregisterRecievers()
        pauseVideo()
        //updateNotificaton()

        App.releaseAndReAcquireLocks(false)

        if (Util.SDK_INT > 23) releasePlayer()
        Intent(App.AppContext, MainService::class.java).apply {
            this.putExtra(Constants.serivceNotificationMethod, Constants.STOP_FROM_NOTIFICATION)
        }.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                App.AppContext.startForegroundService(it)
            } else {
                App.AppContext.startService(it)
            }
        }

        super.onDestroy()
    }











}