package com.happycampers.explodingblobtoss

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.net.InetAddress
import android.widget.*
import java.lang.reflect.Type
import java.util.*

class GameEndActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.round_end)

        val isWinner = intent.getBooleanExtra("IS_WINNER", false)
        val serverAddress = intent.getSerializableExtra("SERVER_ADDRESS") as InetAddress
        val deviceIsOwner = intent.getBooleanExtra("IS_OWNER", false)

        val roundStatusTextView = findViewById<TextView>(R.id.round_result_title)
        if (isWinner) {
            roundStatusTextView.text = "You won!"
        }
        else {
            roundStatusTextView.text = "You lost."
        }

        findViewById<Button>(R.id.next_round_btn).setOnClickListener {
            val intent = Intent(this@GameEndActivity, GameActivity::class.java).apply {
                this.putExtra("IS_OWNER", deviceIsOwner)
                this.putExtra("SERVER_ADDRESS", serverAddress)
            }

            startActivity(intent)
        }
        findViewById<Button>(R.id.restart_btn).setOnClickListener {
            val intent = Intent(this@GameEndActivity, WifiPeerSetupActivity::class.java)

            startActivity(intent)
        }
        findViewById<Button>(R.id.quit_btn).setOnClickListener {
            onDestroy()
        }
    }
}
