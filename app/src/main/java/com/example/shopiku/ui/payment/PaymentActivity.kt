package com.example.shopiku.ui.payment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shopiku.data.local.SessionManager
import com.example.shopiku.data.repository.OrderRepository
import com.example.shopiku.databinding.ActivityPaymentBinding
import com.example.shopiku.ui.tracking.TrackingActivity
import com.example.shopiku.util.CurrencyUtils
import com.example.shopiku.util.Resource
import kotlinx.coroutines.launch

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private lateinit var orderRepository: OrderRepository

    private var orderId: String = ""
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderRepository = OrderRepository(SessionManager(this))
        orderId = intent.getStringExtra("ORDER_ID") ?: ""

        setupUI()
        loadOrder()
        startTimer()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnCopyVa.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("VA BCA", binding.tvVaNumber.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Nomor VA berhasil disalin!", Toast.LENGTH_SHORT).show()
        }

        binding.btnCheckStatus.setOnClickListener {
            lifecycleScope.launch {
                val res = orderRepository.simulatePayment(orderId)
                if (res is Resource.Success) {
                    binding.tvPaymentStatus.text = "Status: LUNAS"
                    binding.tvPaymentStatus.setTextColor(getColor(com.example.shopiku.R.color.status_paid))
                    Toast.makeText(this@PaymentActivity, "Pembayaran Berhasil! (Lunas)", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@PaymentActivity, TrackingActivity::class.java).apply {
                        putExtra("ORDER_ID", orderId)
                    }
                    startActivity(intent)
                    finish()
                } else if (res is Resource.Error) {
                    Toast.makeText(this@PaymentActivity, res.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadOrder() {
        lifecycleScope.launch {
            val res = orderRepository.getOrderById(orderId)
            if (res is Resource.Success) {
                val order = res.data
                binding.tvTotalAmount.text = CurrencyUtils.formatRupiah(order.total)
                binding.tvVaNumber.text = order.vaNumber
                if (order.status == "PAID") {
                    binding.tvPaymentStatus.text = "Status: LUNAS"
                    binding.tvPaymentStatus.setTextColor(getColor(com.example.shopiku.R.color.status_paid))
                }
            }
        }
    }

    private fun startTimer() {
        // 24 hours countdown
        val durationMillis = 24 * 60 * 60 * 1000L
        countDownTimer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = millisUntilFinished / (1000 * 60 * 60)
                val minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60)
                val seconds = (millisUntilFinished % (1000 * 60)) / 1000
                binding.tvCountdown.text = String.format("%02d : %02d : %02d", hours, minutes, seconds)
            }

            override fun onFinish() {
                binding.tvCountdown.text = "00 : 00 : 00"
                binding.tvPaymentStatus.text = "Status: KEDALUWARSA"
                binding.tvPaymentStatus.setTextColor(getColor(com.example.shopiku.R.color.price_tag))
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}