package com.yura.interstoryapp.ui.auth.register

import android.content.Intent
import android.os.Bundle
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

        val pref = UserPrefs.getInstance(dataStore)
        val viewModel = ViewModelProvider(this, VMFactory(pref))[RegisterViewModel::class.java]

        binding.apply {
            tvLogin.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            }

            btnRegister.setOnClickListener {
                if (!etEmail.text.isNullOrEmpty() && !etName.text.isNullOrEmpty() && etPassword.text.toString().length >= 6) {
                    viewModel.register(
                        etName.text.toString(),
                        etEmail.text.toString(),
                        etPassword.text.toString(),
                        this@RegisterActivity
                    ).observe(this@RegisterActivity){
                        if (it){
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        }
                    }
                }
            }
        }
    }
}