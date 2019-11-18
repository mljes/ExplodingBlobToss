package com.happycampers.explodingblobtoss

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.main_menu.*

class MainMenu : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val animation:Animation = AnimationUtils.loadAnimation(context,R.anim.bounce)

        start_btn.startAnimation(animation)
        tutorial_btn.startAnimation(animation)
        lore_btn.startAnimation(animation)

        lore_btn.setOnClickListener { view:View->
            view.findNavController().navigate(R.id.action_main_menu_to_lore)
        }
        tutorial_btn.setOnClickListener { view:View ->
            view.findNavController().navigate(R.id.action_main_menu_to_tutorial)
        }
        start_btn.setOnClickListener { view:View->
            view.findNavController().navigate(R.id.action_main_menu_to_connect_to_peers)
        }
        (activity as AppCompatActivity).supportActionBar?.hide()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.main_menu, container, false)
    }


    companion object {

        fun newInstance(): MainMenu {
            val fragment = MainMenu()
            return fragment
        }
    }



}
