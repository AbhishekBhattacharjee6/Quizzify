package com.example.quizzify.Fragments

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.quizzify.Adapters.SavedNameListAdapter
import com.example.quizzify.Dialogs.NewSaveListDialog
import com.example.quizzify.Dialogs.QuestionLoaderDialog
import com.example.quizzify.Dialogs.SaveListEditDialog
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.ViewModelFactories.ARIndividualQuestionViewModelFactory
import com.example.quizzify.ViewModelFactories.IndividualQuestionViewModelFactory
import com.example.quizzify.ViewModelFactories.SavedListNamesVMFactory
import com.example.quizzify.ViewModels.GlobalFragViewModel
import com.example.quizzify.ViewModels.IndividualQuestionViewModel
import com.example.quizzify.ViewModels.SavedListNamesViewModel
import com.example.quizzify.datamodels.QuestionModel
import com.example.quizzify.datamodels.SavedCollectionModel
import com.example.quizzify.utils.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FieldValue
import javax.inject.Inject


class SaveCollectionsFragment : Fragment() {



    var SelectedIcon:Int=0

    @Inject
    lateinit var FireStore:FireStoreInstance

    @Inject
    lateinit var SavedListNameVMFactory:SavedListNamesVMFactory

    lateinit var SavedListNameAdapter:SavedNameListAdapter

    private lateinit var GlobalFragVM: GlobalFragViewModel

    @Inject
    lateinit var IndividualQuestionVMFactory: IndividualQuestionViewModelFactory

    private val SavedListNameVM:SavedListNamesViewModel by viewModels {
        SavedListNameVMFactory
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_save_collections, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity().application as QuizApplication).applicationComponent.injectSaveCollectionsFragment(this)
        SelectedIcon=0
        val FAB=view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val RV=view.findViewById<RecyclerView>(R.id.SaveListRV)
        RV.layoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        GlobalFragVM= ViewModelProvider(this)[GlobalFragViewModel::class.java]

        SavedListNameAdapter=SavedNameListAdapter(this, mutableListOf())
        RV.adapter=SavedListNameAdapter

        SavedListNameVM.getSavedLists()
        SavedListNameVM.SavedNameListSet.observe(viewLifecycleOwner){
            Log.d("SavedList",it.toString())
            SavedListNameAdapter.updateList(it)
            SavedListNameAdapter.notifyDataSetChanged()
        }
        FAB.setOnClickListener {

            val dialog = NewSaveListDialog()
            dialog.show(parentFragmentManager, "NewListDialog")
        }
        setFragmentResultListener("dialogDismissed") { _, _ ->
            RefreshRecyclerView()
        }
        setFragmentResultListener("dialogDismissed2") { _, _ ->
            RefreshRecyclerView()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragmentManager = requireActivity().supportFragmentManager
                if (fragmentManager.backStackEntryCount == 0) {
                    requireActivity().finishAffinity() // 🔥 Ensures complete exit
                } else {
                    fragmentManager.popBackStack() // ✅ Properly handles back navigation
                }
            }
        })
    }

    fun EditList(QuizList:List<SavedCollectionModel>,Item: SavedCollectionModel,TitleName:String,isPrivate:Boolean,QuestionList:List<String>){
        val Index=QuizList.indexOf(Item)
        val dialog= SaveListEditDialog.newInstance(TitleName,isPrivate,QuizList,Index)
        dialog.show(parentFragmentManager,"SaveListEditDialog")
    }
    fun DeleteList(QuizList:List<SavedCollectionModel>,Item:SavedCollectionModel){
        val Index=QuizList.indexOf(Item)
        var NewList=QuizList.toMutableList()
        NewList.removeAt(Index)
        val UID_Ref=FireStore.getFireStore().collection("UIDs").document(Constants.UID)
        val updatedSavedList = NewList.map { it.toMap() }
        UID_Ref.update("SavedLists", updatedSavedList)
            .addOnSuccessListener {
                Log.d("Firestore", "savedlist field updated successfully")
                //SavedListNameAdapter.updateList(NewList)
                RefreshRecyclerView()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error updating savedlist field", e)
            }
    }
    fun ReplaceFrag(Frag:Fragment,Data:Bundle){
        Frag.arguments=Data
        val fragManager=parentFragmentManager
        val fragTransaction=fragManager.beginTransaction()
        fragTransaction.addToBackStack(null)
        GlobalFragVM.currentFragment=Frag
        fragTransaction.replace(R.id.FrameLayout,Frag)
        fragTransaction.commit()
    }

    override fun onResume() {
        super.onResume()
        SavedListNameVM.getSavedLists()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragmentManager = requireActivity().supportFragmentManager
                if (fragmentManager.backStackEntryCount == 0) {
                    requireActivity().finishAffinity() // 🔥 Ensures complete exit
                } else {
                    fragmentManager.popBackStack() // ✅ Properly handles back navigation
                }
            }
        })
    }
    fun getQuestions(QuestionIDList:List<String>){
        var Questions = mutableListOf<QuestionModel>()
        val loaderDialog = QuestionLoaderDialog()
        loaderDialog.show(childFragmentManager, "QuestionLoaderDialog")

        // Track the start time for a minimum 3-second delay
        val startTime = System.currentTimeMillis()
        val IndividualQuestionVM: IndividualQuestionViewModel by viewModels {
            IndividualQuestionVMFactory
        }
        IndividualQuestionVM.getQuestions(QuestionIDList)
        IndividualQuestionVM.questions.observe(viewLifecycleOwner) { questionsList ->
            if (questionsList.size == QuestionIDList.size) {
                Questions.addAll(questionsList)//
                Log.d("AllQuestions", Questions.toString())// // Wait until all questions are fetched
                val Data = Bundle()
                Data.putParcelableArrayList(
                    "QuestionList",
                    ArrayList(Questions)
                )
                val elapsedTime = System.currentTimeMillis() - startTime
                val remainingTime = 3000 - elapsedTime
                Handler(Looper.getMainLooper()).postDelayed({
                    loaderDialog.dismissLoader()
                    EmptyUpandReplace(SavedListPractice(),Data)
                }, maxOf(remainingTime, 0))

            }
        }
    }
    fun RefreshRecyclerView(){
       SavedListNameVM.getSavedLists()
    }
    fun EmptyUpandReplace(frag:Fragment,data:Bundle){
        frag.arguments=data
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        val fragments = requireActivity().supportFragmentManager.fragments
        for (fragment in fragments) {
            transaction.remove(fragment)
        }

        requireActivity().supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        GlobalFragVM.currentFragment=frag

        transaction.replace(R.id.FrameLayout, frag)

        transaction.commitNow()
    }
}