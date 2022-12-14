package com.yura.interstoryapp.data.remote.data
//
//import androidx.paging.PagingSource
//import androidx.paging.PagingState
//import com.yura.interstoryapp.data.remote.IApi
//import com.yura.interstoryapp.data.remote.response.StoriesResponse
//import retrofit2.awaitResponse
//
//class StoriesPagingSource(private val apiService: IApi, private val auth: String) : PagingSource<Int, StoriesResponse>() {
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoriesResponse> {
//        return try {
//            val position = params.key ?: INITIAL_PAGE_INDEX
//            val responseData = apiService.getStories(auth, position, params.loadSize)
//            LoadResult.Page(
//                data = responseData,
//                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
//                nextKey = if (responseData.isNullOrEmpty()) null else position + 1
//            )
//        } catch (exception: Exception) {
//            return LoadResult.Error(exception)
//        }
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, StoriesResponse>): Int? {
//        return state.anchorPosition?.let { anchorPosition ->
//            val anchorPage = state.closestPageToPosition(anchorPosition)
//            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
//        }
//    }
//
//    private companion object {
//        const val INITIAL_PAGE_INDEX = 1
//    }
//}