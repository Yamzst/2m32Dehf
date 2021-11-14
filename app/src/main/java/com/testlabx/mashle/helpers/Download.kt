package com.testlabx.mashle.helpers

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaMuxer
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import com.testlabx.mashle.utils.Utilsx
import com.testlabx.mashle.utils.sharedApp
import okhttp3.*
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.*

object Download {

    //converter class

    val TAG = "tsDwll"
    private lateinit var client : OkHttpClient
    var dwsEnqueue =  0

    fun initHttp3(){
        dwsEnqueue = 0
        client = OkHttpClient()
    }


   fun downloadVideo(ctn:Context,url:String,tp:String,nm:String,completion: (ptSv:Uri?) -> Unit) {
       dwsEnqueue += 1
       Log.i(TAG,"iniiooooooooooo")
       val request: Request = Request.Builder().header("Range", "bytes=0-").url(url).build()
       //val request: Request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                dwsEnqueue -= 1
                completion(null)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i(TAG, "Descargado")
                if (response.isSuccessful) {
                    Log.i(TAG, "Descargado isSuccessfull")

                    var tmpFileDw:File? = null
                    var tmpFilePr:File? = null
                    var outUri:Uri? = null

                    try {

                        tmpFileDw = File.createTempFile("vid", null, ctn.applicationContext.externalCacheDir)
                        tmpFileDw.delete()
                        tmpFileDw.mkdir()
                        tmpFileDw.deleteOnExit()
                        //hasta aui folder
                        ///Log.i(TAG, tmpFileDw.absolutePath)
                        val dwFile =  if (tp == "video"){
                            File(tmpFileDw, "flDw.mp4")
                        }else{
                            File(tmpFileDw, "flDw.m4a")
                        }

                        val sink: BufferedSink = dwFile.sink().buffer()
                        sink.writeAll(response.body!!.source())
                        sink.close()
                        Log.i(TAG, "Descarga Guardada $tp")

                        tmpFilePr = File.createTempFile("vid", null, ctn.externalCacheDir)
                        tmpFilePr.delete()
                        tmpFilePr.mkdir()
                        tmpFilePr.deleteOnExit()

                        val prFile = if (tp == "video"){
                            File(tmpFilePr, "flPs.mp4")
                        }else{
                            File(tmpFilePr, "flPs.m4a")
                        }

                        if (tp == "video"){
                            val movie = MovieCreator.build(dwFile.absolutePath)
                            val tracks = movie.tracks
                            movie.tracks = LinkedList()

                            for (track in tracks) {
                                movie.addTrack(AppendTrack(track))
                            }

                            val out = DefaultMp4Builder().build(movie)
                            val fos = FileOutputStream(prFile)
                            val fc = fos.channel
                            out.writeContainer(fc)
                            fc.close()
                            fos.close()
                        }else{
                            val result = MovieCreator.build(dwFile.absolutePath)
                            val out = DefaultMp4Builder().build(result)

                            //preguntar si todos los que se tiene que cerrar se esta cerrando

                            val fos = FileOutputStream(prFile)
                            Log.i(TAG, "Write")
                            out.writeContainer(fos.channel)
                            fos.close()
                            Log.i(TAG, "Write close")
                        }




                        tmpFilePr.listFiles().forEach {
                            Log.i("$TAG File", it.toString())
                            Log.i("$TAG File", it.name)

                            outUri = getUri(ctn,nm,tp)

                            Log.i(TAG, outUri.toString())
                            it.inputStream().saveToMusicFolder(ctn, outUri!!)

                            Log.i(TAG, "Copiado a MediaStore")
                            Log.i(TAG,"Listo")

                            sharedFile(ctn, outUri!!,tp)

                        }
                        Log.i(TAG, "${tmpFilePr.listFiles().size.toString()} ll")
                        Log.i(TAG, "deleteRecursi")




                    } catch (e:Exception){
                        e.message?.let { Log.i(TAG, it) }
                        //event
                    }finally {
                        dwsEnqueue -= 1
                        tmpFileDw!!.deleteRecursively()
                        tmpFilePr!!.deleteRecursively()
                        completion(outUri)

                    }

                } else {
                    dwsEnqueue -= 1
                    completion(null)
                    //Handle the error
                }
            }
        })


    }



    fun getUri(ctn:Context,nm:String,tp:String): Uri {

        val newUri =  if (tp == "video"){
            val contentValues = ContentValues()
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/Tayed")
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, "$nm.mp4")
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            val uri = ctn.contentResolver.insert(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            uri
        }else{
            val contentValues = ContentValues()
            contentValues.put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/Tayed")
            contentValues.put(MediaStore.Audio.Media.DISPLAY_NAME, "$nm.m4a")
            contentValues.put(MediaStore.Audio.Media.MIME_TYPE, "audio/m4a")
            val uri = ctn.contentResolver.insert(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            uri
        }

        return newUri!!

    }

    fun InputStream.saveToMusicFolder(ctn:Context,uri: Uri) {
        ctn.contentResolver.openOutputStream(uri)!!.use { copyTo(it) }
        close()
    }



    fun sharedFile(ctn:Context,uri:Uri,tp: String){
        val sharedIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)//"content://media/external/video/media/2438".toUri()
            type = if (tp == "video") "video/mp4" else "audio/m4a"
        }
        ctn.startActivity(Intent.createChooser(sharedIntent, "holiss"))
    }





}