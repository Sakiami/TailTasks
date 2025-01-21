package com.example.tailtasks.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tailtasks.R
import com.example.tailtasks.models.task_model

class TaskAdapter(private val taskList:ArrayList<task_model>): RecyclerView.Adapter<TaskAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskName: TextView
        val taskDescription: TextView
        val dueDate: TextView
        val frequency: TextView
        val checkButton: CheckBox
        val paw: ImageView

        init {
            // Define click listener for the ViewHolder's View
            taskName = view.findViewById(R.id.taskName)
            taskDescription = view.findViewById(R.id.taskDesc)
            dueDate = view.findViewById(R.id.dueDate)
            frequency = view.findViewById(R.id.frequency)
            paw = view.findViewById(R.id.pawPic)
            checkButton = view.findViewById(R.id.checkBox)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_card, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskAdapter.ViewHolder, position: Int) {
        val currentTask = taskList[position]
        holder.taskName.text = currentTask.TaskName
        holder.taskDescription.text = currentTask.Description
        holder.dueDate.text = "Due Date:\n"+currentTask.DueDate
        holder.frequency.text = "Frequency:\n"+currentTask.Frequency
        holder.paw.setImageResource(R.drawable.custom_background)
    }

    override fun getItemCount() = taskList.size

}