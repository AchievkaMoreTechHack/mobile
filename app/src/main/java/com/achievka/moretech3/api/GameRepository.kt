package com.achievka.moretech3.api

class GameRepository constructor(private val retrofitService: GetStoriesService) {

    fun getAllStories() = retrofitService.getAllStories()
}