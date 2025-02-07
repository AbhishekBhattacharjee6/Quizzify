package com.example.quizzify.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Adapters.QuestionListAdapter
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.ViewModelFactories.ARIndividualQuestionViewModelFactory
import com.example.quizzify.ViewModelFactories.QuestionListViewModelFactory
import com.example.quizzify.ViewModels.ARIndividualQuestionViewModel
import com.example.quizzify.ViewModels.QuestionListViewModel
import com.example.quizzify.datamodels.ARQuestionListModel
import com.google.firebase.firestore.FieldValue
import javax.inject.Inject


class ActiveRoomQuestionList : Fragment() {

    @Inject
    lateinit var QuestionListVMFactory:QuestionListViewModelFactory

    @Inject
    lateinit var ARQuestionVMFactory:ARIndividualQuestionViewModelFactory

    @Inject
    lateinit var FireStore:FireStoreInstance

    lateinit var QLAdapter:QuestionListAdapter

    lateinit var QuizSetId:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_active_room_question_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val QLRV=view.findViewById<RecyclerView>(R.id.QuestionListRV)
        QuizSetId= arguments?.getString("QuizSetId").toString()
        QLRV.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        val emptyQuestionList= mutableListOf<ARQuestionListModel>()
        QLAdapter=QuestionListAdapter(this, emptyQuestionList)
        (requireActivity().application as QuizApplication).applicationComponent.injectQuestionList(this)
        val QuestionListVM:QuestionListViewModel by viewModels {
            QuestionListVMFactory
        }
        val ARQuestionViewModel:ARIndividualQuestionViewModel by viewModels {
            ARQuestionVMFactory
        }
        QuestionListVM.getQuestionList(QuizSetId.toString())
        QLRV.adapter=QLAdapter
      QuestionListVM.questionList.observe(viewLifecycleOwner){
        if(it.isNotEmpty()){
            ARQuestionViewModel.getQuestion(it)
            ARQuestionViewModel.IndividualQuestion.observe(viewLifecycleOwner){
                Log.d("ARQuestionList",it.toString())
                QLAdapter.UpdateList(it)
            }
        }
    }
    }
    fun ReplaceFragment(frag:Fragment,Data:Bundle){
        frag.arguments=Data
        val fragManager=parentFragmentManager
        val fragTransaction=fragManager.beginTransaction()
        fragTransaction.replace(R.id.FrameLayout,frag)
        fragTransaction.commit()
    }
    fun DeleteQuiz(QuizId:String){
        val IndividualQuiz_Ref=FireStore.getFireStore().collection("LiveQuestions")
        val IndiQuizDoc_Ref=IndividualQuiz_Ref.document(QuizId)
        IndiQuizDoc_Ref.delete().addOnSuccessListener {
            val QuizSet_Ref=FireStore.getFireStore().collection("Quizset").document(QuizSetId)
            QuizSet_Ref.update("QuestionIds",FieldValue.arrayRemove(QuizId)).addOnSuccessListener {
                Toast.makeText(requireContext(),"Question Deleted",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(requireContext(),"Question Deletion Failed",Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(),"Question Deletion Failed",Toast.LENGTH_SHORT).show()
        }
    }
}