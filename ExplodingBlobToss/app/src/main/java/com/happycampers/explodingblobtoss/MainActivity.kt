package com.happycampers.explodingblobtoss


import android.content.Intent
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
import android.view.HapticFeedbackConstants.VIRTUAL_KEY
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.main_menu.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)
        val animation: Animation = AnimationUtils.loadAnimation(this,R.anim.bounce)

        //button animations
        start_btn.startAnimation(animation)
        tutorial_btn.startAnimation(animation)

        val startButton = findViewById<Button>(R.id.start_btn)

        startButton.setOnClickListener {
            startButton.performHapticFeedback(VIRTUAL_KEY,FLAG_IGNORE_GLOBAL_SETTING)
            val intent = Intent(this, WifiPeerSetupActivity::class.java).apply {
                //?
            }
            startActivity(intent)
        }

        tutorial_btn.setOnClickListener {
            tutorial_btn.performHapticFeedback(VIRTUAL_KEY,FLAG_IGNORE_GLOBAL_SETTING)
            val intent = Intent(this,Tutorial::class.java).apply {
                this.putExtra("IS_FIRST_ROUND", true)
            }
            startActivity(intent)
        }



    }

}


