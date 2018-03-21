package transport.client.bus62unofficial.stations

import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import transport.client.bus62unofficial.R
import transport.client.bus62unofficial.database.DBHelper
import java.util.*

class StationsAdapter(activity: Activity, var data : ArrayList<HashMap<String, String>>) : RecyclerView.Adapter<StationsViewHolder>()  {

    private var db : SQLiteDatabase

    companion object {
        @JvmStatic lateinit var inflater: LayoutInflater
    }

    init {
        inflater =  activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        db = DBHelper(activity).writableDatabase
    }

    override fun onBindViewHolder(holder: StationsViewHolder, position: Int) {
        val record = data[position]
        holder.title.text = record["name"]
        holder.descr.text = record["descr"]
        holder.favoriteButtonListener.setRecord(record)
        holder.favoriteButton.tag = false

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

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): StationsViewHolder {
        val v = LayoutInflater.from(parent!!.context).inflate(R.layout.list_row, parent, false)
        return StationsViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}