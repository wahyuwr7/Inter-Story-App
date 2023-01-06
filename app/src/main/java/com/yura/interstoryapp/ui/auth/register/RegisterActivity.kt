package com.yura.interstoryapp.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.yura.interstoryapp.data.Utils.dataStore
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.databinding.ActivityRegisterBinding
import com.yura.interstoryapp.ui.auth.login.LoginActivity
import com.yura.interstoryapp.ui.viewmodel.VMFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingState(false)

        val pref = UserPrefs.getInstance(dataStore)
        val viewModel = ViewModelProvider(this, VMFactory(pref))[RegisterViewModel::class.java]

        binding.apply {
            tvLogin.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            }

            btnRegister.setOnClickListener {
                loadingState(true)
                if (!etEmail.text.isNullOrEmpty() && !etName.text.isNullOrEmpty() && etPassword.text.toString().length >= 6) {
                    loadingState(true)
                    viewModel.register(
                        etName.text.toString(),
                        etEmail.text.toString(),
                        etPassword.text.toString(),
                        this@RegisterActivity
                    ).observe(this@RegisterActivity) {
                        if (it) {
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        } else {
                            loadingState(false)
                        }
                    }
                }
            }
        }
    }

    private fun loadingState(state: Boolean) {
        if (state) {
            binding.apply {
                progressCircular.visibility = View.VISIBLE
                btnRegister.isEnabled = false
            }
        } else {
            binding.apply {
                progressCircular.visibility = View.GONE
                btnRegister.isEnabled = true
            }
        }
    }
}