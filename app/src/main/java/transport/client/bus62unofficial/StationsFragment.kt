package transport.client.bus62unofficial

import android.app.Fragment
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_stations.*
import org.json.JSONArray
import transport.client.bus62unofficial.utils.Utils
import android.support.v7.widget.LinearLayoutManager


class StationsFragment : Fragment(), Searchable {
    private var stations: ArrayList<HashMap<String, String>> = ArrayList()
    private lateinit var filteredStations: ArrayList<HashMap<String, String>>
    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        loadStations()

        return inflater!!.inflate(R.layout.fragment_stations, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var listLoader = object : ListLoader(activity, list) {
            override fun onLoadItems(items: java.util.ArrayList<HashMap<String, String>>, itemsPerPage: Int) {
                for (i in items.count() until items.count() + itemsPerPage) {
                    items.add(stations[i])
                }
            }
        }

        listLoader.setItemsPerPage(50)
        list.adapter = listLoader.createAdapter()
        list.layoutManager = LinearLayoutManager(activity.applicationContext)
        listLoader.loadItems()
    }

    override fun applyFilter(filter: String) {
        var adapter: LazyAdapter?
        if (filter.isEmpty()) {
            adapter = LazyAdapter(activity, stations)
        } else {
            filteredStations = ArrayList()
            for (s in stations) {
                if (s.get("name").toString().toLowerCase().contains(filter))
                    filteredStations.add(s)
            }

            adapter = LazyAdapter(activity, filteredStations)
        }
        list.adapter = adapter
    }

    // TODO: Rename method, update argument and hook method into UI event
//    fun onButtonPressed(uri: Uri) {
//        if (mListener != null) {
//            mListener!!.onFragmentInteraction(uri)
//        }
//    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    private fun loadStations() {
        val response = Utils.sendGet("getStations.php?city=ryazan&info=12345")
        if (response.isEmpty()) return

        val jsonArray = JSONArray(response)

        for (i in 0..jsonArray.length() - 1) {
            val o = jsonArray.getJSONObject(i)
            val map = HashMap<String, String>()
            map.put("name", o["name"].toString())
            map.put("descr", o["descr"].toString())

            stations.add(map)
        }
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }
}
