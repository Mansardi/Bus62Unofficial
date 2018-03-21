package transport.client.bus62unofficial.utils

import android.net.Uri
import android.text.TextUtils
import org.jetbrains.anko.doAsyncResult
import java.io.InputStream
import java.io.OutputStream
import java.net.*

object Utils {
    fun copyStream(inStream: InputStream, outStream: OutputStream) {
        val bufferSize = 1024
        try {
            val bytes = ByteArray(bufferSize)
            while (true) {
                val count = inStream.read(bytes, 0, bufferSize)
                if (count == -1)
                    break
                outStream.write(bytes, 0, count)
            }
        } catch (ex: Exception) {

        }
    }

    fun sendGet(url: String): String {
        val msCookieManager = CookieManager()
        CookieHandler.setDefault(msCookieManager);

        val base = URL("http://bus62.ru")
        val cookieConnection = base.openConnection()
        doAsyncResult { cookieConnection.headerFields["Set-Cookie"] }.get()

        val host = "http://bus62.ru/php/"
        val conn2 = URL(host + url).openConnection()
        if (msCookieManager.cookieStore.cookies.count() > 0) {

            conn2.setRequestProperty("Cookie", TextUtils.join(";",  msCookieManager.cookieStore.cookies));
        }

        val s = doAsyncResult { conn2.inputStream.bufferedReader().readText() }
        return try {
            s.get()
        } catch (e: Exception) {
            ""
        }
    }


}