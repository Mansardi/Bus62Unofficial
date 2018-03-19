package transport.client.bus62unofficial.utils

import org.jetbrains.anko.doAsyncResult
import java.io.InputStream
import java.io.OutputStream
import java.net.URL

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
        val host = "http://bus62.ru/php/"
        val obj = URL(host + url)
        val s = doAsyncResult { obj.readText() }

        return try {
            s.get()
        } catch (e: Exception) {
            ""
        }
    }
}