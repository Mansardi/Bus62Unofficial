package transport.client.bus62unofficial

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import java.util.*


abstract class ListLoader(private val activity: Activity, recyclerView: RecyclerView, footerView: View? = null) {
    private var itemsPerPage: Int = 0
    private var items: ArrayList<HashMap<String, String>> = ArrayList()
    private var loadingMore: Boolean = false

    var adapter: LazyAdapter? = null

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
        //items = ArrayList()
        // Загружаем данные.
        onLoadItems(items, itemsPerPage)
        // Обновляем список и адаптер.
        activity.runOnUiThread(updateAdapterRunnable)
    }

    private val updateAdapterRunnable = Runnable {
        if (items.size > 0) {
           // adapter!!.addAll(items)
        }
        adapter!!.notifyDataSetChanged()
        loadingMore = false
    }

    init {
        var fView = footerView
        itemsPerPage = 10

        if (fView == null) {
            // Создаём footer по умочанию.
            fView = TextView(activity)
            fView.text = "Loading..."
        }

        loadingMore = false

        //recyclerView.addFooterView(footerView)
        recyclerView.setOnScrollListener(scrollListener)
    }

    /**
     * Создаёт новый адаптер из ресурса разметки.
     * @param layoutResource ресурс разметки
     */
    fun createAdapter() : LazyAdapter? {
        adapter = LazyAdapter(activity, items)
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