package com.moony.calc.fragments

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moony.calc.R
import com.moony.calc.activities.AddSavingGoalActivity
import com.moony.calc.activities.SavingDetailActivity
import com.moony.calc.adapter.SavingBoxAdapter
import com.moony.calc.base.BaseFragment
import com.moony.calc.database.SavingViewModel
import com.moony.calc.model.Saving
import kotlinx.android.synthetic.main.fragment_saving_box.*

class SavingBoxFragment() : BaseFragment() {
    private lateinit var savings: List<Saving>
    private val savingViewModel: SavingViewModel by lazy { ViewModelProvider(this)[SavingViewModel::class.java] }

    companion object {
        const val SAVING_KEY = "com.moony.calc.fragments.SavingBoxFragment.SAVING_KEY"
    }

    override fun init() {
        initControl()
        initEvent()
    }

    private fun initEvent() {
        btn_add_saving_goals.setOnClickListener {
            startActivity(Intent(activity, AddSavingGoalActivity::class.java))
        }
        rv_saving_box.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0 && !btn_add_saving_goals.isShown)
                    btn_add_saving_goals.show()
                else if (dy > 0 && btn_add_saving_goals.isShown)
                    btn_add_saving_goals.hide();
                btn_add_saving_goals.id
            }
        })
    }

    private fun initControl() {
        rv_saving_box.layoutManager = LinearLayoutManager(context)
        savingViewModel.getAllSaving().observe(this,
            Observer { savings ->
                if (savings.isNotEmpty()) {
                    val adapter = SavingBoxAdapter(activity!!, savings) {
                        val saving: Saving = it as Saving
                        val intent: Intent = Intent(baseContext, SavingDetailActivity::class.java)
                        intent.putExtra(SAVING_KEY,saving)
                        startActivity(intent)
                    }
                    rv_saving_box.adapter = adapter
                    txt_add_goal_manual.visibility = View.GONE
                    txt_empty_goal.visibility = View.GONE
                    rv_saving_box.visibility = View.VISIBLE
                } else {
                    txt_add_goal_manual.visibility = View.VISIBLE
                    txt_empty_goal.visibility = View.VISIBLE
                    rv_saving_box.visibility = View.GONE
                }

            })
    }

    override fun getLayoutId(): Int = R.layout.fragment_saving_box

}