package com.example.quizzify.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Adapters.SavedCollectionQuestionListAdapter
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.ViewModelFactories.ARIndividualQuestionViewModelFactory
import com.example.quizzify.ViewModels.ARIndividualQuestionViewModel
import com.example.quizzify.datamodels.ARQuestionListModel
import com.example.quizzify.datamodels.SavedCollectionModel
import com.example.quizzify.utils.Constants
import javax.inject.Inject


class SavedCollectionQuestionList : Fragment() {

    @Inject
    lateinit var ARQuestionVMFactory: ARIndividualQuestionViewModelFactory

    lateinit var SavedCollectionAdapter:SavedCollectionQuestionListAdapter

    @Inject
    lateinit var FireStore:FireStoreInstance

    private var position: Int? = null
    private var savedNameList: ArrayList<SavedCollectionModel>? = null
    private var QuestionIDList: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_collection_question_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity().application as QuizApplication).applicationComponent.injectSavedCollectionQuestionList(this)
        val RV=view.findViewById<RecyclerView>(R.id.QuestionLVR_V)
        RV.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        SavedCollectionAdapter=SavedCollectionQuestionListAdapter(this, mutableListOf())
        RV.adapter=SavedCollectionAdapter
        val ARQuestionVM:ARIndividualQuestionViewModel by viewModels {
            ARQuestionVMFactory
        }
        QuestionIDList= arguments?.getStringArrayList("QuestionIDs")?.toList()
        savedNameList= arguments?.getParcelableArrayList("SavedList")
        position=arguments?.getInt("Position")
        if(QuestionIDList!=null) {
            ARQuestionVM.getQuestion(QuestionIDList!!)
        }
        ARQuestionVM.IndividualQuestion.observe(viewLifecycleOwner){questions->
            SavedCollectionAdapter.UpdateList(questions)
        }
    }
    fun DeleteQuiz(QuestionList:List<ARQuestionListModel>,_Position:Int){
        val updatedQuestionList =
            QuestionIDList?.toMutableList()?.apply { removeAt(_Position) } // Fix

        position?.let {
            val currentList = savedNameList?.get(it)

            if (currentList != null) {
                val updatedList =
                    updatedQuestionList?.let { it1 -> currentList.copy(QuestionIDs = it1) } // Use updated list
                if (updatedList != null) {
                    savedNameList?.set(it, updatedList)
                } // Replace old object
            }
        }

        val UID_Ref = FireStore.getFireStore().collection("UIDs").document(Constants.UID)
        val updatedSavedList = savedNameList?.map { it.toMap() }

        UID_Ref.update("SavedLists", updatedSavedList)
            .addOnSuccessListener {
                Log.d("Firestore", "SavedLists field updated successfully")
                val newList = QuestionList.toMutableList().apply { removeAt(_Position) }
                SavedCollectionAdapter.UpdateList(newList)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error updating SavedLists field", e)
            }

    }
}