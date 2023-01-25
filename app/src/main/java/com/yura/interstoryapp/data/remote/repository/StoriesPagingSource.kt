package com.yura.interstoryapp.data.remote.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.yura.interstoryapp.data.utils.Utils.userAuth
import com.yura.interstoryapp.data.remote.IApi
import com.yura.interstoryapp.data.remote.response.ListStoryItem
import retrofit2.awaitResponse

class StoriesPagingSource(private val apiService: IApi) :
    PagingSource<Int, ListStoryItem>() {

    @Suppress("UNCHECKED_CAST")
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val auth = "Bearer $userAuth"
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData =
                apiService.getStories(auth, position, params.loadSize).awaitResponse()
                    .body()?.listStory as List<ListStoryItem>
            LoadResult.Page(
                data = responseData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}