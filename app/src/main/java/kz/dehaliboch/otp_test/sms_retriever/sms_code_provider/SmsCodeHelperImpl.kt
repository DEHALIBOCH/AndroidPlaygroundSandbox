package kz.dehaliboch.otp_test.sms_retriever.sms_code_provider

import android.content.Context
import android.content.IntentFilter
import com.google.android.gms.auth.api.phone.SmsRetriever
import kz.dehaliboch.otp_test.sms_retriever.broadcast_receirver.SmsMessagingReceiver

class SmsCodeHelperImpl(private val context: Context) : SmsCodeProvider {

    private var smsMessagingReceiver: SmsMessagingReceiver = SmsMessagingReceiver()
    private val otpRegex = "\\b\\d{4}\\b"

    override fun registerReceiver(onOtpReady: (String) -> Unit) {
        SmsRetriever.getClient(context).startSmsRetriever().addOnSuccessListener {
            try {
                smsMessagingReceiver.setOnOtpReady {
                    onOtpReady(getCleanCodeFromMessage(it))
                }
                val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
                context.registerReceiver(smsMessagingReceiver, intentFilter)
            } catch (_: Exception) {
                Unit
            }
        }
    }

    override fun unregisterReceiver() {
        try {
            context.unregisterReceiver(smsMessagingReceiver)
        } catch (_: Exception) {
            Unit
        }
    }

    private fun getCleanCodeFromMessage(message: String): String {
        val otpRegex = Regex(otpRegex)
        return otpRegex.find(message)?.value.orEmpty()
    }
}