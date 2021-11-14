package com.testlabx.mashle


import android.content.*
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.media.MediaMetadataRetriever
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.startapp.sdk.ads.banner.Banner
import com.startapp.sdk.ads.banner.BannerListener
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener
import com.startapp.sdk.adsbase.adlisteners.AdEventListener
import com.startapp.sdk.adsbase.model.AdPreferences
import com.vungle.warren.*
import com.vungle.warren.error.VungleException
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collect
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.localization.Localization
import java.util.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.startapp.sdk.adsbase.*
import com.testlabx.mashle.activitys.*
import com.testlabx.mashle.helpers.*
import com.testlabx.mashle.utils.*
import androidx.viewpager2.widget.ViewPager2
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
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
import com.google.android.material.tabs.TabLayout

import android.widget.Toast
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import android.widget.RelativeLayout
import androidx.media.session.MediaButtonReceiver
import com.bumptech.glide.Glide
import com.startapp.sdk.ads.nativead.NativeAdDetails
import kotlinx.android.synthetic.main.native_ad.*
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory


class MainActivity : AppCompatActivity() {


    var mnList: ArrayList<PlsMn> = ArrayList()

    var idsList: ArrayList<String> = ArrayList()
    var colaVid: ArrayList<String> = ArrayList()
    val imgList: HashMap<Int, Bitmap> = HashMap()

    val TAG = "tsFinx"

    private var pagerAdapter: PagerAdapter? = null
    var currentFg:ShowFragment? = null
    lateinit var simpleExoPlayer: SimpleExoPlayer

    private lateinit var storage: SharedPreferences
    private var svFrzCnt = 1

    var internet = true

    val startAppAd = StartAppAd(this)
    var startAppBanner:Banner? = null
    private val adPrederence = AdPreferences().muteVideo()

    var vungleBanner: VungleBanner? = null
    val adcfg = AdConfig()


    val tmrCntIrts = Handler(Looper.getMainLooper())
    val tmrIrts = Handler(Looper.getMainLooper())


    var searchError = false
    var idTx = 0


    private var serviceToMainActivityCallback: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Mashle)
        super.onCreate(savedInstanceState)

        StartAppAd.disableAutoInterstitial()
        StartAppAd.disableSplash()
        StartAppSDK.setTestAdsEnabled(true)

        setContentView(R.layout.activity_main)

        tintBarNv(Color.BLACK)
        tintBarSt(Color.BLACK)


        Varss.restartVars()
        FirebaseRC.resetVars()

        //iniciar en intenet conection..entonces si nohya al ingresar y luego viene iniviar esta vez con intenet
        Log.i("initnew","inicio")
        NewPipe.init(CustomDownloader.getInstance(),Localization("es","PE"))
        Log.i("initnew","finn")

        /*GlobalScope.launch(Dispatchers.IO) {
        //colocar try cath x q si no hay interner se bloque la app
            val st = StreamInfo.getInfo("https://m.youtube.com/watch?v=QaXhVryxVBk")
            val vdSt = st.videoStreams
            Log.i("sdfvf",vdSt[0].url)
        }*/


        //CONSTRUIR CON CONTEXT DE APPLICAICON APP.CONTEXT
        simpleExoPlayer = SimpleExoPlayer.Builder(this).build()
        simpleExoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        simpleExoPlayer.playWhenReady = true
        //playerView.hideController()
        //playerView.controllerAutoShow = false

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
                FirebaseRC.settingsRmtCnfg()

                val intent_help = Intent(this@MainActivity, HelpActivity::class.java)
                startActivity(intent_help)

            }
            2 ->{ }
        }


        FirebaseRC.svLstMsj = storage.getString(Constants.SV_LAST_MSJ,"").toString()

        //INTENTS
        Intentsx.New(intent)

        //GET CONFIG CLOUD
        internetState()

        Handler(Looper.getMainLooper()).postDelayed({
            //if (!Vungle.isInitialized() && internet){
            //  initVungle()
            //  FirebaseEvent.send("VgNnt","","")
            //}
        }, 10000)

        adcfg.setMuted(true)
        adcfg.adOrientation = AdConfig.PORTRAIT


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

                showBg(true)
                //mostrar load
                //en ready ocultar
                Varss.crntFg = position
                val fg = pagerAdapter!!.hashMap[viewPager.currentItem] as ShowFragment?
                fg?.setPlayer()

                Log.i(TAG,"${simpleExoPlayer.mediaItemCount} numero de mediaItems")

                simpleExoPlayer.playWhenReady = true
                simpleExoPlayer.seekTo(position,0)

                if (position < Varss.idsN - 1) getNewUrl(position + 1,Varss.crtnPlst)
                if (position < Varss.idsN - 2) getNewUrl(position + 2,Varss.crtnPlst)
                if (position < Varss.idsN - 3) getNewUrl(position + 3,Varss.crtnPlst)
                if (position < Varss.idsN - 4) getNewUrl(position + 4,Varss.crtnPlst)
                if (position < Varss.idsN - 5) getNewUrl(position + 5,Varss.crtnPlst)
                if (position < Varss.idsN - 6) getNewUrl(position + 6,Varss.crtnPlst)
                if (position < Varss.idsN - 7) getNewUrl(position + 7,Varss.crtnPlst)

                if (position == Varss.idsN - 1) Toast.makeText(this@MainActivity, "final", Toast.LENGTH_SHORT).show()

                //add
                //loadNativeAd()
                AdControler.showAdItrs()

                if (position == Varss.idsN - 1){
                    icUp.visibility = View.VISIBLE
                }else{
                    icUp.visibility = View.GONE
                }

            }
        })

        tabLayout.removeAllTabs()
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                Varss.crtnPlst = tab.position
                newQuery(mnList[tab.position].url)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


        showBg(true)
        //newQuery("PLeyDm-yUdpqAJ6SuROS9M8uEx-vrcF_73")

        GlobalScope.launch(Dispatchers.IO) {
            val extractor =
                ServiceList.YouTube.getSearchExtractor("estados para whatsapp  after:2021-11-05")
            extractor.fetchPage()
            for (song in extractor.initialPage.items) {
                val ex = song as StreamInfoItem
                Log.i("tsSrchxYt1", ex.name)
            }

            Log.i("tsSrchxYt1", "----------------------------------------")

            val extractor2 = ServiceList.YouTube.getSearchExtractor(
                "estados para whatsapp  after:2021-11-05",
                Collections.singletonList(YoutubeSearchQueryHandlerFactory.MUSIC_VIDEOS),
                ""
            )
            extractor2.fetchPage()
            for (song in extractor2.initialPage.items) {
                val ex = song as StreamInfoItem
                Log.i("tsSrchxYt2", ex.name)
            }
        }



    }

    fun newQuery(qrx:String) {

        var tp = ""
        val qrSearch = if (qrx.contains("srch:")){
            tp = "search"
            qrx.replace("srch:","")
        }else{
            tp = "playlist"
            "https://www.youtube.com/playlist?list=$qrx"
        }

        searchQr(qrSearch,tp)

        viewPager.visibility = View.GONE
        showBg(true)

        if (searchError){
            removeTxEr(idTx)
        }
        searchError = false


        if (Varss.pdIrts) AdControler.showAdItrs() else loadNativeAd()


    }



    fun searchQr(qrx:String,tp:String){

        GlobalScope.launch(Dispatchers.IO) {

            try {

                var cl = false
                if (!cl){
                    //esto puede ir afuera del for
                    DtList.database?.vidDao()?.deleteAll()
                    imgList.clear()
                    colaVid.clear()
                    idsList.clear()
                    launch(Dispatchers.Main) {
                        simpleExoPlayer.clearMediaItems()
                    }

                    cl = true
                    Varss.idsN = 0
                }

                //var lst:StreamInfoItem
                 if (tp == "search"){
                     val extractor = ServiceList.YouTube.getSearchExtractor(qrx,
                         Collections.singletonList(YoutubeSearchQueryHandlerFactory.MUSIC_VIDEOS),
                         "")
                     extractor.fetchPage()
                     for (song in extractor.initialPage.items) {

                         val ex = song as StreamInfoItem
                         val n = Utilsx.getIdFromLink(ex.url)

                         DtList.database?.vidDao()?.insertVid(Vid(n,Varss.idsN,ex.name,ex.uploaderName,"","","",""))

                         idsList.add(n)
                         Varss.idsN ++

                     }
                }else{
                     val extractor = ServiceList.YouTube.getPlaylistExtractor(qrx)
                     extractor.fetchPage()
                     for (song in extractor.initialPage.items) {

                         val ex = song as StreamInfoItem
                         val n = Utilsx.getIdFromLink(ex.url)

                         DtList.database?.vidDao()?.insertVid(Vid(n,Varss.idsN,ex.name,ex.uploaderName,"","","",""))

                         idsList.add(n)
                         Varss.idsN ++

                     }
                }



                getNewUrl(0,Varss.crtnPlst)
                //lamar a mas aqui

                Log.i("tsBonsel",DtList.database!!.vidDao().getAllUser().toString())

            } catch (e: Exception) {
                Log.i("tsSearchx", e.toString())
                Log.i("tsSearchx","error1")
                showTxNoFound()
                //(scOpBarSr.drawable as? Animatable)?.stop()
                //scOpBarSr.visibility = View.GONE

                searchError = true



            }

            if (!searchError){


                Log.i("tsSearchx","setData")
                launch(Dispatchers.Main) {

                    viewPager.visibility = View.VISIBLE
                    //showBg(false)

                    pagerAdapter = PagerAdapter(this@MainActivity)
                    viewPager.adapter = pagerAdapter


                    Varss.frstDw = true

                    //loadsd()

                }

            }

        }

    }



    fun getNewUrl(position:Int,pls:Int){
        GlobalScope.launch(Dispatchers.IO) {
            //if plst != current plst return

            val id = idsList[position]
            //val vid = DtList.database!!.vidDao().getVidFromPos(position)
            //val id = vid.idVid
            val vid = DtList.database!!.vidDao().getVidFromId(id)
            val nexUrl = vid.urlVid

            //val nexUrl = DtList.database!!.vidDao().getUrlFromPos(position)
            Log.i(TAG, "Vid = " + vid.nmVid)

            if (nexUrl == "" && colaVid.indexOf(id) == -1){
                colaVid.add(id)
                val urlAu:String
                val nwUrl:String
                try {

                    val st = StreamInfo.getInfo("https://m.youtube.com/watch?v=${vid.idVid}")

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
                    //Log.i("sdfgdfg", st.relatedItems.toString())
                    //Log.i("sdfgdfg", st.relatedItems.size.toString())
                }catch (e:Exception){
                    launch(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "errorr", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }finally {
                    //VID ya no enquee
                }

                ////val pos = Varss.listIds.indexOf(idNw)

                if (pls != Varss.crtnPlst) return@launch

                DtList.database!!.vidDao().setUrlVid(vid.idVid,nwUrl,urlAu)


                launch(Dispatchers.Main) {

                    Log.i(TAG, "hay ${simpleExoPlayer.mediaItemCount} item media")

                    dwImg(nwUrl,simpleExoPlayer.mediaItemCount)
                    DtList.setPos(vid.idVid,simpleExoPlayer.mediaItemCount)

                    val mediaItem = MediaItem.fromUri(nwUrl)
                    simpleExoPlayer.addMediaItem(simpleExoPlayer.mediaItemCount,mediaItem)//
                    simpleExoPlayer.prepare()

                    pagerAdapter?.notifyDataSetChanged()

                }

                //IMPORTANTE SI UN ENLACE NO SE HAÑADE PARA Q VUELVA A INTENTAR
                //colaVid.remove(id)
                Log.i(TAG,"Lego $position")
            }else{

                Log.i(TAG,"YA OBTUVOOOO $position")
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

            launch(Dispatchers.Main) {
            //Toast.makeText(this@MainActivity, "oooooooo", Toast.LENGTH_SHORT).show()
            // importante no se puede lanza toas preguntar para despejar dudas
            // imgx.setImageBitmap(bitmap)
            }

        }
    }



    private fun getConfigCloud() {
        FirebaseRC.fetch {
        }

    }


    fun updt(tp: String, url: String) {
        val intn_updt = Intent(this, UpdateActivity::class.java)
        intn_updt.putExtra("updtUrl", url)
        when (tp) {
            "A" -> {
                intn_updt.putExtra("updtTp", "A")
            }
            "B" -> {
                intn_updt.putExtra("updtTp", "B")

            }
        }
        startActivity(intn_updt)

    }



    //Internet
    private fun internetState() {
        Log.i("TestRmt","gintenetSate")
        var fv = true
        var initStApp = false
        NetworkConnection.initialize(this)
        lifecycleScope.launchWhenStarted {
            NetworkConnection.isConnected.collect {isConnected ->
                if (isConnected) {
                    internet = true

                    if (fv){
                        if (!FirebaseRC.obDt){
                            //Varss.dsMr = true
                            getConfigCloud()
                        }
                        fv = false
                    }

                    //Si vulge si se tiene qeu activar y no se inicializo inicializar
                    /*if (!Vungle.isInitialized() && !fv){
                    //initVungle()
                    }
                    if (!initStApp){
                        initStarApp()
                        initStApp = true
                    }*/


                }else{
                    internet = false
                    //se setearia cada vex q no halla hacer q sea solo una vez
                    FirebaseRC.setConfigCloud()

                    val intent = Intent(this@MainActivity, WifiActivity::class.java)
                    startActivity(intent)

                    //WIFI
                    //Detener video   -> SE TENDRIA Q REALIZAR SOLO CUANDO SE COMPLETE EL SET DEL PAYER
                    //YA QUE EN ON STOP SE TENDRIA Q DETENER EL VIDEO
                    //val intent = Intent(this@MainActivity, WifiActivity::class.java)
                    //startActivity(intent)
                    //fv = false
                }
            }
        }
    }




    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.i("NewIntxa1", intent?.data.toString())
        Log.i("NewIntxa2", intent?.getStringExtra(Intent.EXTRA_TEXT).toString())
        Intentsx.New(intent)
    }


    //ADSX

    fun initStarApp() {
        val ageUser = storage.getInt(Constants.SV_AGE,22)
        val genUser = storage.getInt(Constants.SV_GEN,0)

        val gen = if (genUser == 0){
            SDKAdPreferences.Gender.MALE
        }else{
            SDKAdPreferences.Gender.FEMALE
        }

        val sdkAdPreferences = SDKAdPreferences().setAge(ageUser).setGender(gen)
        StartAppSDK.init(this,Constants.ID_APP_ST,sdkAdPreferences,false)


    }


    fun initVungle(){

        Vungle.init(Constants.ID_APP_VG, applicationContext, object : InitCallback {
            override fun onSuccess() {
                Log.i("testAds", "Vungle se inicio")
                setBnrAds()
            }

            override fun onError(exception: VungleException?) {
                setBnrAds()
                Log.i("testAds", "Vungle NO inicio ${exception?.exceptionCode}")
                FirebaseEvent.send("vgInitEr","er", exception?.exceptionCode.toString())
            }
            override fun onAutoCacheAdAvailable(placementId: String?) {
                Log.i("testAds", "onAuto")
            }
        })

    }

    fun initItrs(dsItr:Int) {

        tmrCntIrts.postDelayed({
            if (dsItr != 3){
                setItrAds(dsItr) //ini ad 1->vungle 2-<St
            }
        }, Constants.TM_AD_ITR)

    }

    fun setItrAds(dsItr:Int) {
        var crnAd = when(dsItr){
            1 -> 2
            2 -> 1
            else -> 1
        }

        tmrIrts.post(object : Runnable {
            override fun run() {

                Toast.makeText(this@MainActivity, "Puede New Ad", Toast.LENGTH_SHORT).show()
                if (internet){

                    if (!Vungle.isInitialized() && dsItr != 2){
                        crnAd = 2
                    }

                    Varss.pdIrts = true

                    if (crnAd == 1) {

                        Varss.pdIrtsTp = "vungle"
                        if (dsItr!= 2) crnAd = 2

                    } else {

                        Varss.pdIrtsTp = "starApp"
                        if (dsItr!= 1) crnAd = 1

                    }

                }


                tmrIrts.postDelayed(this, Constants.TM_AD_ITR)
            }
        })
    }

    fun showIrts(){
        if (Varss.pdIrtsTp == "vungle"){
            initVungleInterstitial()
        }else{
            initStartAppInterstitial()
        }
    }



    fun stopTmrItrs(){
        tmrIrts.removeCallbacksAndMessages(null)
        tmrCntIrts.removeCallbacksAndMessages(null)
    }


    fun initVungleInterstitial(){
        Vungle.loadAd(Constants.ID_INT_VG, object : LoadAdCallback {
            override fun onAdLoad(id: String) {

                if (Vungle.canPlayAd(Constants.ID_INT_VG)) {
                    Vungle.playAd(Constants.ID_INT_VG, adcfg, vunglePlayAdCallback("Itrs"))
                }else{
                    Log.i("testAds", "\n Interstitial no Play Ad")
                }

                Log.i("testAds", "\n Vungle interstitial onLoad")

            }
            override fun onError(id: String, e: VungleException) {
                Log.i("testAds", "\n Vungle Interstitial error : ${e.localizedMessage} ")
                FirebaseEvent.send("vgItrLdEr","er", e.exceptionCode.toString())
            }
        })
    }

    var stVidAdx = false
    private fun initStartAppInterstitial(){

        startAppAd.loadAd(adPrederence,object : AdEventListener {
            override fun onReceiveAd(ad: Ad) {
                //stVidAdx = mainWebView.currentIsPlaying
                startAppAd.showAd(startAppAdCallback())
                Log.i("testAds", "\n StartApp Receive Interstitial")

            }
            override fun onFailedToReceiveAd(ad: Ad) {
                Log.i("testAds", " StartApp failed Receive Interstitial ${ad.errorMessage.toString()}")
                FirebaseEvent.send("stItrLdEr","er", ad.errorMessage.toString())
            }
        })


    }


    fun setBnrAds() {
        /*if (internet){

            if (!Vungle.isInitialized() && dsItr != 2) {
            }
        }*/

        when(FirebaseRC.dsBnr){
            0 -> {
                val n = (0..1).random()
                Log.i("lod", n.toString())
                if (n == 0) {
                    if (Vungle.isInitialized()) initVungleBanner() else initStarAppBanner()
                } else {
                    initStarAppBanner()
                }
            }
            1 -> if (Vungle.isInitialized()) initVungleBanner()
            2 -> initStarAppBanner()
            else -> "Bnr ad Disable"
        }

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
                /*if (!appOpen){
                    startAppBanner?.hideBanner()
                }*/
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


    fun initVungleBanner(){
        clrBnrVg()

        val adConfig = BannerAdConfig()
        adConfig.adSize = AdConfig.AdSize.BANNER
        Banners.loadBanner(Constants.ID_BNR_VG, adConfig, object : LoadAdCallback {
            override fun onAdLoad(id: String) {
                if (Banners.canPlayAd(Constants.ID_BNR_VG,adConfig.adSize)){
                    Log.i("testAds", "\n Vungle banner onAdLoad  + id ")
                    vungleBanner = Banners.getBanner(id, adConfig, App.getMainActivity()?.vunglePlayAdCallback("Bnr"))
                    bnrTop.addView(vungleBanner)
                    /*if (!appOpen){
                        vungleBanner?.setAdVisibility(false)
                    }*/
                }

            }

            override fun onError(id: String, e: VungleException) {
                Log.i("testAds", "\n Vungle banner loaderror : ${e.localizedMessage} ")
                FirebaseEvent.send("vgBnrLdEr","er", e.exceptionCode.toString())
            }
        })


    }

    private fun clrBnrSt(){
        if (startAppBanner != null){
            startAppBanner!!.hideBanner()
            startAppBanner = null
            bnrTop.removeAllViews()
        }
    }

    private fun clrBnrVg(){
        if (vungleBanner != null){
            vungleBanner?.destroyAd()
            vungleBanner = null
            bnrTop.removeAllViews()
        }

    }



    fun vunglePlayAdCallback(identity: String) = object : PlayAdCallback {
        override fun creativeId(creativeId: String?) {
            //TODO("Not yet implemented")
        }

        override fun onAdStart(id: String?) {
            Log.i("testAds", "\n Vungle $identity onAdStart ")
        }

        override fun onAdEnd(id: String?, completed: Boolean, isCTAClicked: Boolean) {
            Log.i("testAds", "\n Vungle $identity onAdEnd $completed $isCTAClicked ")
        }

        override fun onAdEnd(id: String?) {
            Log.i("testAds", "\n Vungle $identity onAdEnd2 ")
        }

        override fun onAdClick(id: String?) {
            Log.i("testAds", "\n Vungle $identity onAdClick $id")
            FirebaseEvent.send("vg${identity}Cl","", "")
        }

        override fun onAdRewarded(id: String?) {
            Log.i("testAds", "\n Vungle $identity onAdRewarded ")
            FirebaseEvent.send("vg${identity}Rw","", "")
        }

        override fun onAdLeftApplication(id: String?) {
            Log.i("testAds", "\n Vungle $identity onAdLeftApplication ")
        }

        override fun onError(id: String?, exception: VungleException?) {
            Log.i("testAds", "\n Vungle $identity onError + ${exception?.message} ")
            FirebaseEvent.send("vg${identity}Er","er", exception?.exceptionCode.toString())
        }

        override fun onAdViewed(id: String?) {
            Log.i("testAds", "\n Vungle $identity onAdViewed ")
            FirebaseEvent.send("vg${identity}Vw","", "")
        }

    }

    private fun startAppAdCallback() = object : AdDisplayListener{
        override fun adHidden(p0: Ad?) {
            Log.i("testAds", "\n StartApp Itrs adHidden ")
        }

        override fun adDisplayed(p0: Ad?) {
            Log.i("testAds", "\n StartAppItrs  adDisplayed ")
            if (stVidAdx){
                //mainWebView.playVideo()
                stVidAdx = false
            }
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


    private fun loadNativeAd() {
        val nativePrefs = NativeAdPreferences()
            .setAdsNumber(1)
            .setPrimaryImageSize(4)
            .setSecondaryImageSize(0)

        val nativeAd = StartAppNativeAd(this)
        nativeAd.loadAd(nativePrefs, object : AdEventListener {
            override fun onReceiveAd(ad: Ad) {
                Log.i("comolasmodas","onReceiveAd")
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
                    Glide.with(this@MainActivity).load(ad.secondaryImageUrl).error(R.drawable.er_glide).into(adNtvIc)
                    adNtvTl.text = ad.title
                    adNtvDes.text = ad.description
                    adNtvBtn.text = if (ad.callToAction != "") ad.callToAction else if (ad.isApp) "Install" else "Open"

                    ad.registerViewForInteraction(ctnNtvAd)

                }else{
                    //holder.itemView.visibility = View.GONE
                }
            }

            override fun onFailedToReceiveAd(ad: Ad) {
                Log.i("comolasmodas","onFailed")
            }
        })
        Log.i("comolasmodas","onReceiveAdfIN")
    }


    fun deleteNtvAd(){
        //ctnNtvAd.removeAllViews()
        ctnNtvAd.visibility = View.GONE
    }


    fun showAcMsj(msjx:String,urlMsj:String){
        storage.edit().putString(Constants.SV_LAST_MSJ, msjx).apply()
        val intent_msj = Intent(this, MensajeActivity::class.java)
        intent_msj.putExtra("msjx",msjx)
        intent_msj.putExtra("urlMsjx",urlMsj)
        App.getMainActivity()?.startActivity(intent_msj)

    }

    fun removeTxEr(idTx:Int){
        ctnLA.removeView(findViewById(idTx))
    }


    fun getUpdMsSh():Int {
        return storage.getInt(Constants.SV_UPD_MSJ_SH,0)
    }


    fun putUpdMsSh(vs:Int){
        storage.edit().putInt(Constants.SV_UPD_MSJ_SH, vs).apply()
    }



    //ONBACK
    override fun onBackPressed() {

        if (viewPager.currentItem == 1){
            viewPager.currentItem = 0
        }else{
            super.onBackPressed()
        }


    }


    private fun releasePlayer() {
        simpleExoPlayer.release()
    }


    fun stFocusVideo(st:Boolean){
        if (st){
            //val pnl = (activity as ListActivity).quBhvr.state
            /*if (pnl == BottomSheetBehavior.STATE_EXPANDED) {
                simpleExoPlayer.playWhenReady = true
                simpleExoPlayer.playbackState
            }*/

        }else{
            simpleExoPlayer.pause()
            simpleExoPlayer.playWhenReady = false
            simpleExoPlayer.playbackState


        }

    }

    override fun onDestroy() {
        super.onDestroy()

        App.releaseAndReAcquireLocks(false)

        Log.i("tsd","SALIOOOO")
        if (Util.SDK_INT > 23) releasePlayer()
    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            if (playbackState == Player.STATE_BUFFERING) {
                //progressBar.visibility = View.VISIBLE
                //playerView.useController = false

            } else {
                //progressBar.visibility = View.GONE
                //playerView.useController = true
            }
            //logx("onPlayerStateChanged || $playWhenReady || $playbackState")
        }


        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> {
                    /*if (DtList.getVidFromPos(0).idVid == vidCurrent){
                        sh_exo_prev.alpha = 0.2f
                    }else{
                        sh_exo_prev.alpha = 1f

                    }*/

                    //showPanel(false)
                    "ExoPlayer.STATE_READY     -"
                }
                ExoPlayer.STATE_ENDED -> {
                    //showPanel(true)
                    //AdControler.showAd()
                    "ExoPlayer.STATE_ENDED     -"
                }
                else -> "UNKNOWN_STATE             -"
            }
            Log.i("tslistenr", "changed state to $stateString || $playbackState")
            if (stateString == "ExoPlayer.STATE_READY     -"){
                //nextVideo()
                Log.i("tslistenr", "changed state tooooooooooo")
                //currentFg?.hideImg()
                //if esta en help
                showBg(false)
                val fg = pagerAdapter!!.hashMap[viewPager.currentItem] as ShowFragment?
                fg?.hideImg()

                if (Varss.inHelp){
                    simpleExoPlayer.pause()
                }

                deleteNtvAd()
            }
        }

    }



    private fun showTxNoFound() {

        Log.i("tsSearchx","showTxNoFound1")

        GlobalScope.launch(Dispatchers.Main) {
            Log.i("tsSearchx","showTxNoFound2")
            showBg(false)

            //(scOpBarSr.drawable as? Animatable)?.stop()
            //scOpBarSr.visibility = View.GONE

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

    fun addTabs(){
        mnList.forEach {
            tabLayout.addTab(tabLayout.newTab().setText(it.title))
        }
    }

    fun logx(ch:String,ms:String){
        Log.i(ch,ms)
    }


    fun saveLastSing() {
        //SALVAR LA ULTIMA LIST QUE ESTUVO REPRODUCIENDO PARA VOLVER A ELLA CUANDO REGRESE
        //storage.edit().putString(SAVE_LAST_SING, mainWebView.currentVideoId).apply()
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

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    hideKeyboard(v)
                }
            }
        }
        return super.dispatchTouchEvent(event)
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





}