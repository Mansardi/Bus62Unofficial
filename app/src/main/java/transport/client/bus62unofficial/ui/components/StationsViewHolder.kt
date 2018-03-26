package transport.client.bus62unofficial.ui.components

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import transport.client.bus62unofficial.R
import transport.client.bus62unofficial.database.DBHelper

class StationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var title: TextView = itemView.findViewById(R.id.title)
    var descr: TextView = itemView.findViewById(R.id.descr)
    var favoriteButton: ImageView = itemView.findViewById(R.id.favorite)
    var favoriteButtonListener: FavoriteButtonListener = FavoriteButtonListener()

    init {
        favoriteButton.setOnClickListener(favoriteButtonListener)
    }


    class FavoriteButtonListener : View.OnClickListener {
        private var record: HashMap<String, String>? = null

        override fun onClick(v: View) {
            val db = DBHelper(v.context).writableDatabase
            val name = record?.get("name")
            val descr = record?.get("descr")

            if (v.tag == "false") {
                db.execSQL("INSERT INTO stations (name, descr) VALUES ('$name','$descr')")
                (v as ImageView).setImageResource(R.drawable.ic_star_black_24dp)
                v.tag = "true";
                Toast.makeText(v.context, "Остановка добавлена", Toast.LENGTH_SHORT).show()
            } else {
                db.execSQL("DELETE FROM stations WHERE (name = '$name' and descr = '$descr')")
                (v as ImageView).setImageResource(R.drawable.ic_star_border_black_24dp)
                v.tag = "false";
                Toast.makeText(v.context, "Остановка удалена", Toast.LENGTH_SHORT).show()
            }

            db.close()
        }

        fun setRecord(record: HashMap<String, String>) {
            this.record = record
        }
    }
}

