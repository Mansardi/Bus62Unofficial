package transport.client.bus62unofficial.stations

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import java.util.*


abstract class StationsListLoader(private val activity: Activity, recyclerView: RecyclerView) {
    private var itemsPerPage: Int = 0
    private var items: ArrayList<HashMap<String, String>> = ArrayList()
    private var loadingMore: Boolean = false

    private var adapter: RecyclerView.Adapter<*>? = null

    private val scrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, scrollState: Int) {}

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            var mLayoutManager = recyclerView.layoutManager as LinearLayoutManager

            val visibleItemCount = mLayoutManager.childCount//смотрим сколько элементов на экране
            val totalItemCount = mLayoutManager.itemCount//сколько всего элементов
            val firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition()//какая позиция первого элемента
            val lastInScreen = firstVisibleItem + visibleItemCount
            if (lastInScreen == totalItemCount && !loadingMore) {
                loadItems()
            }
        }
    }

    private val loadMoreListItemsRunnable = Runnable {
        loadingMore = true

        onLoadItems(items, itemsPerPage)

        activity.runOnUiThread(updateAdapterRunnable)
    }

    private val updateAdapterRunnable = Runnable {
        adapter!!.notifyDataSetChanged()
        loadingMore = false
    }

    init {
        recyclerView.setOnScrollListener(scrollListener)
    }

    fun createAdapter() : RecyclerView.Adapter<*>? {
        adapter = StationsAdapter(activity, items)
        return adapter
    }

    /**
     * Задаёт количество элементов на страницу.
     * Используется при загрузке новых элементов для ограничения загружаемых данных.
     * @param value количество элементов
     */
    fun setItemsPerPage(value: Int) {
        itemsPerPage = value
    }

    fun loadItems() {
        val thread = Thread(loadMoreListItemsRunnable)
        thread.start()
    }

    /**
     * Метод загрузки новых элементов списка.
     * Выполняется в отдельном потоке.
     * @param items список для заполнения
     * @param itemsPerPage количество элементов на страницу (по умолчанию 10)
     */
    abstract fun onLoadItems(items: ArrayList<HashMap<String, String>>, itemsPerPage: Int)
}