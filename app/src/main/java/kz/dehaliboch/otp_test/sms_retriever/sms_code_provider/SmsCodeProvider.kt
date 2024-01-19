package kz.dehaliboch.otp_test.sms_retriever.sms_code_provider

interface SmsCodeProvider {

    fun registerReceiver(onOtpReady: (String) -> Unit)

    fun unregisterReceiver()
}