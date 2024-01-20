package kz.dehaliboch.otp_test.concurrency

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kz.dehaliboch.otp_test.R
import kz.dehaliboch.otp_test.databinding.ActivityPipeExampleBinding
import java.io.IOException
import java.io.PipedReader
import java.io.PipedWriter

private const val TAG = "PipeExampleActivity"


class PipeExampleActivity : AppCompatActivity() {

    private var _binding: ActivityPipeExampleBinding? = null
    private val binding get() = _binding!!
    private var reader: PipedReader? = null
    private var writer: PipedWriter? = null
    private var workerThread: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPipeExampleBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        writer = PipedWriter()
        reader = PipedReader()

        try {
            writer?.connect(reader)
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
        }

        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    if (count > before) { // обрабатываем только добавленные символы
                        // запись последнего введенного символа в канал
                        writer?.write(s?.subSequence(before, count).toString())
                    }
                } catch (e: IOException) {
                    Log.e(TAG, e.stackTraceToString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // do nothing
            }
        })

        workerThread = Thread(TextHandlerTask(reader))
        workerThread?.start()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        workerThread?.interrupt()
        try {
            reader?.close()
            writer?.close()
        } catch (e: IOException) {
            Log.e(TAG, e.stackTraceToString())
        }
        reader = null
        writer = null
        workerThread = null
    }
}

private class TextHandlerTask(val reader: PipedReader?) : Runnable {

    override fun run() {
        if (reader == null) {
            Log.e(TAG, "Reader is null")
            return
        }
        while (!Thread.currentThread().isInterrupted) {
            try {
                var i: Int
                while (reader.read().also { i = it } != -1) {
                    val ch = i.toChar()
                    // Обработка введенного текста
                    Log.d(TAG, "char = $ch")
                }
            } catch (e: IOException) {
                Log.e(TAG, e.stackTraceToString())
            }
        }
    }
}