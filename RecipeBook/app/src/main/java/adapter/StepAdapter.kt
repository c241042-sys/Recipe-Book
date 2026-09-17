package com.example.recipebook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StepAdapter(
    private var stepList: List<Step>,
    private val onItemClick: (Step) -> Unit
) : RecyclerView.Adapter<StepAdapter.StepViewHolder>() {

    class StepViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val number: TextView =
            itemView.findViewById(
                R.id.stepNumber
            )

        val title: TextView =
            itemView.findViewById(
                R.id.stepTitle
            )

        val description: TextView =
            itemView.findViewById(
                R.id.stepDescription
            )

        val timer: TextView =
            itemView.findViewById(
                R.id.stepTimer
            )
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StepViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_step,
                    parent,
                    false
                )

        return StepViewHolder(view)
    }


    override fun onBindViewHolder(
        holder: StepViewHolder,
        position: Int
    ) {

        val step =
            stepList[position]

        holder.number.text =
            step.stepNumber.toString()

        holder.title.text =
            step.title

        holder.description.text =
            step.description


        if (step.timer > 0) {

            holder.timer.text =
                "⏱ ${formatTime(step.timer)}"

        } else {

            holder.timer.text =
                "タイマーなし"
        }


        // タップで編集
        holder.itemView.setOnClickListener {

            onItemClick(step)
        }
    }


    override fun getItemCount(): Int {

        return stepList.size
    }


    fun updateList(
        newList: List<Step>
    ) {

        stepList =
            newList

        notifyDataSetChanged()
    }


    private fun formatTime(
        seconds: Int
    ): String {

        val minutes =
            seconds / 60

        val remainingSeconds =
            seconds % 60

        return if (
            minutes > 0 &&
            remainingSeconds > 0
        ) {

            "${minutes}分${remainingSeconds}秒"

        } else if (minutes > 0) {

            "${minutes}分"

        } else {

            "${remainingSeconds}秒"
        }
    }
}