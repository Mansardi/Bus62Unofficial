package transport.client.bus62unofficial.forecasts

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_forecasts.*

import org.json.JSONArray
import transport.client.bus62unofficial.R
import transport.client.bus62unofficial.common.Constants
import transport.client.bus62unofficial.stations.StationsFragment
import transport.client.bus62unofficial.utils.Utils


class ForecastFragment : Fragment() {

    private var forecasts: ArrayList<HashMap<String, String>> = ArrayList()
    private var mListener: StationsFragment.OnFragmentInteractionListener? = null
    private var currentStationId = ""
    private var isRefreshed: Boolean = false

    companion object {
        var STATION_ID = "stationId"
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_forecasts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        forecastsList.adapter = ForecastAdapter(activity, forecasts)
        forecastsList.layoutManager = LinearLayoutManager(activity.applicationContext)

        swipeRefresh.setOnRefreshListener { initiateRefresh() }
    }

    private fun getAdapter(): ForecastAdapter {
        return forecastsList.adapter as ForecastAdapter
    }

    override fun onStart() {
        super.onStart()

        val args = arguments
        if (args != null) {
            updateForecastView(args.getString(STATION_ID))
        } else if (currentStationId.isNotEmpty()) {
            updateForecastView(currentStationId)
        }
    }



    fun updateForecastView(stationId: String) {
        currentStationId = stationId
        swipeRefresh.post({
            swipeRefresh.isRefreshing = true
        })
        initiateRefresh()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is StationsFragment.OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onAttach(context: Activity?) {
        super.onAttach(context)
        if (context is StationsFragment.OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }


    private fun loadForecast() {
        var uri = Constants.Requests.STATION_FORECAST.format(currentStationId)
        val response = Utils.sendGet(uri)
        if (response.isEmpty()) return

        forecasts.clear()

        val jsonArray = JSONArray(response)
        for (i in 0..jsonArray.length() - 1) {
            val o = jsonArray.getJSONObject(i)
            val map = HashMap<String, String>()
            map.put("arrt", o["arrt"].toString())
            map.put("rtype", o["rtype"].toString())
            map.put("rnum", o["rnum"].toString())
            map.put("lastst", o["lastst"].toString())
            forecasts.add(map)
        }
    }

    private fun initiateRefresh() {
        isRefreshed = false

        UpdateForecastsBackgroundTask().execute()
    }

    private fun onRefreshComplete(result: Boolean) {
        swipeRefresh.isRefreshing = false
        isRefreshed = false
        if (!result) {
//                val refreshError = EventData(Event.REFRESH_ERROR, null)
//                listener.handleEvent(refreshError)
            val toast = Toast.makeText(activity, Constants.HTTP_ERROR, Toast.LENGTH_SHORT)
            toast.show()
        }

        getAdapter().notifyDataSetChanged()
    }

    fun setIsRefreshed(isRefreshed: Boolean) {
        this.isRefreshed = isRefreshed
    }

    @SuppressLint("StaticFieldLeak")
    private inner class UpdateForecastsBackgroundTask : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean? {
            loadForecast()

            setIsRefreshed(true)

//            try {
//                Thread.sleep(Constants.TASK_TICK)
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }

            return true
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)

            onRefreshComplete(result!!)
        }
    }
}
