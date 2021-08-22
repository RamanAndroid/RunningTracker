package com.example.runningtracker.retrofit

import com.example.runningtracker.retrofit.pojo.authorizations.SignInRequest
import com.example.runningtracker.retrofit.pojo.authorizations.SignInResponse
import com.example.runningtracker.retrofit.pojo.authorizations.SignUpRequest
import com.example.runningtracker.retrofit.pojo.authorizations.SignUpResponse
import com.example.runningtracker.retrofit.pojo.running.CompletedTrackRequest
import com.example.runningtracker.retrofit.pojo.running.CompletedTrackResponse
import com.example.runningtracker.retrofit.pojo.running.PointsRequest
import com.example.runningtracker.retrofit.pojo.running.PointsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiRunningTracker {

    @POST("lesson-26.php?method=login")
    fun signIn(@Body signIn: SignInRequest): Call<SignInResponse>

    @POST("lesson-26.php?method=register")
    fun signUp(@Body signUp: SignUpRequest): Call<SignUpResponse>

    @POST("lesson-26.php?method=points")
    fun getListPointForCurrentTrack(@Body pointsRequest: PointsRequest?): Call<PointsResponse>

    @POST("lesson-26.php?method=save")
    fun saveTrack(@Body completedTrackRequest: CompletedTrackRequest): Call<CompletedTrackResponse>
}