package com.group29.eventapp.presentation.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.group29.eventapp.R
import com.group29.eventapp.common.SessionManager
import com.group29.eventapp.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnRegister.setOnClickListener {
            if (validateRegisterForm()) {
                // // MOCK: Giả lập tạo tài khoản thành công và tự động đăng nhập demo
                SessionManager.loginAsAttendee()
                Toast.makeText(requireContext(), "Tạo tài khoản thành công! Chào mừng bạn gia nhập.", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_login_to_home)
            }
        }

        binding.tvGoToLogin.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun validateRegisterForm(): Boolean {
        val fullName = binding.etFullName.text?.toString()?.trim().orEmpty()
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString().orEmpty()
        val confirmPassword = binding.etConfirmPassword.text?.toString().orEmpty()
        var isValid = true

        if (fullName.isEmpty()) {
            binding.tilFullName.error = getString(R.string.val_field_required)
            isValid = false
        } else {
            binding.tilFullName.error = null
        }

        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.val_field_required)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.val_email_invalid)
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.val_field_required)
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = getString(R.string.val_password_short)
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.error = getString(R.string.val_field_required)
            isValid = false
        } else if (confirmPassword != password) {
            binding.tilConfirmPassword.error = getString(R.string.val_password_not_match)
            isValid = false
        } else {
            binding.tilConfirmPassword.error = null
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
