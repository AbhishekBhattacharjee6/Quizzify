package com.example.quizzify

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.quizzify.Fragments.CategoryFragment
import com.example.quizzify.Fragments.CustomQuizFragment
import com.example.quizzify.Fragments.FinalResultFragment
import com.example.quizzify.Fragments.LiveQuizFragment
import com.example.quizzify.Fragments.ProfileFragment
import com.example.quizzify.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val BottomNavBar=findViewById<BottomNavigationView>(R.id.BottomNavBar)
        replaceFrameWithFragment(LiveQuizFragment())
       BottomNavBar.setOnItemReselectedListener {
           when(it.itemId){
               R.id.Practice->replaceFrameWithFragment(CategoryFragment())
               R.id.LiveQuiz->replaceFrameWithFragment(LiveQuizFragment())
               R.id.CustomQuiz->replaceFrameWithFragment(CustomQuizFragment())
               R.id.Profile->replaceFrameWithFragment(ProfileFragment())
           }
       }
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
        val fragManager=supportFragmentManager
        val CurrentFrag=fragManager.findFragmentByTag("FinalResult")
        if(CurrentFrag is FinalResultFragment && CurrentFrag.isVisible){
            if(fragManager.backStackEntryCount>0){
                fragManager.popBackStackImmediate(null,FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            replaceFrameWithFragment(LiveQuizFragment())
        }
    }
    private fun replaceFrameWithFragment(frag:Fragment) {
        val fragManager=supportFragmentManager
        val fragTransaction=fragManager.beginTransaction()
        fragTransaction.addToBackStack(null)
        fragTransaction.replace(R.id.FrameLayout,frag)
        fragTransaction.commit()
    }
    private fun replaceandEmptyBackStack(frag:Fragment){
        val fragManager=supportFragmentManager
        if(fragManager.backStackEntryCount>0){

        }
    }
}