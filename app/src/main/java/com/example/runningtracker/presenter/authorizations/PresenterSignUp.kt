package com.example.runningtracker.presenter.authorizations

import android.content.SharedPreferences
import bolts.Task
import com.example.runningtracker.ApplicationConstants
import com.example.runningtracker.presenter.BasePresenter
import com.example.runningtracker.retrofit.ApiRunningTracker
import com.example.runningtracker.retrofit.pojo.authorizations.SignUpRequest

class PresenterSignUp(
    private val provider: ApiRunningTracker,
    private val sharedPreferences: SharedPreferences
) :
    BasePresenter<PresenterAuthorizationsContract.IViewAuthorizations>(),
    PresenterAuthorizationsContract.IPresenterSignUp {

    companion object {
        const val INVALID_REQUEST_METHOD = "INVALID_REQUEST_METHOD"
        const val INVALID_REQUEST_DATA = "INVALID_REQUEST_DATA"
        const val INVALID_API_METHOD = "INVALID_API_METHOD"
        const val REQUIRED_FIELDS_ARE_EMPTY = "REQUIRED_FIELDS_ARE_EMPTY"
        const val INVALID_EMAIL = "INVALID_EMAIL"
        const val EMAIL_ALREADY_EXISTS = "EMAIL_ALREADY_EXISTS"
        const val ERROR_FROM_THE_SERVER = "ERROR_FROM_THE_SERVER"
        const val ERROR = "ERROR"
    }

    private var isLoading = false
    private var isRecording = false
    private var token = ""

    override fun signUp(signUpRequest: SignUpRequest) {
        if (!isLoading) {
            isLoading = true
            getView().showViewLoading()
            Task.callInBackground {
                return@callInBackground provider.signUp(signUp = signUpRequest).execute()
            }.onSuccess({
                getView().hideViewLoading()
                isLoading = false
                if (it.result.body()!!.error == INVALID_API_METHOD
                    || it.result.body()!!.error == INVALID_REQUEST_DATA
                    || it.result.body()!!.error == INVALID_REQUEST_METHOD
                ) {
                    getView().errorResponse(Throwable(ERROR_FROM_THE_SERVER))
                } else if (it.result.body()!!.error == REQUIRED_FIELDS_ARE_EMPTY) {
                    getView().errorResponse(Throwable(REQUIRED_FIELDS_ARE_EMPTY))
                } else if (it.result.body()!!.error == INVALID_EMAIL) {
                    getView().errorResponse(Throwable(INVALID_EMAIL))
                } else if (it.result.body()!!.error == EMAIL_ALREADY_EXISTS) {
                    getView().errorResponse(Throwable(EMAIL_ALREADY_EXISTS))
                } else if (it.result.body() == null) {
                    getView().errorResponse(Throwable(ERROR))
                } else {
                    return@onSuccess it.result.body()
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
                        getView().errorResponse(Throwable(ERROR))
                    } else {
                        getView().onSuccess()
                    }
                }, Task.UI_THREAD_EXECUTOR)
        }
    }
}