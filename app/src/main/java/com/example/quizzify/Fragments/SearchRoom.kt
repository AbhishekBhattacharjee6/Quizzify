package com.example.quizzify.Fragments

import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Adapters.SearchRoomAdapter
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.ViewModelFactories.SearchRoomViewModelFactory
import com.example.quizzify.ViewModels.SearchRoomViewModel
import com.example.quizzify.datamodels.QuizSetModel
import com.example.quizzify.utils.Constants
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FieldValue
import org.w3c.dom.Text
import javax.inject.Inject


class SearchRoom : Fragment() {

    @Inject
    lateinit var SearchRoomVMFactory:SearchRoomViewModelFactory

    lateinit var SearchRoomAdapter:SearchRoomAdapter

    @Inject
    lateinit var FireStore:FireStoreInstance



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val RoomtoSearch= arguments?.getString("RoomtoSearch")
        Log.i("RoomtoSearch",RoomtoSearch.toString())
        val SearchRV=view.findViewById<RecyclerView>(R.id.SearchRoomRV)
        (requireActivity().application as QuizApplication).applicationComponent.injectSearchRoom(this)
        val SearchRoomVM:SearchRoomViewModel by viewModels{
            SearchRoomVMFactory
        }
        SearchRoomVM.getSearchResults(RoomtoSearch.toString())
        SearchRV.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        SearchRoomAdapter=SearchRoomAdapter(this, emptyList())
        SearchRV.adapter=SearchRoomAdapter
        SearchRoomVM.SearchResult.observe(viewLifecycleOwner){
            SearchRoomAdapter.updateList(it)
        }
        SearchRoomAdapter.setOnItemClickListener(object :SearchRoomAdapter.itemClickListener{
            override fun onItemClick(
                position: Int,
                RoomName: String,
                RoomCreatorName: String,
                PassCode: String,
                StartFrom: String,
                ValidTill: String,
                RoomId: String,
                Duration:String
            ) {
                showPreRegDialog(RoomName,RoomCreatorName,PassCode,StartFrom,ValidTill,RoomId,Duration)
            }


        })
    }
    fun showPreRegDialog(RoomName: String,
                         RoomCreatorName: String,
                         PassCode: String,
                         StartFrom: String,
                         ValidTill: String,
                         RoomId: String,
                         Duration:String
                         ){

        val UID_Ref=FireStore.getFireStore().collection("UIDs").document(Constants.UID)
        val dialog=Dialog(requireContext())
        dialog.setContentView(R.layout.room_pre_reg_dialog)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)
        val roomName=dialog.findViewById<TextView>(R.id.textView9)
        val roomCreatorName=dialog.findViewById<TextView>(R.id.textView10)
        val passCode=dialog.findViewById<TextInputEditText>(R.id.PassCode)
        val startFrom=dialog.findViewById<TextView>(R.id.textView13)
        val validTill=dialog.findViewById<TextView>(R.id.textView15)
        val duraTion=dialog.findViewById<TextView>(R.id.textView17)
        val VerifyButton=dialog.findViewById<Button>(R.id.Verifybutton)
        roomName.text=RoomName
        roomCreatorName.text=RoomCreatorName
        startFrom.text=StartFrom
        validTill.text=ValidTill
        duraTion.text=Duration
        val calendar=Calendar.getInstance()
        val year=calendar.get(Calendar.YEAR)
        val month=calendar.get(Calendar.MONTH)+1
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        val date=year.toString()+month.toString()+day.toString()
        Log.i("CurrentDate",date.toString())
        if(date.toInt()<StartFrom.toInt()) {
            VerifyButton.text="PreRegister"
            VerifyButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.neon_green))
            VerifyButton.setOnClickListener {
                val Pass = passCode.text.toString()
                if (Pass.isEmpty()) {
                    Toast.makeText(requireContext(), "Enter a Valid PassCode", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (Pass != PassCode) {
                        Toast.makeText(requireContext(), "Wrong PassCode", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        val newPreRegRoom=QuizSetModel(Duration,PassCode,RoomId,RoomName,StartFrom,RoomCreatorName,ValidTill)
                        val preRegRoom=newPreRegRoom.toMap()
                        UID_Ref.update("PreRegisteredRooms", FieldValue.arrayUnion(preRegRoom))
                            .addOnSuccessListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Successfully Registered",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }.addOnFailureListener {
                            Toast.makeText(
                                requireContext(),
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
        else{
            VerifyButton.text="PreRegister Date Expired"
            VerifyButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.neon_grey))
        }
        dialog.show()
    }

}