package com.happycampers.explodingblobtoss

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.happycampers.explodingblobtoss.Hosts.ShakeDetector
import android.content.Context.VIBRATOR_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.os.Vibrator



class MainGame : Fragment() {
    private lateinit var shakeDetector: ShakeDetector
    private lateinit var hapticFeedback: Vibrator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        shakeDetector = ShakeDetector(this.context!!)
        hapticFeedback = this.context!!.getSystemService(VIBRATOR_SERVICE) as Vibrator


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.main_game_screen, container, false)
    }

    override fun onResume() {
        super.onResume()
        if (shakeDetector.isSupported()) {
            shakeDetector.startListening(object : ShakeDetector.ShakeListener {
                override fun onShake(force: Float) {
                    //do something once shaken
                }
            })

        }
    }
    override fun onPause() {
        super.onPause()
        shakeDetector.stopListening()
    }


}
