package transport.client.bus62unofficial.ui.components

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import transport.client.bus62unofficial.R

class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var route: TextView = itemView.findViewById(R.id.route)
    var currentStation: TextView = itemView.findViewById(R.id.currentStation)
    var time: TextView = itemView.findViewById(R.id.arrt)
    var list_image: ImageView = itemView.findViewById(R.id.list_image)
    }

