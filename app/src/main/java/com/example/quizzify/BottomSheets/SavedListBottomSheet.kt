package com.example.quizzify.BottomSheets

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Adapters.ListShowAdapter
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.ViewModelFactories.ListShowViewModelFactories
import com.example.quizzify.ViewModels.ListShowViewModel
import com.example.quizzify.datamodels.SavedCollectionModel
import com.example.quizzify.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import nl.dionsegijn.konfetti.core.Position
import javax.inject.Inject

class SavedListBottomSheet: BottomSheetDialogFragment() {
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    @Inject
    lateinit var ListShowVMFactory: ListShowViewModelFactories

    @Inject
    lateinit var FireStore: FireStoreInstance

    lateinit var ListShowAdapter:ListShowAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as QuizApplication).applicationComponent.injectSavedListBottomSheet(this)
       // setStyle(STYLE_NORMAL,R.style.BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_saved_lists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val QuestionID= arguments?.getString("QuestionID")
        val QuestionPos= arguments?.getInt("Pos")
        val RV=view.findViewById<RecyclerView>(R.id.savedListsRecyclerView)
        val closeBtn=view.findViewById<ImageButton>(R.id.closeButton)
        val bottomSheet=view.findViewById<NestedScrollView>(R.id.bottomSheet)
       // val parentView = view.parent as? View ?: return
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable=false
        bottomSheetBehavior.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
        }
        val windowHeight = resources.displayMetrics.heightPixels
        //view.layoutParams.height = (windowHeight * 0.75).toInt()
        bottomSheet.layoutParams.height = (windowHeight * 0.75).toInt()
        ListShowAdapter=ListShowAdapter(this, mutableListOf(),QuestionID!!,QuestionPos!!)
        val ListShowVM: ListShowViewModel by viewModels {
            ListShowVMFactory
        }
        ListShowVM.getSavedLists()
        RV.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            RV.adapter = ListShowAdapter
            addItemDecoration(
                MarginItemDecoration(
                    resources.getDimensionPixelSize(R.dimen.list_item_spacing)
                )
            )
        }
        ListShowVM.SavedNameListSet.observe(viewLifecycleOwner){
            Log.d("SavedList","$it")
            ListShowAdapter.updateList(it)
        }
        closeBtn.setOnClickListener {
            dismiss()
        }
    }
    companion object{
        fun newInstance(QuestionID:String,Pos:Int):SavedListBottomSheet{
            val args=Bundle()
            args.putString("QuestionID",QuestionID)
            args.putInt("Pos",Pos)
            val fragment=SavedListBottomSheet()
            fragment.arguments=args
            return fragment
        }
    }
    fun AddQuestion(savedNameList:MutableList<SavedCollectionModel>, QuestionID:String, Item:SavedCollectionModel){
        val Position=savedNameList.indexOf(Item)
        val newList=savedNameList
        val currentModule=newList[Position]
        currentModule.QuestionIDs = currentModule.QuestionIDs.toMutableList().apply {
            add(QuestionID)
        }
        newList[Position] = currentModule
        val updatedSavedList = newList.map { it.toMap() }
        val UID_Ref=FireStore.getFireStore().collection("UIDs").document(Constants.UID)
        UID_Ref.update("SavedLists", updatedSavedList)
            .addOnSuccessListener {
                Toast.makeText(requireContext(),"Question Added Succesfully", Toast.LENGTH_SHORT).show()
                Log.d("Firestore", "savedlist field updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error updating savedlist field", e)
            }
    }
}