package com.example.quizzify.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import com.example.quizzify.BottomNavBar.CustomBottomNavigation
import com.example.quizzify.R
import com.example.quizzify.ViewModels.GlobalFragViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class LiveQuizFragment : Fragment() {


    private lateinit var GlobalFragVM: GlobalFragViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_live_quiz, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Ensure BottomNavBar is visible
        requireActivity().findViewById<CustomBottomNavigation>(R.id.bottomNavigation).visibility = View.VISIBLE

        val CreateRoom=view.findViewById<LinearLayout>(R.id.CreateRoom)
        val JoinRoom=view.findViewById<LinearLayout>(R.id.JoinRoom)

        GlobalFragVM= ViewModelProvider(this)[GlobalFragViewModel::class.java]

        CreateRoom.setOnClickListener {
            ReplaceFrag(ActiveRooms())
        }
        JoinRoom.setOnClickListener {
            ReplaceFrag(PreRegisteredRooms())
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragmentManager = requireActivity().supportFragmentManager
                if (fragmentManager.backStackEntryCount == 1) {
                    requireActivity().finishAffinity() // 🔥 Ensures complete exit
                } else {
                    fragmentManager.popBackStack() // ✅ Properly handles back navigation
                }
            }
        })
    }
    fun ReplaceFrag(Frag:Fragment){
        val fragManager=parentFragmentManager
        val fragTransaction=fragManager.beginTransaction()
        GlobalFragVM.currentFragment=Frag
        fragTransaction.addToBackStack(null)
        fragTransaction.replace(R.id.FrameLayout,Frag)
        fragTransaction.commit()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragmentManager = requireActivity().supportFragmentManager
                if (fragmentManager.backStackEntryCount == 1) {
                    requireActivity().finishAffinity() // 🔥 Ensures complete exit
                } else {
                    fragmentManager.popBackStack() // ✅ Properly handles back navigation
                }
            }
        })
        }
}