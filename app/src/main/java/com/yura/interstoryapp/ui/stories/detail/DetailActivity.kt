package com.yura.interstoryapp.ui.stories.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.yura.interstoryapp.data.remote.response.ListStoryItem
import com.yura.interstoryapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.getParcelableExtra<ListStoryItem>(DATA)

        binding.apply {
            if (extras != null) {
                tvFirstUserChar.text = extras.name?.get(0)?.uppercase()
                tvName.text = extras.name
                tvCreatedAt.text = extras.createdAt?.substring(0, 10)
                tvDescription.text = extras.description

                Glide.with(this@DetailActivity)
                    .load(extras.photoUrl)
                    .into(imgDetail)
            }

            btnBack.setOnClickListener {
                finish()
            }
        }
    }

    companion object {
        const val DATA = "data"
    }
}