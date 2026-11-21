package dz.ooredoo.connectionkeeper

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class MainActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var log: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()
    private val history = StringBuilder()
    private val urls = listOf(
        "Google" to "https://www.google.com/generate_204",
        "Facebook" to "https://www.facebook.com/",
        "Ooredoo portal" to "https://choof.ooredoo.dz/"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        status = TextView(this).apply { textSize = 18f; text = "جاري فحص الاتصال..." }
        log = TextView(this).apply { textSize = 14f }
        val refresh = Button(this).apply {
            text = "فحص الآن"
            setOnClickListener { checkConnections() }
        }
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 28, 28, 28)
            addView(status)
            addView(refresh)
            addView(log)
        }
        setContentView(ScrollView(this).apply { addView(layout) })
        checkConnections()
    }

    private fun checkConnections() {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        val caps = network?.let { cm.getNetworkCapabilities(it) }
        val connected = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        status.text = if (connected) "الشبكة متصلة — يجري اختبار الوصول للمواقع" else "لا يوجد اتصال إنترنت فعّال"
        executor.execute {
            val results = urls.map { (name, address) ->
                val result = try {
                    val c = (URL(address).openConnection() as HttpURLConnection).apply {
                        connectTimeout = 5000
                        readTimeout = 5000
                        requestMethod = "GET"
                        instanceFollowRedirects = true
                    }
                    val code = c.responseCode
                    c.disconnect()
                    "$name: HTTP $code"
                } catch (e: Exception) {
                    "$name: ${e.javaClass.simpleName}"
                }
            }
            handler.post {
                history.insert(0, results.joinToString("\n") + "\n----------------\n")
                log.text = history.toString()
            }
        }
    }

    override fun onDestroy() {
        executor.shutdownNow()
        super.onDestroy()
    }
}
