package com.geonwoo.assignment.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TodoDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("completed")
    val isCompleted: Boolean,
    @SerializedName("created_at")
    val createdAt: Long
)
