package com.example.quizzify.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Adapters.QuizAdapter
import com.example.quizzify.Adapters.SavedNameListAdapter
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.ViewModelFactories.QuizViewModelFactory
import com.example.quizzify.ViewModelFactories.SavedListNamesVMFactory
import com.example.quizzify.ViewModels.QuizViewModel
import com.example.quizzify.ViewModels.SavedListNamesViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import javax.inject.Inject


class QuizQuestionFragment : Fragment() {


    lateinit var QuizAdapter:QuizAdapter
    lateinit var SLAdapter:SavedNameListAdapter

    private lateinit var QuizRV:RecyclerView
    lateinit var dialog: BottomSheetDialog

    @Inject
    lateinit var QuizVMFactory:QuizViewModelFactory

    @Inject
   lateinit var SLNVMFactory:SavedListNamesVMFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val CategoryNum= arguments?.getInt("CategoryNum")!!.toInt()
        val QuestionNum= arguments?.getInt("QuestionNum")!!.toInt()
        val Difficulty= arguments?.getString("Difficulty").toString()
        QuizRV=view.findViewById<RecyclerView>(R.id.QuizRV)
        QuizAdapter= QuizAdapter(this, emptyList())
        QuizRV.layoutManager=object:LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false){
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        QuizRV.adapter=QuizAdapter
        (requireActivity().application as QuizApplication).applicationComponent.injectQuiz(this)
        val QuizVM:QuizViewModel by viewModels {
            QuizVMFactory
        }
        QuizVM.getParams(QuestionNum,CategoryNum,Difficulty)
        QuizVM.questionset.observe(viewLifecycleOwner){
            QuizAdapter.updateList(it)
        }
    }
    fun movetonextItem(currentItem:Int,totalItems:Int){
        if(currentItem<totalItems-1){
            QuizRV.scrollToPosition(currentItem+1)
        }
        else{
            Toast.makeText(requireContext(),"No questions left",Toast.LENGTH_SHORT).show()
        }
    }
  fun movetoprevItem(currentItem: Int){
      if(currentItem!=0){
          QuizRV.scrollToPosition(currentItem-1)
      }
      else{
          Toast.makeText(requireContext(),"No questions left",Toast.LENGTH_SHORT).show()
      }
  }
    fun ReplaceFragment(frag:Fragment,Data:Bundle){
        frag.arguments=Data
        val fragManager=parentFragmentManager
        val fragTransaction=fragManager.beginTransaction()
        fragTransaction.replace(R.id.FrameLayout,frag,"FinalResult")
        fragTransaction.commit()
    }
     fun saveListDialog(){
        val dialog_view=layoutInflater.inflate(R.layout.saved_list_ui,null)
        dialog=BottomSheetDialog(requireContext(),R.style.BottomSheetDialogTheme)
        dialog.setContentView(dialog_view)
        val SLRV=dialog_view.findViewById<RecyclerView>(R.id.slrv)
        SLRV.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        SLAdapter= SavedNameListAdapter(this, emptyList())
        SLRV.adapter=SLAdapter
        val SLVM:SavedListNamesViewModel by viewModels {
            SLNVMFactory
        }
        SLVM.SavedNameListSet.observe(viewLifecycleOwner){
            SLAdapter.updateList(it)
        }
        dialog.show()
    }
}