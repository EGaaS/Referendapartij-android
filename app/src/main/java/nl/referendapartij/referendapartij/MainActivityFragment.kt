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

/**
 * A placeholder fragment containing a simple view.
 */
class MainActivityFragment : Fragment() {

    private val TAG = this.javaClass.canonicalName

    var webView: WebView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_main, container, false)

        webView = view.findViewById(R.id.main_web_view) as WebView

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
        webView?.loadUrl(POOL)
        webView?.setWebViewClient(CustomWebClient())

        return view
    }

    companion object {
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
            if (!url.contains(DOMAIN, true)) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

        }
    }
}
