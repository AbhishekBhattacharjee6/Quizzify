package com.example.quizzify.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.replace
import com.example.quizzify.R

// TODO: Rename parameter arguments, choose names that match

class LiveQuizFragment : Fragment() {


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
        val CreateRoom=view.findViewById<Button>(R.id.button6)
        val JoinRoom=view.findViewById<Button>(R.id.button7)
        CreateRoom.setOnClickListener {
            ReplaceFrag(ActiveRooms())
        }
        JoinRoom.setOnClickListener {
            ReplaceFrag(PreRegisteredRooms())
        }
    }
    fun ReplaceFrag(Frag:Fragment){
        val fragManager=parentFragmentManager
        val fragTransaction=fragManager.beginTransaction()
        fragTransaction.replace(R.id.FrameLayout,Frag)
        fragTransaction.commit()
    }
}