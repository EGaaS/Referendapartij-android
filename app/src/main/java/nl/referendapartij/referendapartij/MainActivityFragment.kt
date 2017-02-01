package nl.referendapartij.referendapartij

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

/**
 * A placeholder fragment containing a simple view.
 */
class MainActivityFragment : Fragment() {

    private val TAG = this.javaClass.canonicalName

    var webView: WebView? = null
    var schema = "https"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_main, container, false)

        webView = view.findViewById(R.id.main_web_view) as WebView
        setupWebView()

        if (arguments == null) {
            webView?.loadUrl(POOL)
            return view
        }

        val key = arguments["key"] as String?

        if (key != null) {
            val url = "$schema://egaas.$DOMAIN/?key=$key"
            Toast.makeText(context, "Loading $url", Toast.LENGTH_LONG)
                    .show()
            webView?.loadUrl(url)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PURCHASE_RESULT_OK) {
            webView?.loadUrl("$schema://egaas.$DOMAIN/payment?result=ok")
        }
    }

    fun setupWebView() {
        val settings = webView?.settings
        settings?.javaScriptEnabled = true
        settings?.allowFileAccessFromFileURLs = true
        settings?.domStorageEnabled = true
        settings?.cacheMode = WebSettings.LOAD_NO_CACHE
        settings?.loadWithOverviewMode = true
        settings?.useWideViewPort = true
        settings?.setSupportZoom(true)
        webView?.clearHistory()
        webView?.clearFormData()
        webView?.setWebViewClient(CustomWebClient())
    }

    companion object {
        val PURCHASE_RESULT_OK = 666
        val PURCHASE_RESULT_FAIL = 777
        val DOMAIN = "referendapartij.nl"
        val POOL = "http://signup.referendapartij.nl/"
    }

    inner class CustomWebClient : WebViewClient() {
        var poolName: String? = null

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            if (poolName == null) {
                poolName = url
            }
            Log.d(TAG, view.url + "->" + url)

            when {
                url.contains(DOMAIN, true) && url.contains("payment", true) -> {
                    val intent = Intent(activity, BillingActivity().javaClass)
                    startActivity(intent)
                }
                !url.contains(DOMAIN, true) -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivityForResult(intent, PURCHASE_RESULT_OK)
                }
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

        }
    }
}
