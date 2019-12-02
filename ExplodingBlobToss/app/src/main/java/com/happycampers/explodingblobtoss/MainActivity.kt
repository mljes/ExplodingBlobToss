package com.happycampers.explodingblobtoss


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
import android.view.HapticFeedbackConstants.VIRTUAL_KEY
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Switch
import kotlinx.android.synthetic.main.main_menu.*

const val SHAREDPREF_NAME = "setting_preferences"
const val AUDIO_SETTING = "audio_setting"
const val VIBRATE_SETTING = "vibrate_setting"


class MainActivity : AppCompatActivity() {
    companion object{
       lateinit var audioSwitch: Switch
        lateinit var vibrateSwitch: Switch
     }
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)
        sharedPreferences = getSharedPreferences(SHAREDPREF_NAME, Context.MODE_PRIVATE)
        val animation: Animation = AnimationUtils.loadAnimation(this,R.anim.bounce)
        audioSwitch = findViewById(R.id.audio_switch)
        vibrateSwitch = findViewById(R.id.haptic_switch)
        audioSwitch.isChecked = sharedPreferences.getBoolean(AUDIO_SETTING,true)
        vibrateSwitch.isChecked = sharedPreferences.getBoolean(VIBRATE_SETTING,true)
        //button animations
        start_btn.startAnimation(animation)
        tutorial_btn.startAnimation(animation)
        settings_btn.startAnimation(animation)


        start_btn.setOnClickListener {
            start_btn.performHapticFeedback(VIRTUAL_KEY,FLAG_IGNORE_GLOBAL_SETTING)
                val intent = Intent(this, WifiPeerSetupActivity::class.java).apply {
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

        back_button.setOnClickListener {
            back_button.performHapticFeedback(VIRTUAL_KEY,FLAG_IGNORE_GLOBAL_SETTING)
            pause_menu.visibility = View.INVISIBLE
            with(sharedPreferences.edit()){
                putBoolean(AUDIO_SETTING, audioSwitch.isChecked)
                putBoolean(VIBRATE_SETTING, vibrateSwitch.isChecked)
                apply()
            }
        }
        settings_btn.setOnClickListener {
            settings_btn.performHapticFeedback(VIRTUAL_KEY,FLAG_IGNORE_GLOBAL_SETTING)
            pause_menu.visibility = View.VISIBLE
        }
    }
}
