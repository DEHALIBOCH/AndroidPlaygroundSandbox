package kz.dehaliboch.otp_test

interface SmsCodeProvider {

    fun registerReceiver(onOtpReady: (String) -> Unit)

    fun unregisterReceiver()
}