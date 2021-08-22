package com.example.runningtracker.presenter

interface MainContract {
    interface View {
        fun showViewLoading()
        fun hideViewLoading()
    }

    interface Presenter<View : MainContract.View> {
        fun attachView(view: View)
        fun detach()
    }
}