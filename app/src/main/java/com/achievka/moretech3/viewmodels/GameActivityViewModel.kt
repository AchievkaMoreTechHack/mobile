package com.achievka.moretech3.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.LiveData
import com.achievka.moretech3.models.SceneData


class GameActivityViewModel: ViewModel() {
    private var scenes: MutableLiveData<List<SceneData>>? = null

    fun getWallets(): LiveData<List<SceneData>>? {
        if (scenes == null) {
            scenes = MutableLiveData<List<SceneData>>()
        }
        return scenes
    }

    fun updateScences(scenes: List<SceneData>) {
        this.scenes!!.value = scenes
    }
}