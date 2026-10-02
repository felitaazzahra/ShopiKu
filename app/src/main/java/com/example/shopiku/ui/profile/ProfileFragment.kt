package com.example.shopiku.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.shopiku.data.local.SessionManager
import com.example.shopiku.data.repository.AuthRepository
import com.example.shopiku.data.repository.OrderRepository
import com.example.shopiku.databinding.FragmentProfileBinding
import com.example.shopiku.ui.auth.AuthActivity
import com.example.shopiku.ui.tracking.TrackingActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var authRepository: AuthRepository
    private lateinit var orderRepository: OrderRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        authRepository = AuthRepository(sessionManager)
        orderRepository = OrderRepository(sessionManager)

        setupUser()
        setupListeners()
    }

    private fun setupUser() {
        val user = authRepository.getCurrentUser()
        binding.tvUserName.text = user.name
        binding.tvUserEmail.text = user.email
        binding.tvUserPhone.text = user.phone
    }

    private fun setupListeners() {
        binding.btnOrderTracking.setOnClickListener {
            val orders = SessionManager(requireContext()).getOrders()
            if (orders.isNotEmpty()) {
                val latestOrder = orders.first()
                val intent = Intent(requireContext(), TrackingActivity::class.java).apply {
                    putExtra("ORDER_ID", latestOrder.id)
                }
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Belum ada pesanan aktif", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnMenuAddress.setOnClickListener {
            Toast.makeText(requireContext(), "Alamat: Kota Cirebon, Jawa Barat", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            authRepository.logout()
            Toast.makeText(requireContext(), "Berhasil keluar", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}