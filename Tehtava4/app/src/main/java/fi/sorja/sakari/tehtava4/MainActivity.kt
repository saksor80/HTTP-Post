package fi.sorja.sakari.tehtava4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    lateinit var Button: Button
    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var strResult:String

        lifecycleScope.launch {

            strResult = sendPost("http://mockbin.org/bin/2db893c0-4847-443c-b89d-403e29fbbf5c")

            runOnUiThread {

                Button = findViewById(R.id.button)
                textView = findViewById(R.id.textView)

                Button.setOnClickListener({
                    textView.text = strResult
                })
            }
        }
    }

    suspend fun sendPost(myUrl: String): String {
        val json: JSONObject = JSONObject()
        json.put("luoja","Sakari")
        json.put("viesti", "Hello")
        val result = requestPOST(myUrl,json)
        //Log.d("return",result)
        return result
    }

    suspend fun requestPOST(url: String, para:JSONObject): String{
        val result = withContext(Dispatchers.IO){
            val urli = URL(url)
            val conn: HttpURLConnection = urli.openConnection() as HttpURLConnection
            conn.readTimeout = 3000
            conn.connectTimeout = 3000
            conn.requestMethod = "POST"
            conn.doInput = true
            conn.doOutput = true
            val os: OutputStream = conn.outputStream
            val writer = BufferedWriter(OutputStreamWriter(os,"UTF-8"))
            writer.write(para.toString())
            writer.flush()
            writer.close()
            os.close()
            val responseCode: Int = conn.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inp = BufferedReader(InputStreamReader(conn.inputStream))
                val sb = StringBuffer("")
                var line: String? =""
                while(inp.readLine().also { line = it } != null){
                    sb.append(line)
                    break
                }
                inp.close()
                conn.disconnect()
                sb.toString()
            }
            else {
                conn.disconnect()
                "Epäonnistui"
            }
        }
        return result
    }
}