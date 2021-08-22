package com.example.runningtracker.ui.fragments.reminder

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context.ALARM_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.runningtracker.R
import com.example.runningtracker.RunningTrackerApplication
import com.example.runningtracker.RunningTrackerApplication.Companion.calendar
import com.example.runningtracker.database.entity.ReminderEntity
import com.example.runningtracker.databinding.FragmentReminderBinding
import com.example.runningtracker.presenter.authorizations.PresenterSignUp
import com.example.runningtracker.presenter.reminder.PresenterReminder
import com.example.runningtracker.presenter.reminder.PresenterReminderContract
import com.example.runningtracker.ui.fragments.BaseFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*


class ReminderFragment :
    BaseFragment<PresenterReminderContract.IPresenterReminder, PresenterReminderContract.IViewReminder>(),
    PresenterReminderContract.IViewReminder, ReminderAdapter.OnReminderClickListener {

    companion object {
        private const val TAG_TIME_PICKER_DIALOG = "TAG_TIME_PICKER_DIALOG"
        private const val TAG_DATA_PICKER_DIALOG = "TAG_DATA_PICKER_DIALOG"
        const val REQUEST_ID = "REQUEST_ID"
    }

    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!
    private val adapter: ReminderAdapter by lazy { ReminderAdapter(this) }
    private var requestId: Int = 1
    private var alertDialog: AlertDialog? = null
    private var alarmManager: AlarmManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPresenter().getListReminder()
        binding.addReminderBtn.setOnClickListener {
            createTimePickerDialog()
        }
        alarmManager = activity?.getSystemService(ALARM_SERVICE) as AlarmManager
        initializerRecyclerView()
        getPresenter().getLastIdReminder()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        alertDialog?.dismiss()
        alertDialog = null
        alarmManager = null
        _binding = null
    }

    private fun initializerRecyclerView() {
        binding.rvReminderList.adapter = adapter
        binding.rvReminderList.setHasFixedSize(true)
        binding.rvReminderList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun createTimePickerDialog() {
        calendar.time = Date()
        val clockFormat = TimeFormat.CLOCK_24H
        val timePickerDialog = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .setTitleText(requireContext().getString(R.string.adding_a_reminder))
            .build()
        timePickerDialog.show(childFragmentManager, TAG_TIME_PICKER_DIALOG)
        timePickerDialog.addOnPositiveButtonClickListener {
            createDatePickerDialog(timePickerDialog.hour, timePickerDialog.minute)
        }
    }

    private fun createDatePickerDialog(hours: Int, minutes: Int) {
        val datePickerDialog = MaterialDatePicker.Builder.datePicker()
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setTitleText(requireContext().getString(R.string.adding_a_reminder))
            .build()
        datePickerDialog.show(childFragmentManager, TAG_DATA_PICKER_DIALOG)
        datePickerDialog.addOnPositiveButtonClickListener { dateLong ->
            addOneTimeReminder(requestId = requestId, dateLong, hours, minutes)
        }
        datePickerDialog.addOnNegativeButtonClickListener {
            addRepeatReminder(requestId = requestId, hours, minutes)
        }

    }

    private fun addOneTimeReminder(requestId: Int, date: Long, hours: Int, minutes: Int) {
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
            .putExtra(REQUEST_ID, requestId)
        val pendingIntent =
            PendingIntent.getBroadcast(context, requestId, intent, 0)
        calendar.time = Date(date)
        calendar[Calendar.HOUR_OF_DAY] = hours
        calendar[Calendar.MINUTE] = minutes
        if (System.currentTimeMillis() < calendar.timeInMillis) {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    alarmManager?.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                    val reminder = ReminderEntity(requestId, date, hours, minutes)
                    getPresenter().addReminder(reminder)
                    adapter.reminderList.add(adapter.itemCount, reminder)
                    adapter.notifyItemInserted(adapter.itemCount)
                }
                else -> {
                    alarmManager?.set(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            }
        } else {
            Toast.makeText(
                context,
                getString(R.string.toast_missing_date),
                Toast.LENGTH_LONG
            ).show()
        }
        getPresenter().getLastIdReminder()
    }

    private fun addRepeatReminder(requestId: Int, hours: Int, minutes: Int) {
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
            .putExtra(REQUEST_ID, requestId)
        val pendingIntent = PendingIntent.getBroadcast(context, requestId, intent, 0)
        calendar[Calendar.HOUR_OF_DAY] = hours
        calendar[Calendar.MINUTE] = minutes
        if (System.currentTimeMillis() < calendar.timeInMillis) {
            alarmManager?.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
            val reminder = ReminderEntity(requestId, 0, hours, minutes)
            getPresenter().addReminder(reminder)
            adapter.reminderList.add(adapter.itemCount, reminder)
            adapter.notifyItemInserted(adapter.itemCount)
        } else {
            Toast.makeText(
                context,
                getString(R.string.toast_missing_date),
                Toast.LENGTH_LONG
            ).show()
        }
        getPresenter().getLastIdReminder()
    }

    private fun updateReminder(reminderId: Int, position: Int) {
        calendar.time = Date()
        val clockFormat = TimeFormat.CLOCK_24H
        val timePickerDialog = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .setTitleText(requireContext().getString(R.string.adding_a_reminder))
            .build()
        timePickerDialog.show(childFragmentManager, TAG_TIME_PICKER_DIALOG)
        timePickerDialog.addOnPositiveButtonClickListener {

            val datePickerDialog = MaterialDatePicker.Builder.datePicker()
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTitleText(requireContext().getString(R.string.adding_a_reminder))
                .build()
            datePickerDialog.show(childFragmentManager, TAG_DATA_PICKER_DIALOG)
            datePickerDialog.addOnPositiveButtonClickListener { dateLong ->

                val intent = Intent(context, ReminderBroadcastReceiver::class.java)
                    .putExtra(REQUEST_ID, requestId)
                val pendingIntent =
                    PendingIntent.getBroadcast(context, requestId, intent, 0)
                calendar.time = Date(dateLong)
                calendar[Calendar.HOUR_OF_DAY] = timePickerDialog.hour
                calendar[Calendar.MINUTE] = timePickerDialog.minute
                if (System.currentTimeMillis() < calendar.timeInMillis) {
                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                            alarmManager?.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                calendar.timeInMillis,
                                pendingIntent
                            )
                            val reminder = ReminderEntity(
                                reminderId,
                                dateLong,
                                timePickerDialog.hour,
                                timePickerDialog.minute
                            )
                            getPresenter().changeReminder(reminder)
                            adapter.reminderList[position] = reminder
                            adapter.notifyItemChanged(position)
                        }
                        else -> {
                            alarmManager?.set(
                                AlarmManager.RTC_WAKEUP,
                                calendar.timeInMillis,
                                pendingIntent
                            )
                        }
                    }
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.toast_missing_date),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun createPresenter(): PresenterReminderContract.IPresenterReminder {
        return PresenterReminder(RunningTrackerApplication.databaseHelper)
    }

    override fun setData(reminderList: List<ReminderEntity>) {
        adapter.setData(reminderList.toMutableList())
        adapter.notifyDataSetChanged()
    }

    override fun setRequestIdForReminder(id: Int) {
        this.requestId = id + 1
    }

    override fun errorResponse(t: Throwable) {
        when (t.message) {
            PresenterSignUp.ERROR -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_unexpected),
                    Toast.LENGTH_SHORT
                ).show()
            }
            PresenterReminder.ERROR_DELETE_DB -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.try_delete_reminder),
                    Toast.LENGTH_SHORT
                ).show()
            }
            PresenterReminder.ERROR_CHANGE_DB -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.try_change_reminder),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun showViewLoading() {
        binding.progressBar.isVisible = true
    }

    override fun hideViewLoading() {
        binding.progressBar.isVisible = false
    }

    override fun onReminderLayoutClick(reminder: ReminderEntity, position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.change_reminder_question))
        builder.setCancelable(false)
        builder.setPositiveButton(getString(R.string.yes)) { dialogs, _ ->
            if (reminder.date == 0.toLong()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.cant_change_repeating_reminder),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                updateReminder(reminderId = reminder.id, position = position)
            }
            dialogs.dismiss()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialogs, _ ->
            dialogs.dismiss()
        }
        alertDialog = builder.create()
        alertDialog?.let {
            it.show()
            it.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.GREEN)
            it.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }
    }

    override fun onReminderBtnCloseClick(reminder: ReminderEntity, position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.delete_reminder_question))
        builder.setCancelable(false)
        builder.setPositiveButton(getString(R.string.yes)) { dialogs, _ ->
            getPresenter().deleteReminder(reminder.id)
            cancelReminder(reminder.id)
            adapter.reminderList.removeAt(position)
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position, adapter.itemCount)
            dialogs.dismiss()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialogs, _ ->
            dialogs.dismiss()
        }
        alertDialog = builder.create()
        alertDialog?.let {
            it.show()
            it.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.GREEN)
            it.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }
    }

    private fun cancelReminder(requestId: Int) {
        val intent = Intent(requireContext(), ReminderBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), requestId, intent, 0)
        alarmManager?.cancel(pendingIntent)
    }

}