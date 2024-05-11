import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object Server {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val url = URL("http://192.168.35.101:8864/api/data")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        val reader = BufferedReader(InputStreamReader(conn.inputStream))
        var line: String?
        val response = StringBuilder()
        while (reader.readLine().also { line = it } != null) {
            response.append(line)
        }
        reader.close()
        println("Response from Node.js server: $response")
        conn.disconnect()
    }
}
