package com.example.runningtracker.presenter.authorizations

import com.example.runningtracker.presenter.MainContract
import com.example.runningtracker.retrofit.pojo.authorizations.SignInRequest
import com.example.runningtracker.retrofit.pojo.authorizations.SignUpRequest

interface PresenterAuthorizationsContract {

    interface IViewAuthorizations : MainContract.View {
        fun errorResponse(t: Throwable)
        fun onSuccess()
    }

    interface IPresenterSignUp :
        MainContract.Presenter<IViewAuthorizations> {
        fun signUp(signUpRequest: SignUpRequest)
    }

    interface IPresenterSignIn :
        MainContract.Presenter<IViewAuthorizations> {
        fun signIn(signInRequest: SignInRequest)
    }
}