package com.example.quizzify.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Adapters.CategoryAdapter
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.ViewModelFactories.CategoryViewModelFactory
import com.example.quizzify.ViewModels.CategoryViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import it.sephiroth.android.library.numberpicker.NumberPicker
import it.sephiroth.android.library.numberpicker.doOnProgressChanged
import javax.inject.Inject


class CategoryFragment : Fragment() {
    lateinit var categoryAdapter: CategoryAdapter
    lateinit var dialog:BottomSheetDialog

    @Inject
    lateinit var CategoryViewModelFactory:CategoryViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerview=view.findViewById<RecyclerView>(R.id.categoryRv)
        val categoryViewModel:CategoryViewModel by viewModels{
            CategoryViewModelFactory
        }
        categoryAdapter=CategoryAdapter(this, emptyList())
        recyclerview.layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        recyclerview.adapter=categoryAdapter
        categoryAdapter.setOnItemClickListener(object:CategoryAdapter.itemClickListener{
            override fun onItemClick(position: Int,CategoryNum:Int) {
                showBottomSheet(CategoryNum)
            }

        })
        (requireActivity().application as QuizApplication).applicationComponent.injectCategory(this)
        categoryViewModel.categoryList.observe(viewLifecycleOwner) {
            categoryAdapter.updateList(it)
        }

    }
    fun showBottomSheet(CategoryNum:Int){
        val dialogView=layoutInflater.inflate(R.layout.bottom_sheeth,null)
        dialog=BottomSheetDialog(requireContext(),R.style.BottomSheetDialogTheme)
        dialog.setContentView(dialogView)
        val spinner=dialogView.findViewById<Spinner>(R.id.spinner)
        val numberPicker=dialogView.findViewById<NumberPicker>(R.id.numberPicker)
        val startBtn=dialogView.findViewById<Button>(R.id.practicestart)
        var selectedNum:Int=50
        numberPicker.doOnProgressChanged { numberPicker, progress, formUser ->
            selectedNum=progress
            Log.d("Number","Selected Number is $selectedNum")
        }
        val items=arrayOf<String>("easy","medium","hard")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        var selectedDifficulty:String="Beginner"
        spinner.onItemSelectedListener = object : AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedDifficulty=items[p2]
                Log.d("Difficulty","selected Difficulty is $selectedDifficulty")

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Toast.makeText(requireContext(),"Please select a difficulty", Toast.LENGTH_SHORT).show()
            }

        }
        startBtn.setOnClickListener {
            val data=Bundle()
            data.putInt("CategoryNum",CategoryNum)
            data.putInt("QuestionNum",selectedNum)
            data.putString("Difficulty",selectedDifficulty)
            replaceFrameWithFragment(QuizQuestionFragment(),data)
            dialog.cancel()
        }
        dialog.show()
    }
    private fun replaceFrameWithFragment(frag:Fragment,data:Bundle) {
        frag.arguments=data
        val fragManager=parentFragmentManager
        val fragTransaction=fragManager.beginTransaction()
        fragTransaction.replace(R.id.FrameLayout,frag)
        fragTransaction.commit()
    }
}