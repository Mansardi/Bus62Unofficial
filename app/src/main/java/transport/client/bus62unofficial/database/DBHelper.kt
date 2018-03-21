package transport.client.bus62unofficial.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context : Context) : SQLiteOpenHelper(context, "Bus62Base", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table stations ("
                + "id integer primary key,"
                + "name text,"
                + "descr text" + ");")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {

    }
}