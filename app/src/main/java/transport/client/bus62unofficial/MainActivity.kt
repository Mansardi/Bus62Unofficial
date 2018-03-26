package transport.client.bus62unofficial

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import transport.client.bus62unofficial.common.Searchable
import transport.client.bus62unofficial.ui.ForecastFragment
import transport.client.bus62unofficial.ui.StationsFragment

class MainActivity : AppCompatActivity(), StationsFragment.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val transaction = fragmentManager.beginTransaction()
            var mainScreen = getStringPref("main_screen")
            when (mainScreen) {
                R.id.navigation_home.toString(), "" -> {
                    transaction.replace(R.id.frame_layout, StationsFragment(), mainScreen)
                }
                R.id.navigation_favorites.toString() -> {
                    transaction.replace(R.id.frame_layout, StationsFragment(), mainScreen)
                }
                R.id.navigation_notifications.toString() -> {
                    transaction.replace(R.id.frame_layout, StationsFragment(), mainScreen)
                }
            }
            transaction.commit()
        }

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
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var fragment: Fragment? = null
        val tag = item.itemId.toString()
        val transaction = fragmentManager.beginTransaction()

        when (item.itemId) {
            R.id.navigation_home -> {
                fragment = fragmentManager.findFragmentByTag(tag)
                if (fragment == null) {
                    fragment = StationsFragment()
                    transaction.add(R.id.frame_layout, fragment, tag)
                }
            }
            R.id.navigation_favorites -> {
                fragment = fragmentManager.findFragmentByTag(tag)
                if (fragment == null) {
                    fragment = StationsFragment()
                    transaction.add(R.id.frame_layout, fragment, tag)
                }
            }
            R.id.navigation_notifications -> {
                fragment = fragmentManager.findFragmentByTag(tag)
                if (fragment == null) {
                    fragment = StationsFragment()
                    transaction.add(R.id.frame_layout, fragment, tag)
                }
            }
        }

        if (fragment == null)
            return@OnNavigationItemSelectedListener false

        transaction.show(fragment)
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

                val fragment = fragmentManager.findFragmentById(R.id.frame_layout) as Searchable
                fragment.applyFilter(query)
                searchViewAndroidActionBar.tag = query
                return true

            }

            override fun onQueryTextChange(newText: String): Boolean {
                val fragment = fragmentManager.findFragmentById(R.id.frame_layout) as Searchable
                fragment.applyFilter(newText)
                searchViewAndroidActionBar.tag = newText
//                if (newText.isEmpty() && searchViewAndroidActionBar.tag != null) {
//                    val fragment = fragmentManager.findFragmentById(R.id.frame_layout) as Searchable
//                    fragment.applyFilter("")
//                }
                return false
            }
        })

        searchViewAndroidActionBar.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("ApplySharedPref")
    private fun setStringPref(param: String, value: String) {
        applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE).edit().putString(param, value).commit()
    }

    private fun getStringPref(param: String): String {
        return applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE).getString(param, "").toString()
    }

    override fun onDestroy() {
        setStringPref("main_screen", navigation.selectedItemId.toString())

        super.onDestroy()
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        } else {
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        }

        return super.onOptionsItemSelected(item)
    }
}
