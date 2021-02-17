package com.taxiappclone.common.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import com.nandroidex.emailhelper.EmailIntentBuilder
import com.rengwuxian.materialedittext.MaterialEditText
import com.taxiappclone.common.R


class HelpActivity : AppCompatActivity() {

    private var toolbar: Toolbar? = null
    private var actionBar: ActionBar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        actionBar = supportActionBar
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar!!.setHomeButtonEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.title = "Help"

        findViewById<AppCompatTextView>(R.id.tvCall).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:+91 89556 70599")
            startActivity(intent)
        }
        findViewById<AppCompatButton>(R.id.btnSend).setOnClickListener {
            val name = findViewById<MaterialEditText>(R.id.etName).text.toString()
            val mobile = findViewById<MaterialEditText>(R.id.etMobile).text.toString()
            val message = findViewById<MaterialEditText>(R.id.etMessage).text.toString()
            if (name.trim().isNotEmpty() && message.trim().isNotEmpty() && mobile.trim().isNotEmpty()) {
                val body = message +
                        "\n" +
                        "\n" +
                        name +
                        "\n" +
                        mobile
                EmailIntentBuilder.from(this@HelpActivity)
                        .to("admin@mbsindia.co.in")
                        .subject("Help - MBS")
                        .body(body)
                        .start()

                findViewById<MaterialEditText>(R.id.etName).setText("")
                findViewById<MaterialEditText>(R.id.etMobile).setText("")
                findViewById<MaterialEditText>(R.id.etMessage).setText("")
            } else {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}