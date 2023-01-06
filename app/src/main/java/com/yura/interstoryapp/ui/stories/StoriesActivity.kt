package com.yura.interstoryapp.ui.stories

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.window.OnBackInvokedDispatcher
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yura.interstoryapp.data.Utils.dataStore
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.data.remote.response.ListStoryItem
import com.yura.interstoryapp.databinding.ActivityStoriesBinding
import com.yura.interstoryapp.ui.stories.add.AddStoryActivity
import com.yura.interstoryapp.ui.stories.detail.DetailActivity
import com.yura.interstoryapp.ui.stories.detail.DetailActivity.Companion.DATA
import com.yura.interstoryapp.ui.stories.logout.PopupLogoutFragment
import com.yura.interstoryapp.ui.viewmodel.VMFactory

class StoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoriesBinding
    private lateinit var viewModel: StoriesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPrefs.getInstance(dataStore)
        viewModel = ViewModelProvider(this, VMFactory(pref))[StoriesViewModel::class.java]

        showStories()
        backPressed()
        getProfile()

        binding.apply {
            layoutRefresh.setOnRefreshListener {
                showStories()
                Handler(Looper.getMainLooper()).postDelayed({
                    layoutRefresh.isRefreshing = false
                }, 5000L)
            }
            btnAddStory.setOnClickListener {
                startActivity(Intent(this@StoriesActivity, AddStoryActivity::class.java))
            }
            btnBarLogout.setOnClickListener {
                val childFragmentManager = supportFragmentManager
                PopupLogoutFragment().show(
                    childFragmentManager, PopupLogoutFragment.TAG
                )
            }
        }
    }

    private fun goToDetail(adapter: StoriesAdapter) {
        adapter.setOnItemClickCallback(object : StoriesAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem, optionsCompat: ActivityOptionsCompat) {
                showStories()
                startActivity(
                    Intent(this@StoriesActivity, DetailActivity::class.java)
                        .putExtra(DATA, data), optionsCompat.toBundle()
                )
            }

        })
    }

    private fun getProfile() {
        viewModel.getUsername().observe(this) {
            if (!it.isNullOrEmpty()) {
                binding.txtFirstUserChar.text = it[0].toString()
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
            onBackPressedDispatcher.addCallback(this@StoriesActivity) {
                finish()
            }
        }
    }

    private fun showStories() {
        viewModel.getUserToken().observe(this) { token ->
            viewModel.getStories(token, this).observe(this) {
                val adapter = StoriesAdapter(it)

                goToDetail(adapter)

                binding.apply {
                    rvStories.layoutManager = LinearLayoutManager(this@StoriesActivity)
                    rvStories.setHasFixedSize(true)
                    rvStories.adapter = adapter
                    layoutRefresh.isRefreshing = false
                }
            }
        }
    }

}