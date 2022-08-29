package com.yura.interstoryapp.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.yura.interstoryapp.R
import com.yura.interstoryapp.data.Utils.backPressedTime
import com.yura.interstoryapp.data.Utils.dataStore
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.databinding.ActivityLoginBinding
import com.yura.interstoryapp.ui.stories.StoriesActivity
import com.yura.interstoryapp.ui.auth.register.RegisterActivity
import com.yura.interstoryapp.ui.viewmodel.VMFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingState(false)

        val pref = UserPrefs.getInstance(dataStore)
        val viewModel = ViewModelProvider(this, VMFactory(pref))[LoginViewModel::class.java]

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
                                startActivity(Intent(this@LoginActivity, StoriesActivity::class.java))
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
                btnLogin.isEnabled = false
            }
        } else {
            binding.apply {
                progressCircular.visibility = View.GONE
                btnLogin.isEnabled = true
            }
        }
    }

    override fun onBackPressed() {
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finishAffinity()
        } else {
            Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_LONG).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}