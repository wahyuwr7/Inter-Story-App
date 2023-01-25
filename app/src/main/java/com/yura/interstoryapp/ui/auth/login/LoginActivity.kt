package com.yura.interstoryapp.ui.auth.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.window.OnBackInvokedDispatcher
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.yura.interstoryapp.R
import com.yura.interstoryapp.data.utils.Utils.dataStore
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.databinding.ActivityLoginBinding
import com.yura.interstoryapp.ui.auth.register.RegisterActivity
import com.yura.interstoryapp.ui.stories.StoriesActivity
import com.yura.interstoryapp.ui.viewmodel.VMFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingState(false)

        backPressed()
        val pref = UserPrefs.getInstance(dataStore)
        val viewModel = ViewModelProvider(this, VMFactory(pref, this))[LoginViewModel::class.java]

        binding.apply {
            etEmail.doOnTextChanged { text, _, _, _ ->
                if (Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches()) {
                    layoutEmail.error = null
                } else {
                    layoutEmail.error = resources.getString(R.string.email_error)
                }
            }

            tvRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                finish()
            }

            btnLogin.setOnClickListener {
                if (etEmail.text.toString()
                        .isNotEmpty() && etPassword.text.toString().length >= 6
                ) {
                    loadingState(true)
                    val email = etEmail.text.toString()
                    val password = etPassword.text.toString()
                    viewModel.login(email, password, this@LoginActivity)
                        .observe(this@LoginActivity) { isLogin ->
                            if (isLogin) {
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        StoriesActivity::class.java
                                    )
                                )
                                finish()
                            } else {
                                loadingState(false)
                            }
                        }
                }
            }
        }
    }

    private fun backPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                finish()
            }
        } else {
            onBackPressedDispatcher.addCallback(this@LoginActivity) {
                finish()
            }
        }
    }

    private fun loadingState(state: Boolean) {
        if (state) {
            binding.apply {
                progressCircular.visibility = View.VISIBLE
                btnLogin.isEnabled = false
            }
        } else {
            binding.apply {
                progressCircular.visibility = View.GONE
                btnLogin.isEnabled = true
            }
        }
    }
}