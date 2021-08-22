package com.example.runningtracker.presenter.reminder

import com.example.runningtracker.database.entity.ReminderEntity
import com.example.runningtracker.presenter.MainContract

interface PresenterReminderContract {

    interface IViewReminder : MainContract.View {
        fun setData(reminderList: List<ReminderEntity>)
        fun setRequestIdForReminder(id:Int)
        fun errorResponse(t: Throwable)
    }

    interface IPresenterReminder : MainContract.Presenter<IViewReminder> {
        fun addReminder(reminder: ReminderEntity)
        fun changeReminder(reminder: ReminderEntity)
        fun deleteReminder(id: Int)
        fun getListReminder()
        fun getLastIdReminder()
    }

}