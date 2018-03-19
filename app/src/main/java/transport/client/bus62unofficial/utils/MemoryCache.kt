package transport.client.bus62unofficial.utils

import java.lang.ref.SoftReference
import java.util.Collections
import java.util.HashMap
import java.util.Map
import android.graphics.Bitmap

class MemoryCache {
    private var cache : MutableMap<String, SoftReference<Bitmap>>? = Collections.synchronizedMap(HashMap<String,SoftReference<Bitmap>>())

    fun get (id : String ) :  Bitmap?{
        if(!cache!!.containsKey(id))
            return null
        var ref : SoftReference<Bitmap>? = cache!!.get(id)
        return ref!!.get()
    }

    fun put(id : String , bitmap : Bitmap? ){
        cache!!.put(id, SoftReference<Bitmap>(bitmap))
    }

    fun clear() {
        cache!!.clear()
    }
}