package transport.client.bus62unofficial

import android.app.Fragment
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.view.Menu
import transport.client.bus62unofficial.common.Searchable
import transport.client.bus62unofficial.forecasts.ForecastFragment
import transport.client.bus62unofficial.stations.StationsFragment

class MainActivity : AppCompatActivity(), StationsFragment.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, StationsFragment(), R.id.navigation_home.toString())
            transaction.commit()
        }

        //actionBar.setDisplayHomeAsUpEnabled(true);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onStationSelected(stationId: String) {
        var forecastFragment = fragmentManager.findFragmentById(R.id.forecastsList) as ForecastFragment?
        if (forecastFragment != null) {
            forecastFragment.updateForecastView(stationId)
        } else {
            forecastFragment = ForecastFragment()
            val args = Bundle()
            args.putString(ForecastFragment.STATION_ID, stationId)
            forecastFragment.arguments = args

            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, forecastFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var stationsFragment: Fragment? = null
        val tag = item.itemId.toString()
        val transaction = fragmentManager.beginTransaction()

        when (item.itemId) {
            R.id.navigation_home -> {
                stationsFragment = fragmentManager.findFragmentByTag(tag)
                if (stationsFragment == null) {
                    stationsFragment = StationsFragment()
                    transaction.add(R.id.frame_layout, stationsFragment, tag)
                }
            }
            R.id.navigation_favorites -> {
                stationsFragment = fragmentManager.findFragmentByTag(tag)
                if (stationsFragment == null) {
                    stationsFragment = StationsFragment()
                    transaction.add(R.id.frame_layout, stationsFragment, tag)
                }
            }
            R.id.navigation_notifications -> {
                stationsFragment = fragmentManager.findFragmentByTag(tag)
                if (stationsFragment == null) {
                    stationsFragment = StationsFragment()
                    transaction.add(R.id.frame_layout, stationsFragment, tag)
                }
            }
        }

        if (stationsFragment == null)
            return@OnNavigationItemSelectedListener false


        transaction.show(stationsFragment)
        transaction.commit()
        return@OnNavigationItemSelectedListener true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_view_menu_item, menu)
        val searchViewItem = menu.findItem(R.id.action_search)

        val searchViewAndroidActionBar = MenuItemCompat.getActionView(searchViewItem) as SearchView
        searchViewAndroidActionBar.isFocusable = false
        searchViewAndroidActionBar.queryHint = getString(R.string.SearchHint)
        searchViewAndroidActionBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchViewAndroidActionBar.clearFocus()

                var fragment = fragmentManager.findFragmentById(R.id.frame_layout) as Searchable
                fragment.applyFilter(query)
                searchViewAndroidActionBar.tag = query
                return true

            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty() && searchViewAndroidActionBar.tag != null) {
                    var fragment = fragmentManager.findFragmentById(R.id.frame_layout) as Searchable
                    fragment.applyFilter("")
                }
                return false
            }
        })

        searchViewAndroidActionBar.setOnCloseListener(object: SearchView.OnCloseListener {
            override fun onClose(): Boolean {
               return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }
}
