package com.achievka.moretech3.model

import com.achievka.moretech3.model.Story
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
class ResponseData {
    @SerializedName("version")
    @Expose
    var version: String? = null

    @SerializedName("startId")
    @Expose
    var startId: Int? = null

    @SerializedName("stories")
    @Expose
    var stories: List<Story>? = null
}
