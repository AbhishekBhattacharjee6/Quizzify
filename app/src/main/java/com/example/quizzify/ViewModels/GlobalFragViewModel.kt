package com.example.quizzify.ViewModels

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel

class GlobalFragViewModel:ViewModel() {
    var currentFragment: Fragment? = null
}