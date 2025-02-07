package com.example.quizzify.Firestore

import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FireStoreInstance @Inject constructor(private val FireBaseFireStore:FirebaseFirestore) {
    fun getFireStore():FirebaseFirestore{
        return FireBaseFireStore
    }
}