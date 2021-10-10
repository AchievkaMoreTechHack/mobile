package com.achievka.moretech3.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
class Story {
    @SerializedName("next")
    @Expose
    var next: String? = null

    @SerializedName("prev")
    @Expose
    var prev: String? = null

    @SerializedName("story_female")
    @Expose
    var storyFemale: List<String>? = null

    @SerializedName("description_preview")
    @Expose
    var descriptionPreview: Any? = null

    @SerializedName("action_type")
    @Expose
    var actionType: String? = null

    @SerializedName("action_data_female")
    @Expose
    var actionDataFemale: List<String>? = null

    @SerializedName("character")
    @Expose
    var character: String? = null

    @SerializedName("id")
    @Expose
    var id = 0

    @SerializedName("story_male")
    @Expose
    var storyMale: List<String>? = null

    @SerializedName("description")
    @Expose
    var description: Any? = null

    @SerializedName("action_data_male")
    @Expose
    var actionDataMale: List<String>? = null

    @SerializedName("background")
    @Expose
    var background: String? = null

    @SerializedName("position")
    @Expose
    var position: String? = null
}