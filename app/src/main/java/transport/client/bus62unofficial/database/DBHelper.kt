package transport.client.bus62unofficial.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by Denis on 19.03.2018.
 */
class DBHelper(context : Context) : SQLiteOpenHelper(context, "Bus62Base", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table stations ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "descr text" + ");")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}