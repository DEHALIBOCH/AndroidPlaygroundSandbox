package kz.dehaliboch.otp_test.concurrency

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kz.dehaliboch.otp_test.R
import kz.dehaliboch.otp_test.databinding.ActivityLooperBinding

class LooperActivity : AppCompatActivity() {

    private var _binding: ActivityLooperBinding? = null
    private val binding get() = _binding!!

    private var mLooperThread: LooperThread? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityLooperBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mLooperThread = LooperThread()
        mLooperThread?.start() // Запускается рабочий поток, теперь он готов к обработке сообщений
    }

    private fun onClick(view: View) {
        /*
            Здесь возможно состояние гонки, когда обработчик mHandler еще не создан в фоновом потоке, но UI поток уже пытается его
            использовать. Поэтому необходимо проверить доступность mHandler.
         */
        if (mLooperThread?.mHandler != null) {
            val msg = mLooperThread?.mHandler?.obtainMessage(0) ?: return // Инициализация объекта Message
            mLooperThread?.mHandler?.sendMessage(msg) // Вставка сообщения в очередь
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        mLooperThread?.mHandler?.looper?.quit() /* Завершение фонового потока. Вызов метода Looper.quit() останавливает передачу сообщений
            и освобождает Looper.loop() от блокировки, поэтому метод run() получает возможность завершить свою работу, что в свою очередь
            приводит к завершению работы соответствующего потока.
        */
    }

    private inner class LooperThread : Thread() { // Рабочий поток работающий как потребитель сообщений
        var mHandler: Handler? = null

        override fun run() {
            Looper.prepare() // Связывание объекта типа Looper - и в неявном виде очереди сообщений MessageQueue - с основным потоком
            /*
                Создание обработчика Handler, используемого производителем для вставки сообщений в очередь. Здесь используется конструктор
                по умолчанию, поэтому он будет связан с объектом Looper из текущего потока. Следовательно, этот обработчик Handler может
                быть создан только после вызова Looper.prepare(), в противном случае связь установится не с чем
             */
            mHandler = Handler {
                /*
                    Функция, которая вызывается диспетчером при передаче сообщения в рабочий поток. В ней проверяется параметр
                    what(  ) и затем выполняется продолжительная операция
                 */
                if (it.what == 0) {
                    doLongRunningOperation()
                }
                true
            }
            Looper.loop() // Запускается передача сообщений из очереди в поток-потребитель. Это блокирующий вызов - поэтому поток не будет завершен.
        }
    }

    private fun doLongRunningOperation() {
        // Операция требующая много времени
    }
}

