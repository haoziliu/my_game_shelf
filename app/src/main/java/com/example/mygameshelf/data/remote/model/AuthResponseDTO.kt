package com.example.mygameshelf.data.remote.model

import com.google.gson.annotations.SerializedName

data class AuthResponseDTO(
    //    "access_token": "y5nllnvf15qxpkmt6d41ss94qtunn1",
    //    "expires_in": 5558358, // seconds
    //    "token_type": "bearer"
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("expires_in")
    val expiresIn: Int,

    @SerializedName("token_type")
    val tokenType: String
)
