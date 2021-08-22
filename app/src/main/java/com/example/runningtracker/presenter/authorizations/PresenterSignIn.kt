package com.example.runningtracker.presenter.authorizations

import android.content.SharedPreferences
import bolts.Task
import com.example.runningtracker.ApplicationConstants
import com.example.runningtracker.presenter.BasePresenter
import com.example.runningtracker.retrofit.ApiRunningTracker
import com.example.runningtracker.retrofit.pojo.authorizations.SignInRequest

class PresenterSignIn(
    private val provider: ApiRunningTracker,
    private val sharedPreferences: SharedPreferences
) : BasePresenter<PresenterAuthorizationsContract.IViewAuthorizations>(),
    PresenterAuthorizationsContract.IPresenterSignIn {

    companion object {
        const val INVALID_CREDENTIALS = "INVALID_CREDENTIALS"
    }

    private var isLoading = false
    private var isRecording = false
    private var token = ""

    override fun signIn(signInRequest: SignInRequest) {
        if (!isLoading) {
            getView().showViewLoading()
            Task.callInBackground {
                return@callInBackground provider.signIn(signIn = signInRequest).execute()
            }.onSuccess({
                isLoading = false
                it.result.body()?.let { body ->
                    if (body.error == INVALID_CREDENTIALS) {
                        getView().errorResponse(Throwable(INVALID_CREDENTIALS))
                    } else {
                        token = body.token
                    }
                }
            }, Task.UI_THREAD_EXECUTOR)
                .onSuccess({
                    if (token.isNotEmpty()) {
                        sharedPreferences.edit()
                            .putString(
                                ApplicationConstants.CONSTANTS_USER_TOKEN,
                                token
                            ).apply()
                        isRecording = true
                    }
                }, Task.BACKGROUND_EXECUTOR)
                .continueWith({
                    getView().hideViewLoading()
                    if (!isRecording) {
                        getView().errorResponse(Throwable(PresenterSignUp.ERROR))
                    } else {
                        getView().onSuccess()
                    }
                }, Task.UI_THREAD_EXECUTOR)
        }
    }
}