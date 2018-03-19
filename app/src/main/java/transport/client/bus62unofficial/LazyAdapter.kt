package transport.client.bus62unofficial

import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import transport.client.bus62unofficial.database.DBHelper
import java.util.*

class LazyAdapter(activity: Activity, var data : ArrayList<HashMap<String, String>>) : RecyclerView.Adapter<ViewHolder>()  {

    private var db : SQLiteDatabase

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = data[position]
        holder.title.text = record["name"]
        holder.descr.text = record["descr"]
        holder.favoriteButtonListener.setRecord(record)
        holder.favoriteButton.tag = false;

        val c = db.query("stations", null, "name = ? and descr = ?", arrayOf(record["name"], record["descr"]), null, null, null)
        if (c.moveToFirst()) {
            holder.favoriteButton.setImageResource(R.drawable.ic_star_black_24dp)
            holder.favoriteButton.tag = "true";
        }
        else {
            holder.favoriteButton.setImageResource(R.drawable.ic_star_border_black_24dp)
            holder.favoriteButton.tag = "false";
        }

        c.close()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent!!.context).inflate(R.layout.list_row, parent, false)
        return transport.client.bus62unofficial.ViewHolder(v)
    }

    companion object {
        @JvmStatic lateinit var inflater: LayoutInflater
    }

    init {
        inflater =  activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        db = DBHelper(activity).writableDatabase
    }

    fun addAll(items: ArrayList<HashMap<String, String>>) {
        data.addAll(items)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}


//private var imageLoader : ImageLoader = ImageLoader(a.applicationContext)

//    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
//        var vi : View? = p1
//
//        if(p1==null)
//            vi = inflater.inflate(R.layout.list_row, null)
//
//        var station: HashMap<String, String> = data.get(p0)
//
//        val title : TextView  = vi!!.findViewById(R.id.title)
//        val descr : TextView  = vi.findViewById(R.id.descr)
//        //val duration : TextView  = vi.findViewById(R.id.duration)
////        var thumb_image : ImageView = vi.findViewById(R.id.list_image)
//        // Setting all values in listview
//        title.text = station.get("name")
//        descr.text = station.get("descr")
//        //duration.text = ("")
//        //imageLoader.DisplayImage(song.get(CustomizedListView.KEY_THUMB_URL), thumb_image);
//        return vi
//    }
//
//    override fun getItem(p0: Int): Any {
//        return p0
//    }
//
//    override fun getItemId(p0: Int): Long {
//        return p0.toLong()
//    }
//
//    override fun getCount(): Int {
//        return data.size
//    }
