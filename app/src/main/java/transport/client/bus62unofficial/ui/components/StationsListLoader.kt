package transport.client.bus62unofficial.ui.components

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import transport.client.bus62unofficial.ui.adapters.StationsAdapter
import java.util.*
import kotlin.collections.ArrayList


abstract class StationsListLoader(private val activity: Activity, private var items: ArrayList<HashMap<String, String>> = ArrayList()) {
    private var itemsPerPage: Int = 0
    private var loadingMore: Boolean = false

    private var adapter: RecyclerView.Adapter<*>? = null

    val scrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, scrollState: Int) {}

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            var mLayoutManager = recyclerView.layoutManager as LinearLayoutManager

            val visibleItemCount = mLayoutManager.childCount//смотрим сколько элементов на экране
            val totalItemCount = mLayoutManager.itemCount//сколько всего элементов
            val firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition()//какая позиция первого элемента
            val lastInScreen = firstVisibleItem + visibleItemCount
            if (lastInScreen == totalItemCount || lastInScreen == totalItemCount - 5  && !loadingMore) {
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

//    init {
//        recyclerView.setOnScrollListener(scrollListener)
//    }

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

    private fun loadItems() {
        val thread = Thread(loadMoreListItemsRunnable)
        thread.start()
    }

    fun startLoadItems() {
        if (items.count() > 0) return
            loadItems()
    }

    /**
     * Метод загрузки новых элементов списка.
     * Выполняется в отдельном потоке.
     * @param items список для заполнения
     * @param itemsPerPage количество элементов на страницу (по умолчанию 10)
     */
    abstract fun onLoadItems(items: ArrayList<HashMap<String, String>>, itemsPerPage: Int)

    fun getIndices(): ArrayList<Int> {
        return items.indices.toCollection(java.util.ArrayList<Int>())
    }

    fun setItems(itemss: ArrayList<HashMap<String, String>>) {
        this.items = itemss
    }
}