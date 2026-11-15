package com.qapractice.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class LoginActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        findViewById<View>(R.id.root).padForSystemBars()

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val error = findViewById<TextView>(R.id.login_error)

        findViewById<Button>(R.id.login_button).setOnClickListener {
            val user = username.text.toString().trim()
            val errorRes = Credentials.check(user, password.text.toString())
            if (errorRes != null) {
                error.setText(errorRes)
                error.visibility = View.VISIBLE
                return@setOnClickListener
            }
            error.visibility = View.GONE
            password.text.clear()
            startActivity(
                Intent(this, TaskListActivity::class.java)
                    .putExtra(TaskListActivity.EXTRA_USERNAME, user)
            )
        }
    }
}
