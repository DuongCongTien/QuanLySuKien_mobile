package com.group29.eventapp.presentation.auth

import android.app.AlertDialog
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.group29.eventapp.R
import com.group29.eventapp.common.SessionManager
import com.group29.eventapp.databinding.DialogForgotPasswordBinding
import com.group29.eventapp.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Đăng nhập thường với form validate
        binding.btnLogin.setOnClickListener {
            if (validateLoginForm()) {
                // // MOCK: Mọi mật khẩu hợp lệ định dạng đều đăng nhập thành công với vai trò Người tham dự
                SessionManager.loginAsAttendee()
                Toast.makeText(requireContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_login_to_home)
            }
        }

        // Đăng nhập demo – Người tham gia
        binding.btnDemoAttendee.setOnClickListener {
            SessionManager.loginAsAttendee()
            Snackbar.make(binding.root, "Đang vào ứng dụng với vai trò: Người tham gia", Snackbar.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_login_to_home)
        }

        // Đăng nhập demo – Ban tổ chức
        binding.btnDemoOrganizer.setOnClickListener {
            SessionManager.loginAsOrganizer()
            Snackbar.make(binding.root, "Đang vào ứng dụng với vai trò: Ban tổ chức", Snackbar.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_login_to_org_dashboard)
        }

        // Quên mật khẩu
        binding.tvForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }

        // Điều hướng sang Đăng ký
        binding.tvGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
    }

    private fun validateLoginForm(): Boolean {
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString().orEmpty()
        var isValid = true

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

        return isValid
    }

    private fun showForgotPasswordDialog() {
        val dialogBinding = DialogForgotPasswordBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancelForgot.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnSubmitForgot.setOnClickListener {
            val email = dialogBinding.etForgotEmail.text?.toString()?.trim().orEmpty()
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                dialogBinding.tilForgotEmail.error = getString(R.string.val_email_invalid)
            } else {
                // // MOCK: Giả lập gửi email khôi phục mật khẩu
                dialog.dismiss()
                Snackbar.make(binding.root, getString(R.string.forgot_password_success), Snackbar.LENGTH_LONG).show()
            }
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
