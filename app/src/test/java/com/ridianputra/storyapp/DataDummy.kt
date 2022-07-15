package com.ridianputra.storyapp

import com.ridianputra.storyapp.data.network.response.ListStoryItem
import com.ridianputra.storyapp.data.network.response.LoginResult
import com.ridianputra.storyapp.data.network.response.SignUpResponse

object DataDummy {
    fun generateLoginResult(): LoginResult {
        return LoginResult("Ridian", "ridian10", "token")
    }
    fun generateSignUp(): SignUpResponse {
        return SignUpResponse(false, "success")
    }
    fun generateStoriesList(): List<ListStoryItem> {
        val storiesList = ArrayList<ListStoryItem>()
        for (i in 0..10) {
            val story = ListStoryItem(
                "Photo URL",
                "Created At",
                "Name",
                "Description",
                50.5,
                "id",
                80.5
            )
            storiesList.add(story)
        }
        return storiesList
    }
}