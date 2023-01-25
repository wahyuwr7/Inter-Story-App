package com.yura.interstoryapp.data.utils

import com.yura.interstoryapp.data.remote.response.ListStoryItem

object FakeData {
    fun generateFakeStories(): List<ListStoryItem> {
        val storyList = ArrayList<ListStoryItem>()
        for (i in 0..10) {
            val story = ListStoryItem(
                "https://story-api.dicoding.dev/images/stories/photos-1674631720571_IyEtV8BV.jpg",
                "2023-01-25T07:28:40.572Z",
                "Yuraa",
                "tes lokasi",
                106.7402344,
                "story-gcGoDNEEO5QlIWld",
                -6.1113491
            )
            storyList.add(story)
        }
        return storyList
    }
}