package transport.client.bus62unofficial.ui

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_stations.*
import org.json.JSONArray
import transport.client.bus62unofficial.common.Constants
import transport.client.bus62unofficial.R
import transport.client.bus62unofficial.common.RecyclerItemClickListener
import transport.client.bus62unofficial.common.Searchable
import transport.client.bus62unofficial.utils.Utils
import transport.client.bus62unofficial.ui.components.StationsListLoader


class StationsFragment : Fragment(), Searchable {
    private var stations: ArrayList<HashMap<String, String>> = ArrayList()
    private var loadedIndices: ArrayList<Int> = ArrayList()

    private var searching: Boolean = false
    private var searchString: String = ""

    private lateinit var listLoader: StationsListLoader
    private var mListener: OnFragmentInteractionListener? = null


    init {
        loadStations()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_stations, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        list.affectOnItemClick(object : RecyclerItemClickListener.OnClickListener {
            override fun onItemClick(position: Int, view: View) {
                mListener!!.onStationSelected(stations[position].get("id").toString())
            }
        })
        list.adapter = listLoader.createAdapter()
        list.itemAnimator = DefaultItemAnimator()
        list.layoutManager = LinearLayoutManager(activity.applicationContext)
        list.setOnScrollListener(listLoader.scrollListener)
        listLoader.startLoadItems()
    }

    override fun applyFilter(filter: String) {
        val adapter: RecyclerView.Adapter<*>?

        if (filter.isEmpty()) {
            if (!searching) return;

            listLoader.setItems(getLoadedList())
            adapter = listLoader.createAdapter()
            searching = false
        } else {
            if (!searching) {
                loadedIndices = listLoader.getIndices()
            }

            val filteredStations = ArrayList<HashMap<String, String>>()
            for (s in stations) {
                if (s["name"].toString().toLowerCase().trim().contains(filter))
                    filteredStations.add(s)
            }

            listLoader.setItems(filteredStations)
            adapter = listLoader.createAdapter()
            searching = true
    }

        searchString = filter
        list.adapter = adapter
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }

        startInit()
    }

    override fun onAttach(context: Activity?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }

        startInit()
    }

    private fun startInit() {
        listLoader = object : StationsListLoader(activity) {
            override fun onLoadItems(items: java.util.ArrayList<HashMap<String, String>>, itemsPerPage: Int) {
                if (searching) return

                val length = if (items.count() + itemsPerPage > stations.count()) stations.count() else items.count() + itemsPerPage
                for (i in items.count() until length) {
                    items.add(stations[i])
                }
            }
        }

        listLoader.setItemsPerPage(50)
    }

    private fun getLoadedList(): ArrayList<HashMap<String, String>> {
        val temp = ArrayList<HashMap<String,String>>()
        for (s in loadedIndices)
        {
            temp.add(stations[s])
        }

        return temp
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    private fun loadStations() {
        val response = Utils.sendGet(Constants.Requests.STATIONS)
        if (response.isEmpty()) return

        val jsonArray = JSONArray(response)

        stations.clear()
        for (i in 0..jsonArray.length() - 1) {
            val o = jsonArray.getJSONObject(i)
            val map = HashMap<String, String>()
            map.put("id", o["id"].toString())
            map.put("name", o["name"].toString())
            map.put("descr", o["descr"].toString())
            stations.add(map)
        }
    }

    interface OnFragmentInteractionListener {
        fun onStationSelected(stationId: String)
    }

    fun RecyclerView.affectOnItemClick(listener: RecyclerItemClickListener.OnClickListener) {
        this.addOnChildAttachStateChangeListener(RecyclerItemClickListener(this, listener, null))
    }

    fun RecyclerView.affectOnLongItemClick(listener: RecyclerItemClickListener.OnLongClickListener) {
        this.addOnChildAttachStateChangeListener(RecyclerItemClickListener(this, null, listener))
    }

    fun RecyclerView.affectOnItemClicks(onClick: RecyclerItemClickListener.OnClickListener, onLongClick: RecyclerItemClickListener.OnLongClickListener) {
        this.addOnChildAttachStateChangeListener(RecyclerItemClickListener(this, onClick, onLongClick))
    }
}
