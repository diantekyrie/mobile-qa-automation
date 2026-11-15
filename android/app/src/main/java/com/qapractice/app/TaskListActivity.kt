package com.qapractice.app

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView

class TaskListActivity : Activity() {

    private val tasks = ArrayList<String>()
    private val handler = Handler(Looper.getMainLooper())
    private var syncing = false

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var taskList: ListView
    private lateinit var summary: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        findViewById<View>(R.id.root).padForSystemBars()

        val username = intent.getStringExtra(EXTRA_USERNAME).orEmpty()
        findViewById<TextView>(R.id.welcome_text).text = getString(R.string.welcome, username)

        summary = findViewById(R.id.task_summary)
        taskList = findViewById(R.id.task_list)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, tasks)
        taskList.adapter = adapter
        // Multiple-choice mode toggles the checkmark on tap; we only refresh the summary.
        taskList.setOnItemClickListener { _, _, _, _ -> updateSummary() }

        val input = findViewById<EditText>(R.id.new_task_input)
        findViewById<Button>(R.id.add_task_button).setOnClickListener {
            val name = input.text.toString().trim()
            if (name.isEmpty()) {
                input.error = getString(R.string.error_empty_task)
                return@setOnClickListener
            }
            input.error = null
            input.text.clear()
            tasks.add(name)
            adapter.notifyDataSetChanged()
            updateSummary()
        }

        val syncButton = findViewById<Button>(R.id.sync_button)
        val syncStatus = findViewById<TextView>(R.id.sync_status)
        syncButton.setOnClickListener {
            syncing = true
            syncButton.isEnabled = false
            syncStatus.setText(R.string.sync_in_progress)
            SyncIdlingResource.increment()
            // Simulates a slow network call, which is what tests must learn to wait for.
            handler.postDelayed({
                syncStatus.text = getString(R.string.sync_done, tasks.size)
                syncButton.isEnabled = true
                syncing = false
                SyncIdlingResource.decrement()
            }, SYNC_DELAY_MS)
        }

        findViewById<Button>(R.id.logout_button).setOnClickListener { finish() }

        updateSummary()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        if (syncing) SyncIdlingResource.decrement()
        super.onDestroy()
    }

    private fun updateSummary() {
        summary.text = getString(R.string.task_summary, tasks.size, taskList.checkedItemCount)
    }

    companion object {
        const val EXTRA_USERNAME = "username"
        const val SYNC_DELAY_MS = 2_000L
    }
}
