package com.example.runningtracker.presenter


abstract class BasePresenter<View : MainContract.View> : MainContract.Presenter<View> {
    private var view: View? = null

    override fun attachView(view: View) {
        this.view = view
    }

    override fun detach() {
        this.view = null
    }

    protected fun isViewAttached(): Boolean {
        return view != null
    }

    protected fun getView(): View {
        return view ?: error("View is not attached")
    }
}