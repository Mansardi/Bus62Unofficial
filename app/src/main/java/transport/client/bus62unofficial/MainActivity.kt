package transport.client.bus62unofficial

import android.app.Fragment
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.view.Menu

class MainActivity : AppCompatActivity(), StationsFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var stationsFragment: Fragment? = null
        val tag = item.itemId.toString()

        when (item.itemId) {
            R.id.navigation_home -> {
                stationsFragment = fragmentManager.findFragmentByTag(tag)
                if (stationsFragment == null) {
                    stationsFragment = StationsFragment()
                }
            }
            R.id.navigation_favorites -> {
                stationsFragment = fragmentManager.findFragmentByTag(tag)
                if (stationsFragment == null) {
                    stationsFragment = StationsFragment()
                }
            }
            R.id.navigation_notifications -> {
                stationsFragment = fragmentManager.findFragmentByTag(tag)
                if (stationsFragment == null) {
                    stationsFragment = StationsFragment()
                }
            }
        }

        if (stationsFragment == null)
            return@OnNavigationItemSelectedListener false

        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, stationsFragment, tag)
        transaction.commit()
        return@OnNavigationItemSelectedListener true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, StationsFragment(),  R.id.navigation_home.toString())
        transaction.commit()
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
