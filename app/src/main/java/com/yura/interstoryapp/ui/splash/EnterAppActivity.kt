package com.yura.interstoryapp.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.yura.interstoryapp.data.Utils.dataStore
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.databinding.ActivityEnterAppBinding
import com.yura.interstoryapp.ui.stories.StoriesActivity
import com.yura.interstoryapp.ui.auth.login.LoginActivity
import com.yura.interstoryapp.ui.viewmodel.VMFactory

class EnterAppActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEnterAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPrefs.getInstance(dataStore)
        val viewModel = ViewModelProvider(this, VMFactory(pref))[EnterAppViewModel::class.java]

        viewModel.getUserLoginState().observe(this) {
            if (it)
                goToStory()
            else
                goToLogin()
        }
    }

    private fun goToLogin() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
        }, 2000L)
    }

    private fun goToStory() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, StoriesActivity::class.java))
        }, 2000L)
    }
}