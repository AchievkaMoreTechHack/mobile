package com.achievka.moretech3.api

import com.achievka.moretech3.api.APIConfig.BASE_URL
import com.achievka.moretech3.api.APIConfig.STORY_PATH
import com.achievka.moretech3.model.ResponseData
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface GetStoriesService {
    @GET(STORY_PATH)
    fun getAllStories(): Call<ResponseData>

    companion object {

        var getStoryService: GetStoriesService? = null

        fun getInstance() : GetStoriesService {

            if (getStoryService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                getStoryService = retrofit.create(GetStoriesService::class.java)
            }
            return getStoryService!!
        }
    }
}