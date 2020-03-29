package com.moony.calc.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moony.calc.R
import com.moony.calc.base.BaseActivity
import com.moony.calc.database.TransactionViewModel
import com.moony.calc.model.DateTime
import com.moony.calc.model.Transaction

class TransactionAdapter(
    private val dates: List<DateTime>,
    private val activity: BaseActivity,
    private val itemClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.transaction_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dates.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(dates[position], itemClick)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtTransactionDay: TextView = itemView.findViewById(R.id.txt_transaction_day)
        private val txtDayOfWeek: TextView = itemView.findViewById(R.id.txt_transaction_day_of_week)
        private val txtIncome: TextView = itemView.findViewById(R.id.txt_transaction_income)
        private val txtExpenses: TextView = itemView.findViewById(R.id.txt_transaction_expenses)
        private val rvTransaction: RecyclerView = itemView.findViewById(R.id.rv_transaction_item)

        fun onBind(dateTime: DateTime, itemClick: (Transaction) -> Unit) {
            txtTransactionDay.text = dateTime.day.toString()

            val transactionViewModel =
                ViewModelProviders.of(activity).get(TransactionViewModel::class.java)
            //Lấy Transaction theo từng ngày trong tháng và gộp nó vào 1 list để hiển thị trong list con
            transactionViewModel.getAllTransactionByDate(dateTime.id).observe(activity, Observer {

                var income = 0.0
                var expenses = 0.0
                for (transaction in it) {
                    if (transaction.isIncome) income += transaction.money else expenses += transaction.money
                }

                txtExpenses.text = expenses.toString()
                txtIncome.text = income.toString()
                //itemClick ở đây là 1 hàm mà nội dung của hàm sẽ được viết ở TransactionFragment
                val adapter = TransactionChildrenAdapter(it, itemClick, activity)
                val layoutManager = LinearLayoutManager(activity)
                rvTransaction.setHasFixedSize(true)
                rvTransaction.layoutManager = layoutManager
                rvTransaction.adapter = adapter

            })
        }
    }
}