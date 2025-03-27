package com.example.quizzify

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.unit.Constraints
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.quizzify.BottomNavBar.CustomBottomNavigation
import com.example.quizzify.Fragments.CategoryFragment
import com.example.quizzify.Fragments.CustomQuizFragment
import com.example.quizzify.Fragments.FinalResultFragment
import com.example.quizzify.Fragments.LiveQuizFragment
import com.example.quizzify.Fragments.ProfileFragment
import com.example.quizzify.Fragments.SaveCollectionsFragment
import com.example.quizzify.databinding.ActivityMainBinding
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.example.quizzify.SharedPreference.ImagePreference
import com.example.quizzify.SharedPreference.NamePreference
import com.example.quizzify.ViewModelFactories.BasicInfoViewModelFactory
import com.example.quizzify.ViewModelFactories.RecentContestViewModelFactory
import com.example.quizzify.ViewModelFactories.SavedListNamesVMFactory
import com.example.quizzify.ViewModels.BasicInfoViewModel
import com.example.quizzify.ViewModels.GlobalFragViewModel
import com.example.quizzify.ViewModels.RecentContestViewModel
import com.example.quizzify.ViewModels.SavedListNamesViewModel
import com.example.quizzify.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var GlobalFragVM: GlobalFragViewModel

    @Inject
    lateinit var SavedListNamesVMFactory: SavedListNamesVMFactory

    @Inject
    lateinit var BasicInfoVMFactory: BasicInfoViewModelFactory

    @Inject
    lateinit var RecentContestVMFactory: RecentContestViewModelFactory

    val SavedList: SavedListNamesViewModel by viewModels {
        SavedListNamesVMFactory
    }
    val BasicInfoVM: BasicInfoViewModel by viewModels {
        BasicInfoVMFactory
    }
    val RecentContestVM: RecentContestViewModel by viewModels {
        RecentContestVMFactory
    }

    private var isDataLoaded: Boolean = false
    private var isAuthChecked: Boolean = false

    // Launcher for SignInActivity
    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Successfully signed in, proceed with loading the app
                loadMainActivity(null)
            } else {
                Toast.makeText(this, "Sign-in required!", Toast.LENGTH_SHORT).show()
                finish() // Close app if sign-in fails
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        (application as QuizApplication).applicationComponent.injectMainActivity(this)
        super.onCreate(savedInstanceState)

        // Keep splash screen until data + auth check is done
        PreLoadData()
        splashScreen.setKeepOnScreenCondition {
            !isDataLoaded || !isAuthChecked
        }

        checkAuthentication(savedInstanceState)

    }

    private fun checkAuthentication(savedInstanceState: Bundle?) {
        if (FirebaseAuth.getInstance().currentUser == null) {
            // If user is not logged in, launch SignInActivity
            val intent = Intent(this, SignIn::class.java)
            signInLauncher.launch(intent)
        } else {
            // If logged in, proceed
            Constants.UID= FirebaseAuth.getInstance().currentUser!!.uid
            val SharedPref=NamePreference(this)
            val ImgSharedPref=ImagePreference(this)
            Constants.Name=SharedPref.getUserName()
            Constants.ImageURI=ImgSharedPref.getUserImgURI()
            loadMainActivity(savedInstanceState)
        }
    }

    private fun loadMainActivity(savedInstanceState: Bundle?) {
        isAuthChecked = true
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val bottomNavigation = findViewById<CustomBottomNavigation>(R.id.bottomNavigation)
        GlobalFragVM = ViewModelProvider(this)[GlobalFragViewModel::class.java]

        if (savedInstanceState == null) {
            bottomNavigation.selectTab(CustomBottomNavigation.TAB_LIVE_CONTEST)
            replaceFrameWithFragment(LiveQuizFragment())
        } else {
            GlobalFragVM.currentFragment?.let { replaceFrameWithFragmentFromVM(it) }
        }
        setupBottomNavigation()
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
        val fragManager = supportFragmentManager
        val CurrentFrag = fragManager.findFragmentByTag("FinalResult")
        if (CurrentFrag is FinalResultFragment && CurrentFrag.isVisible) {
            if (fragManager.backStackEntryCount > 0) {
                fragManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            replaceFrameWithFragment(LiveQuizFragment())
        }
    }

    private fun replaceFrameWithFragment(frag: Fragment) {
        val fragManager = supportFragmentManager
        val fragTransaction = fragManager.beginTransaction()
        GlobalFragVM.currentFragment = frag
        fragTransaction.addToBackStack(null)
        fragTransaction.replace(R.id.FrameLayout, frag)
        fragTransaction.commit()
    }

    private fun replaceFrameWithFragmentFromVM(frag: Fragment) {
        val fragManager = supportFragmentManager
        val fragTransaction = fragManager.beginTransaction()
        fragTransaction.replace(R.id.FrameLayout, frag)
        fragTransaction.commit()
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<CustomBottomNavigation>(R.id.bottomNavigation)
        bottomNavigation.setOnTabSelectedListener { tabIndex ->
            when (tabIndex) {
                CustomBottomNavigation.TAB_PRACTICE -> replaceFrameWithFragment(CategoryFragment())
                CustomBottomNavigation.TAB_LIVE_CONTEST -> replaceFrameWithFragment(LiveQuizFragment())
                CustomBottomNavigation.TAB_SAVED_LISTS -> replaceFrameWithFragment(SaveCollectionsFragment())
                CustomBottomNavigation.TAB_PROFILE -> replaceFrameWithFragment(ProfileFragment())
            }
        }
    }

    private fun PreLoadData() {
        Thread {
            Thread.sleep(2000)
            isDataLoaded = true
        }.start()
    }
    private fun SetName(UID:String){

    }
}