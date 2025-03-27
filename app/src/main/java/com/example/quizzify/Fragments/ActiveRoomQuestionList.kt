package com.example.quizzify.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Adapters.QuestionListAdapter
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.ViewModelFactories.ARIndividualQuestionViewModelFactory
import com.example.quizzify.ViewModelFactories.QuestionListViewModelFactory
import com.example.quizzify.ViewModels.ARIndividualQuestionViewModel
import com.example.quizzify.ViewModels.GlobalFragViewModel
import com.example.quizzify.ViewModels.QuestionListViewModel
import com.example.quizzify.datamodels.ARQuestionListModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FieldValue
import javax.inject.Inject


class ActiveRoomQuestionList : Fragment() {

    @Inject
    lateinit var QuestionListVMFactory:QuestionListViewModelFactory

    @Inject
    lateinit var ARQuestionVMFactory:ARIndividualQuestionViewModelFactory

    private lateinit var GlobalFragVM: GlobalFragViewModel


    @Inject
    lateinit var FireStore:FireStoreInstance

    lateinit var QLAdapter:QuestionListAdapter

    lateinit var QuizSetId:String

    val QuestionListVM:QuestionListViewModel by viewModels {
        QuestionListVMFactory
    }

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
        val AddQuestion=view.findViewById<FloatingActionButton>(R.id.addQuestion)
        QuizSetId= arguments?.getString("QuizSetId").toString()
        QLRV.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        val emptyQuestionList= mutableListOf<ARQuestionListModel>()
        QLAdapter=QuestionListAdapter(this, emptyQuestionList)
        (requireActivity().application as QuizApplication).applicationComponent.injectQuestionList(this)
        GlobalFragVM= ViewModelProvider(this)[GlobalFragViewModel::class.java]
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
        AddQuestion.setOnClickListener{
            val data=Bundle()
            data.putString("QuizSetID",QuizSetId)
            ReplaceFragfromAdapter(AddNewQuestion(),data)
        }
    }

    fun ReplaceFragfromAdapter(frag:Fragment,Data:Bundle){
        frag.arguments = Data
        val fragManager = parentFragmentManager
        GlobalFragVM.currentFragment=frag
        val fragTransaction = fragManager.beginTransaction()
        fragTransaction.replace(R.id.FrameLayout, frag)
        fragTransaction.addToBackStack(null)
        fragTransaction.commit()
    }
    fun DeleteQuiz(QuizId:String,QuestionList:MutableList<ARQuestionListModel>,Position:Int){
        val IndividualQuiz_Ref=FireStore.getFireStore().collection("LiveQuestions")
        val IndiQuizDoc_Ref=IndividualQuiz_Ref.document(QuizId)
        IndiQuizDoc_Ref.delete().addOnSuccessListener {
            val QuizSet_Ref=FireStore.getFireStore().collection("Quizset").document(QuizSetId)
            QuizSet_Ref.update("QuestionIds",FieldValue.arrayRemove(QuizId)).addOnSuccessListener {
                Toast.makeText(requireContext(),"Question Deleted",Toast.LENGTH_SHORT).show()
                if (Position in QuestionList.indices) {
                    QuestionList.removeAt(Position)
                    //QLAdapter.UpdateList(QuestionList) // Ensure RecyclerView updates
                    RefreshRecycleView()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(),"Question Deletion Failed",Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(),"Question Deletion Failed",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        QuestionListVM.getQuestionList(QuizSetId.toString())
    }
    private fun RefreshRecycleView(){
        QuestionListVM.getQuestionList(QuizSetId.toString())
    }
}