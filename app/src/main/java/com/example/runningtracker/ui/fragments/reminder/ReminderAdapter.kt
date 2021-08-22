package com.example.runningtracker.ui.fragments.reminder

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.runningtracker.RunningTrackerApplication
import com.example.runningtracker.database.entity.ReminderEntity
import com.example.runningtracker.databinding.RowReminderBinding
import java.text.SimpleDateFormat
import java.util.*

class ReminderAdapter(private val listener: OnReminderClickListener) :
    RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    var reminderList = mutableListOf<ReminderEntity>()

    inner class ReminderViewHolder(private val binding: RowReminderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.closeBtn.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val reminder = reminderList[adapterPosition]
                    listener.onReminderBtnCloseClick(reminder,adapterPosition)
                }
            }
            binding.layoutReminder.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val reminder = reminderList[adapterPosition]
                    listener.onReminderLayoutClick(reminder,adapterPosition)
                }
            }
        }

        fun bind(reminder: ReminderEntity) {
            binding.apply {
                RunningTrackerApplication.calendar[Calendar.HOUR_OF_DAY] = reminder.hours
                RunningTrackerApplication.calendar[Calendar.MINUTE] = reminder.minutes
                val timeString = SimpleDateFormat(
                    "HH:mm",
                    Locale.getDefault()
                ).format(RunningTrackerApplication.calendar.time)
                if (reminder.date == 0.toLong()) {
                    binding.textTime.text = timeString
                } else {
                    val dateString =
                        SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(reminder.date)
                    val formatTextTime = "${timeString}\n$dateString"
                    binding.textTime.text = formatTextTime
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = RowReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val currentItem = reminderList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int = reminderList.size

    fun setData(reminderList: MutableList<ReminderEntity>) {
        this.reminderList = reminderList
    }

    interface OnReminderClickListener {
        fun onReminderLayoutClick(reminder: ReminderEntity, position: Int)
        fun onReminderBtnCloseClick(reminder: ReminderEntity, position: Int)
    }

}