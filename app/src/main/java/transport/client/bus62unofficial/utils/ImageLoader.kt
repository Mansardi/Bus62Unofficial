package transport.client.bus62unofficial.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Collections
import java.util.WeakHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import transport.client.bus62unofficial.R

class ImageLoader(context : Context) {

    var memoryCache = MemoryCache()
    var fileCache : FileCache = FileCache(context)
    private var  imageViews : MutableMap<ImageView, String>? = Collections.synchronizedMap(WeakHashMap<ImageView, String>())
    var executorService : ExecutorService = Executors.newFixedThreadPool(5)

    var stub_id : Int = R.drawable.navigation_empty_icon//no_image;

    fun DisplayImage(url : String , imageView: ImageView)
    {
        imageViews!!.put(imageView, url)
        var bitmap : Bitmap? = memoryCache.get(url)
        if(bitmap!=null)
            imageView.setImageBitmap(bitmap)
        else
        {
            queuePhoto(url, imageView)
            imageView.setImageResource(stub_id)
        }
    }

    private fun queuePhoto(url : String , imageView : ImageView )
    {
        var p : PhotoToLoad = PhotoToLoad(url, imageView)
        executorService.submit(PhotosLoader(p, fileCache, memoryCache))
    }

    companion object fun getBitmap(url : String) : Bitmap?
    {
        var f :File = fileCache.getFile(url)

        //from SD cache
        var b : Bitmap? = decodeFile(f)
        if(b!=null)
            return b

        //from web
        try {
            var bitmap : Bitmap?
            var imageUrl : URL = URL(url)
            var conn : HttpURLConnection  = imageUrl.openConnection() as HttpURLConnection
            conn.connectTimeout = 30000
            conn.readTimeout = 30000
            conn.instanceFollowRedirects = true
            var inpitStream : InputStream =conn.inputStream

            var outputStream : OutputStream  = FileOutputStream(f)
            Utils.copyStream(inpitStream, outputStream)
            outputStream.close()
            bitmap = decodeFile(f)
            return bitmap
        } catch (ex : Exception ){
            ex.printStackTrace()
            return null
        }
    }

    //decodes image and scales it to reduce memory consumption
    private fun decodeFile(f : File) : Bitmap? {
        try {
            //decode image size
            var o : BitmapFactory.Options  = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(FileInputStream(f),null, o)

            //Find the correct scale value. It should be the power of 2.
            val REQUIRED_SIZE = 70
            var width_tmp : Int = o.outWidth
            var height_tmp : Int = o.outHeight
            var scale = 1
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break
                width_tmp/=2
                height_tmp/=2
                scale*=2
            }

            //decode with inSampleSize
            var o2 : BitmapFactory.Options  = BitmapFactory.Options()
            o2.inSampleSize=scale
            return BitmapFactory.decodeStream( FileInputStream(f), null, o2)
        } catch (e : FileNotFoundException) {}
        return null
    }

    //Task for the queue
    class PhotoToLoad (var url : String, var imageView : ImageView)

    inner class PhotosLoader(var photoToLoad : PhotoToLoad, var fileCache : FileCache, var memoryCache: MemoryCache) : Runnable {

    override fun run() {
            if(imageViewReused(photoToLoad))
                return
        var bmp : Bitmap? = getBitmap(photoToLoad.url)
        memoryCache.put(photoToLoad.url, bmp)
        if(imageViewReused(photoToLoad))
                return
        var bd : BitmapDisplayer = BitmapDisplayer(bmp!!, photoToLoad)
        var a : Activity = photoToLoad.imageView.context as Activity
        a.runOnUiThread(bd)
    }





    }

    fun imageViewReused(photoToLoad : PhotoToLoad) : Boolean{
        val tag : String? = imageViews!!.get(photoToLoad.imageView)!!
        if(tag==null || !tag.equals(photoToLoad.url)) {
            return true
        }
        return false
    }


    //Used to display bitmap in the UI thread
    inner class BitmapDisplayer(var bitmap : Bitmap?, var photoToLoad: PhotoToLoad) : Runnable
    {
        override fun run()
        {
            if(imageViewReused(photoToLoad))
                return
            if(bitmap!=null)
                photoToLoad.imageView.setImageBitmap(bitmap)
            else
                photoToLoad.imageView.setImageResource(stub_id)
        }
    }

    fun clearCache() {
        memoryCache.clear()
        fileCache.clear()
    }
}