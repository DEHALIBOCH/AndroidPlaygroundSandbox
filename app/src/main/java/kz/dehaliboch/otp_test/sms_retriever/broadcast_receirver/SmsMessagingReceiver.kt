package kz.dehaliboch.otp_test.sms_retriever.broadcast_receirver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SmsMessagingReceiver : BroadcastReceiver() {

    private var onOtpReady: (String) -> Unit = {}

    fun setOnOtpReady(otpListener: (String) -> Unit) {
        this.onOtpReady = otpListener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == SmsRetriever.SMS_RETRIEVED_ACTION) {
            val extras = intent.extras ?: return
            val status = extras.get(SmsRetriever.EXTRA_STATUS) as? Status ?: return
            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                    message?.let{
                        onOtpReady(it)
                    }
                }

                CommonStatusCodes.TIMEOUT -> Unit
            }
        }
    }
}