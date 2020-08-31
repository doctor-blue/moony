package com.moony.calc.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.moony.calc.R
import com.moony.calc.activities.CategoriesActivity
import com.moony.calc.base.BaseFragment
import com.moony.calc.database.SavingViewModel
import com.moony.calc.keys.MoonyKey
import com.moony.calc.model.Category
import com.moony.calc.model.Saving
import com.moony.calc.utils.AssetFolderManager
import com.moony.calc.utils.decimalFormat
import kotlinx.android.synthetic.main.fragment_add_saving_goal.*
import java.text.SimpleDateFormat
import java.util.*

class AddSavingGoalFragment : BaseFragment() {

    private val calendar: Calendar = Calendar.getInstance()
    private var deadLine: String = ""
    private val savingViewModel: SavingViewModel by lazy { ViewModelProvider(this)[SavingViewModel::class.java] }
    private var category: Category? = null

    companion object {
        const val KEY_PICK_CATEGORY = 1101
    }

    override fun getLayoutId(): Int = R.layout.fragment_add_saving_goal

    override fun init() {
        initControl()
        initEvent()
    }

    private fun initControl() {
        toolbar_add_saving_goal.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.mnu_save) {
                saveSavingGoal()
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun saveSavingGoal() {
        val snackbar: Snackbar =
            Snackbar.make(layout_root_add_goal, R.string.empty_date_error, Snackbar.LENGTH_LONG)
        snackbar.setTextColor(resources.getColor(R.color.white))
        snackbar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorAccent))
        snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE

        when {
            edt_goal_description.text.toString().trim().isEmpty() -> {
                textInput_goal_description.error = resources.getString(R.string.empty_error)
            }
            edt_goal_amount.text.toString().trim().isEmpty() -> {
                textInput_goal_amount.error = resources.getString(R.string.empty_error)
            }
            txt_due_date.text.toString().trim().isEmpty() -> {
                hideKeyboard()
                snackbar.show()
            }
            txt_title_category_add_saving.text.toString().trim().isEmpty() -> {
                hideKeyboard()
                snackbar.setText(R.string.empty_category_error)
                snackbar.show()
            }
            else -> {
                val saving = Saving(
                    edt_goal_description.text.toString().trim(),
                    edt_goal_amount.text.toString().trim().toDouble(),
                    deadLine,
                    category!!.idCategory,
                    ""
                )
                savingViewModel.insertSaving(saving)
                requireActivity().onBackPressed()
            }
        }
    }

    private fun initEvent() {

        edt_goal_amount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                s?.let {
                    if (it.isNotEmpty()) {
                        textInput_goal_amount.error = null
                        if (it.toString().contains('.') || it.toString().contains(',')) {
                            /**
                             * "${handleTextToDouble(it.toString())}0" nhiều trường hợp nó ở dạng #. thay vì #.# nên thêm 0 để đc chuỗi dạng #.0
                             * chuyển về kiểu double sau đó format nó sẽ được chuỗi ở dạng #.00 và lấy độ dài làm max length cho edit_text
                             */
                            val maxLength = "${handleTextToDouble(it.toString())}1".toDouble()
                                .decimalFormat().length
                            edt_goal_amount.filters = arrayOf(InputFilter.LengthFilter(maxLength))
                        } else {
                            edt_goal_amount.filters = arrayOf(InputFilter.LengthFilter(13))
                            if (it.length - 1 == 12) {
                                //kiểm tra nếu kí tự cuối cùng không là . or , thì xóa kí tự đó đi
                                val lastChar = it[12]
                                if (!(lastChar == '.' || lastChar == ',')) {
                                    edt_goal_amount.setText(it.subSequence(0, 12))
                                    edt_goal_amount.setSelection(12)
                                }
                            }
                        }
                    }
                }
            }
        })

        edt_goal_description.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (edt_goal_description.text.toString().trim().isNotEmpty()) {
                    textInput_goal_description.error = null
                }
            }
        })

        layout_goal_calendar.setOnClickListener {
            pickDateTime()
        }

        layout_goal_categories.setOnClickListener {
            val intent = Intent(requireContext(), CategoriesActivity::class.java)
            startActivityForResult(
                intent,
                KEY_PICK_CATEGORY
            )
        }

        toolbar_add_saving_goal.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }


    private fun handleTextToDouble(s: String): String {
        var text = s
        if (text.contains(',')) {
            text = text.replace(',', '.')
        }
        return text
    }

    @SuppressLint("SetTextI18n")
    private fun pickDateTime() {
        val dialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            deadLine = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(calendar.time)
            txt_due_date.text = resources.getString(R.string.due_date) + " " + deadLine
        }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == KEY_PICK_CATEGORY)
            if (resultCode == Activity.RESULT_OK) {
                category = data?.getSerializableExtra(MoonyKey.pickCategory) as Category?
                Glide.with(this).load(AssetFolderManager.assetPath + category!!.iconUrl)
                    .into(img_goal_category)
                txt_title_category_add_saving.text = category!!.title

            }
    }

    private fun hideKeyboard() {
        (requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            requireActivity().window.decorView.windowToken, 0
        )
    }
}