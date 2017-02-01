package nl.referendapartij.referendapartij


import android.app.Activity
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.vending.billing.IInAppBillingService
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.util.Log
import android.widget.Button


/**
 * A simple [Fragment] subclass.
 */
class BillingActivityFragment : Fragment() {

    private val TAG = this.javaClass.canonicalName

    private var billingService: IInAppBillingService? = null
    private val serviceConnection = BillingServiceConnection()
    private var buyButton: Button? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_billing, container, false)

        activity.setContentView(R.layout.activity_billling)
        val serviceIntent = Intent("com.android.vending.billing.InAppBillingService.BIND")
        serviceIntent.`package` = "com.android.vending"
        activity.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        buyButton = view.findViewById(R.id.buy_button) as Button
        buyButton?.setOnClickListener {
            val base64PK = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAn1J/eJKVycn8ZGpvmWXnIYXujiPQlvN/EMCi4SqzQUF+F1JH2Rouc2Z6QPTx5sVrQRUOfhFK7xNrHLa8Xp9Sx0nxI5Ln7MphH73bqYnveLPnCWE5qJhePvyTebf8nicupRkjtUbnWdJX3GW63smQFN80DyvXEK+7r1bWa9tHroHmy8N+cInaAkl5g7DRbWXD8ApQ0txuKk/xy5TR4wB1M+W0upms0LFiFMjBWP2zpOQIxC5uuhWTK6hJuDa5sWiSfAuQ4hEin5Q/o+hInAS6sk93fMfYDwyJVbcj9hQXlk7/UTwBRxxuJVAUvH2zgCGLmPJvrIBFbmRw7aYK9RpPOwIDAQAB"
            val buyIntentBundle = billingService?.getBuyIntent(3, activity.packageName, "nl.referendapartij.referendapartij.registration_bill", "inapp", base64PK)
            val pendingIntent = buyIntentBundle?.getParcelable<PendingIntent>("BUY_INTENT")
            startIntentSenderForResult(pendingIntent?.intentSender, 1001, Intent(), 0, 0, 0, null)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Successfully purchased")
        } else {
            Log.d(TAG, "Couldn't purchased the item. Return code is $resultCode")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (billingService != null) {
            activity.unbindService(serviceConnection)
        }
    }

    inner class BillingServiceConnection: ServiceConnection {

        override fun onServiceDisconnected(p0: ComponentName?) {
            this@BillingActivityFragment.billingService = null
        }

        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            this@BillingActivityFragment.billingService = IInAppBillingService.Stub.asInterface(p1)
        }

    }
}

