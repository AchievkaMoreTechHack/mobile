package com.achievka.moretech3.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData

import com.achievka.moretech3.api.GameRepository
import com.achievka.moretech3.model.ResponseData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GameViewModel constructor(private val repository: GameRepository)  : ViewModel() {

    companion object{
        const val TAG = "GameViewModel"
    }

    val stories = MutableLiveData<ResponseData>()
    val currentStoryId = MutableLiveData<Int>()
    val currentDialogIndex = MutableLiveData<Int>()
    val errorMessage = MutableLiveData<String>()

    val username = MutableLiveData<String>(null)
    val userAge = MutableLiveData<Int>(null)
    val isMale = MutableLiveData(true)

    fun getAllStories() {
        Log.d(TAG, "getAllScenes:")
        val response = repository.getAllStories()
        response.enqueue(object : Callback<ResponseData> {
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                Log.d(TAG, "onResponse: $response")
                if (response.code() == 200) {
                    stories.postValue(response.body())
                }else{
                    errorMessage.postValue("Ошибка ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.localizedMessage}")
                errorMessage.postValue("Ошибка ${t.message}")
            }
        })
    }
}