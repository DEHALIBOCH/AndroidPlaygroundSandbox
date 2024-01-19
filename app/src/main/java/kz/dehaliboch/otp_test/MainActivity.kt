package kz.dehaliboch.otp_test

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kz.dehaliboch.otp_test.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val otpHelperV2 by lazy {
        SmsCodeHelperImpl(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val appSignatureHashHelper = AppSignatureHashHelper(this)
        val appSignatureHashHelper2 = AppSignatureHelper(this)
        val hash = appSignatureHashHelper.appSignatures.joinToString("\n")
        val hash2 = "Java " + appSignatureHashHelper2.appSignatures.joinToString("\n")
        binding.hash.text = hash
        binding.hash2.text = hash2
        Log.d("App Signatures", hash)
        Log.d("App Signatures", "java" + hash2)
    }

    override fun onResume() {
        super.onResume()
        otpHelperV2.registerReceiver { otp ->
            binding.otp.text = otp
            Toast.makeText(this, "Otp received: $otp", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        otpHelperV2.unregisterReceiver()
    }

}