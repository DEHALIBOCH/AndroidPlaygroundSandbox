package kz.dehaliboch.otp_test.concurrency

import android.os.Handler
import android.os.Looper
import android.os.MessageQueue

class ConsumeAndQuitThread : Thread(THREAD_NAME), MessageQueue.IdleHandler {

    companion object {
        private const val THREAD_NAME = "ConsumeAndQuitThread"
    }

    var mConsumeHandler: Handler? = null
    var mIsFirstIdle = true

    override fun run() {
        Looper.prepare()
        mConsumeHandler = Handler {
            true
        }
        Looper.myQueue().addIdleHandler(this) /* Регистрация обработчика IdleHandler при запуске фонового потока, когда
            объект Looper и соответсвующая ему очередь MessageQueue готовы к работе.
        */
        Looper.loop()
    }

    override fun queueIdle(): Boolean {
        if (mIsFirstIdle) { // Обработка первого вызова queueIdle, который происходит до получения нового сообщения
            mIsFirstIdle = false
            return true // Так как обработчик IdleHandler все еще остается зарегистрированным, первый вызов возвращает true
        }
        mConsumeHandler?.looper?.quit() // Завершение потока
        return false
    }

    fun enqueueData(i: Int) {
        mConsumeHandler?.sendEmptyMessage(i)
    }
}