package com.dicoding.todoapp.ui.detail


import android.content.Intent
import android.os.Bundle

import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope

import com.dicoding.todoapp.R

import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.ui.list.TaskActivity

import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.TASK_ID

import kotlinx.coroutines.launch

class DetailTaskActivity : AppCompatActivity() {
    private lateinit var detailTaskViewModel: DetailTaskViewModel
    private lateinit var title: EditText
    private lateinit var description: EditText
    private lateinit var due: EditText
    private lateinit var delete: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        //TODO 11 : Show detail task and implement delete action
        val factory = ViewModelFactory.getInstance(this)
        detailTaskViewModel = ViewModelProvider(this, factory).get(DetailTaskViewModel::class.java)
        val id = intent.getIntExtra(TASK_ID, 0)
        detailTaskViewModel.setTaskId(id)
        title = findViewById(R.id.detail_ed_title)
        description = findViewById(R.id.detail_ed_description)
        due = findViewById(R.id.detail_ed_due_date)
        delete = findViewById(R.id.btn_delete_task)
        detailTaskViewModel.task.observe(this) {
            if (it != null) {
                lifecycleScope.launch {
                    title.setText(it.title)
                    description.setText(it.description)
                    due.setText(DateConverter.convertMillisToString(it.dueDateMillis))
                }
            }


        }
        delete.setOnClickListener {

            detailTaskViewModel.deleteTask()
            startActivity(Intent(this, TaskActivity::class.java))

        }
    }

}