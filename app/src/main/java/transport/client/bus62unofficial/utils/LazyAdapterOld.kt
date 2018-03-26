package transport.client.bus62unofficial.utils

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import transport.client.bus62unofficial.R
import java.util.ArrayList
import java.util.HashMap

class LazyAdapterOld(a: Activity, var data : ArrayList<HashMap<String, String>>) : BaseAdapter() {

    companion object {
        @JvmStatic lateinit var inflater: LayoutInflater
    }

    init {
        inflater =  a.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    private var imageLoader : ImageLoader = ImageLoader(a.applicationContext)

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var vi : View? = p1

        if(p1==null)
            vi = inflater.inflate(R.layout.stations_row, null)

        var station: HashMap<String, String> = data.get(p0)

        val title : TextView = vi!!.findViewById(R.id.title)
        val descr : TextView = vi.findViewById(R.id.descr)
        //val duration : TextView  = vi.findViewById(R.id.duration)
//        var thumb_image : ImageView = vi.findViewById(R.id.list_image)
        // Setting all values in listview
        title.text = station.get("name")
        descr.text = station.get("descr")
        //duration.text = ("")
        //imageLoader.DisplayImage(song.get(CustomizedListView.KEY_THUMB_URL), thumb_image);
        return vi
    }

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return data.size
    }

    fun addAll(items: ArrayList<HashMap<String, String>>) {

    }
}