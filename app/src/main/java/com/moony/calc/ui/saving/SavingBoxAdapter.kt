package com.moony.calc.ui.saving

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.moony.calc.R
import com.moony.calc.model.Saving

class SavingBoxAdapter(
    private val context: FragmentActivity,
    private val onGoalCompleted: () -> Unit,
    private val onClick: (Any) -> Unit
) : RecyclerView.Adapter<SavingViewHolder>() {
    private var savings: List<Saving> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavingViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.saving_goals_item, parent, false)
        return SavingViewHolder(view, context, onGoalCompleted, onClick)
    }

    override fun getItemCount(): Int = savings.size


    override fun onBindViewHolder(holder: SavingViewHolder, position: Int) {
        holder.onBind(savings[position])
    }

    fun setSavings(savings: List<Saving>) {
        this.savings = savings
        notifyDataSetChanged()
    }
}