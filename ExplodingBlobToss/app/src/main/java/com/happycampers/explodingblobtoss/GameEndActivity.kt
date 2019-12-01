package com.happycampers.explodingblobtoss
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import java.net.InetAddress
import android.widget.*
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.round_end.*

class GameEndActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.round_end)

        val isWinner = intent.getBooleanExtra("IS_WINNER", false)
        val serverAddress = intent.getSerializableExtra("SERVER_ADDRESS") as InetAddress
        val deviceIsOwner = intent.getBooleanExtra("IS_OWNER", false)
        var gameText = intent.getStringExtra("END_REASON")
        val explodeAnimation = AnimationUtils.loadAnimation(this, R.anim.scaleup_fadeout)


        gameText ?: run {
            gameText = "Hmmm..."
        }

        val roundStatusTextView = findViewById<TextView>(R.id.round_result_title)
        roundStatusTextView.text = gameText

        GameActivity.roundNumber++

        if (isWinner) {
            Splat_ImageView.visibility = View.INVISIBLE
            Splat_ImageView2.visibility = View.INVISIBLE
            GameActivity.score++

        }
        else {
            Splat_ImageView.visibility = View.VISIBLE
            Splat_ImageView2.visibility = View.VISIBLE
            Splat_ImageView2.startAnimation(explodeAnimation)

        }

        //next round button
        findViewById<Button>(R.id.next_round_btn).setOnClickListener {
            next_round_btn.performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )

            val intent = Intent(this@GameEndActivity, GameActivity::class.java).apply {
                this.putExtra("IS_OWNER", deviceIsOwner)
                this.putExtra("SERVER_ADDRESS", serverAddress)
            }

            startActivity(intent)
        }
        //restart button
        findViewById<Button>(R.id.restart_btn).setOnClickListener {
            restart_btn.performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )
            goToWifiPeerSetupActivity()
        }
        //quit button
        findViewById<Button>(R.id.quit_btn).setOnClickListener {
            quit_btn.performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )

            GameActivity.roundNumber = 1
            GameActivity.score = 0

            val intent = Intent(this@GameEndActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        goToWifiPeerSetupActivity()
    }

    private fun goToWifiPeerSetupActivity() {
        GameActivity.roundNumber = 1
        GameActivity.score = 0

        val intent = Intent(this@GameEndActivity, WifiPeerSetupActivity::class.java).apply {
            this.putExtra("IS_FIRST_ROUND", false)
        }

        startActivity(intent)
    }
}
