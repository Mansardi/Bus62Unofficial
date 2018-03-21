package transport.client.bus62unofficial.forecasts

import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import transport.client.bus62unofficial.R
import transport.client.bus62unofficial.database.DBHelper
import java.util.ArrayList
import java.util.HashMap

class ForecastAdapter(activity: Activity, var data : ArrayList<HashMap<String, String>>) : RecyclerView.Adapter<ForecastViewHolder>()  {

    private var db : SQLiteDatabase

    companion object {
        @JvmStatic lateinit var inflater: LayoutInflater
    }

    init {
        inflater =  activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        db = DBHelper(activity).writableDatabase
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val record = data[position]
        holder.route.text = record["rtype"] + record["rnum"]
        holder.currentStation.text = record["lastst"]
        var minutes = (record["arrt"]).toString().toInt() / 60
        holder.time.text = if (minutes == 0) {"<1"} else {minutes.toString()} + " м."

        when (record["rtype"]) {
            "А" -> holder.list_image.setImageResource(R.drawable.ic_label_red_24dp)
            "М" -> holder.list_image.setImageResource(R.drawable.ic_label_yellow_24dp)
            "Т" -> holder.list_image.setImageResource(R.drawable.ic_label_blue_24dp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ForecastViewHolder {
        val v = LayoutInflater.from(parent!!.context).inflate(R.layout.detailed_list_row, parent, false)
        return ForecastViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}