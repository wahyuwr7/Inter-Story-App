package com.yura.interstoryapp.ui.stories

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yura.interstoryapp.R
import com.yura.interstoryapp.data.remote.response.ListStoryItem
import com.yura.interstoryapp.databinding.ItemStoriesBinding

class StoriesAdapter(private val listStories: ArrayList<ListStoryItem?>?) :
    RecyclerView.Adapter<StoriesAdapter.StoryViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding =
            ItemStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        listStories?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = listStories?.size ?: 0

    inner class StoryViewHolder(private val binding: ItemStoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(stories: ListStoryItem) {
            binding.apply {
                Glide.with(itemView)
                    .load(stories.photoUrl)
                    .into(imgStory)
                tvName.text = stories.name
                tvUsername.text = stories.name
                tvFirstUserChar.text = stories.name?.get(0).toString().uppercase()
                tvCreatedAt.text = stories.createdAt?.substring(0, 10)

                val descLength = stories.description?.length
                if (descLength != null && descLength > 25) {
                    tvDescription.text = "${stories.description.substring(0,24)}..."
                } else {
                    tvDescription.text = stories.description
                }

                val color = getBgColor(adapterPosition)
                imgAccount.setImageResource(color)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(imgStory, "Stories"),
                        Pair(tvName, "Name"),
                        Pair(tvFirstUserChar, "First Char"),
                        Pair(tvCreatedAt, "2022–08–22"),
                        Pair(tvDescription, "Description")
                    )

                root.setOnClickListener {
                    onItemClickCallback.onItemClicked(stories, optionsCompat)
                }
            }
        }

        private fun getBgColor(position: Int): Int {
            return if (position % 5 == 0) {
                R.color.cyan
            } else if (position % 5 == 1) {
                R.color.green
            } else if (position % 5 == 2) {
                R.color.pink
            } else if (position % 5 == 3) {
                R.color.violet
            } else {
                R.color.yellow
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem, optionsCompat: ActivityOptionsCompat)
    }
}
