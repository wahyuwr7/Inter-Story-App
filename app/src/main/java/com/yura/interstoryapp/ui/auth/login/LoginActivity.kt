package com.yura.interstoryapp.ui.auth.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.yura.interstoryapp.data.Utils.dataStore
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.databinding.ActivityLoginBinding
import com.yura.interstoryapp.ui.auth.register.RegisterActivity
import com.yura.interstoryapp.ui.story.StoryActivity
import com.yura.interstoryapp.ui.viewmodel.VMFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPrefs.getInstance(dataStore)
        val viewModel = ViewModelProvider(this, VMFactory(pref))[LoginViewModel::class.java]

        binding.apply {
            tvRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }

            btnLogin.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                viewModel.login(email, password, this@LoginActivity)
                    .observe(this@LoginActivity) { isLogin ->
                        if (isLogin) {
                            startActivity(Intent(this@LoginActivity, StoryActivity::class.java))
                        }
                    }
            }
        }
    }
}