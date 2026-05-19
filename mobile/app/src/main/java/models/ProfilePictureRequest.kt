package com.staffguard.mobile.models

import com.google.gson.annotations.SerializedName

data class ProfilePictureRequest(
    @SerializedName("profilePicture") val profilePicture: String
)