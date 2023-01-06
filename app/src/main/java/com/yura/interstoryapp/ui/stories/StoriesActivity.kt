package com.yura.interstoryapp.ui.stories

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.*
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yura.interstoryapp.R
import com.yura.interstoryapp.data.Utils
import com.yura.interstoryapp.data.Utils.dataStore
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.data.remote.response.ListStoryItem
import com.yura.interstoryapp.databinding.ActivityStoriesBinding
import com.yura.interstoryapp.ui.splash.EnterAppActivity
import com.yura.interstoryapp.ui.splash.EnterAppActivity.Companion.fromBack
import com.yura.interstoryapp.ui.stories.add.AddStoryActivity
import com.yura.interstoryapp.ui.stories.detail.DetailActivity
import com.yura.interstoryapp.ui.stories.detail.DetailActivity.Companion.DATA
import com.yura.interstoryapp.ui.stories.logout.PopupLogoutFragment
import com.yura.interstoryapp.ui.viewmodel.VMFactory
import kotlin.system.exitProcess

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
            binding.txtFirstUserChar.text = it[0].toString()
        }
    }

    private fun backPressed() {
        onBackPressedDispatcher.addCallback(this) {
            if (Utils.backPressedTime + 3000 > System.currentTimeMillis()) {
                onBackPressedDispatcher.onBackPressed()
                finishAffinity()
                finishActivity(0)
            } else {
                Utils.backPressedToast(this@StoriesActivity)
            }
            Utils.backPressedTime = System.currentTimeMillis()
        }
    }

    @Suppress("UNCHECKED_CAST")
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