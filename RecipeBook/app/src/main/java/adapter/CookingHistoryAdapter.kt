package com.example.recipebook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CookingHistoryAdapter(
    private var historyList: List<CookingHistory>
) : RecyclerView.Adapter<CookingHistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val recipeName: TextView =
            itemView.findViewById(
                R.id.historyRecipeName
            )

        val startTime: TextView =
            itemView.findViewById(
                R.id.historyStartTime
            )

        val finishTime: TextView =
            itemView.findViewById(
                R.id.historyFinishTime
            )

        val elapsedTime: TextView =
            itemView.findViewById(
                R.id.historyElapsedTime
            )

        val comment: TextView =
            itemView.findViewById(
                R.id.historyComment
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_cooking_history,
                    parent,
                    false
                )

        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: HistoryViewHolder,
        position: Int
    ) {

        val history =
            historyList[position]

        holder.recipeName.text =
            history.recipeName

        holder.startTime.text =
            "開始：${history.startTime}"

        holder.finishTime.text =
            "終了：${history.finishTime}"

        holder.elapsedTime.text =
            "調理時間：${formatElapsedTime(history.elapsedTime)}"

        if (history.comment.isNotEmpty()) {

            holder.comment.visibility =
                View.VISIBLE

            holder.comment.text =
                "メモ：${history.comment}"

        } else {

            holder.comment.visibility =
                View.GONE
        }
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    fun updateList(
        newList: List<CookingHistory>
    ) {

        historyList =
            newList

        notifyDataSetChanged()
    }

    private fun formatElapsedTime(
        seconds: Int
    ): String {

        val hours =
            seconds / 3600

        val minutes =
            (seconds % 3600) / 60

        val remainingSeconds =
            seconds % 60

        return when {

            hours > 0 ->
                "${hours}時間${minutes}分"

            minutes > 0 ->
                "${minutes}分${remainingSeconds}秒"

            else ->
                "${remainingSeconds}秒"
        }
    }
}