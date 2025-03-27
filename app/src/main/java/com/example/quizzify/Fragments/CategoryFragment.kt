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
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Adapters.CategoryAdapter
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.ViewModelFactories.CategoryViewModelFactory
import com.example.quizzify.ViewModels.CategoryViewModel
import com.example.quizzify.ViewModels.GlobalFragViewModel
import com.example.quizzify.datamodels.CategoryQuestionCountModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import it.sephiroth.android.library.numberpicker.NumberPicker
import it.sephiroth.android.library.numberpicker.doOnProgressChanged
import javax.inject.Inject


class CategoryFragment : Fragment() {
    lateinit var categoryAdapter: CategoryAdapter
    lateinit var dialog:BottomSheetDialog

    private lateinit var GlobalFragVM: GlobalFragViewModel


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

        GlobalFragVM= ViewModelProvider(this)[GlobalFragViewModel::class.java]

        val categoryViewModel:CategoryViewModel by viewModels{
            CategoryViewModelFactory
        }
        val CategoryQuestionCount= listOf(CategoryQuestionCountModel(9,340,145,133,62,false),
            CategoryQuestionCountModel(10,106,33,45,28,false),
            CategoryQuestionCountModel(11,265,94,126,45,false),
            CategoryQuestionCountModel(12,388,113,202,73,false),
            CategoryQuestionCountModel(13,34,9,14,11,false),
            CategoryQuestionCountModel(14,181,71,80,30,false),
            CategoryQuestionCountModel(15,1030,351,474,205,false),
            CategoryQuestionCountModel(16,64,21,18,25,false),
            CategoryQuestionCountModel(17,242,66,106,71,false),
            CategoryQuestionCountModel(18,165,53,75,37,false),
            CategoryQuestionCountModel(19,56,14,24,18,false),
            CategoryQuestionCountModel(20,64,22,28,14,false),
            CategoryQuestionCountModel(21,138,51,66,21,false),
            CategoryQuestionCountModel(22,280,82,142,56,false),
            CategoryQuestionCountModel(23,332,76,172,84,false),
            CategoryQuestionCountModel(24,63,19,28,16,false),
            CategoryQuestionCountModel(25,36,15,12,9,false),
            CategoryQuestionCountModel(26,53,13,32,8,false),
            CategoryQuestionCountModel(27,81,29,34,18,false),
            CategoryQuestionCountModel(28,72,21,33,18,false),
            CategoryQuestionCountModel(29,70,16,35,19,false),
            CategoryQuestionCountModel(30,31,15,10,6,false),
            CategoryQuestionCountModel(31,188,59,82,47,false),
            CategoryQuestionCountModel(32,95,34,44,17,false)
            )
        val CategoryImg= listOf(R.drawable.ic_gk,R.drawable.ic_book,R.drawable.ic_entertainment,R.drawable.ic_music,R.drawable.ic_guitar,R.drawable.ic_television,R.drawable.ic_videogame,R.drawable.ic_boardgames,R.drawable.ic_science,R.drawable.ic_computer,R.drawable.ic_maths,R.drawable.ic_mythology,R.drawable.ic_sports,R.drawable.ic_geography,R.drawable.ic_history,R.drawable.ic_politics,R.drawable.ic_art,R.drawable.ic_celebrities,R.drawable.ic_animal,R.drawable.ic_car,R.drawable.ic_comics,R.drawable.ic_gadgets,R.drawable.ic_anime,R.drawable.ic_cartoon)
        categoryAdapter=CategoryAdapter(this, emptyList(),CategoryQuestionCount,CategoryImg)
        recyclerview.layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        recyclerview.adapter=categoryAdapter
        categoryAdapter.setOnItemClickListener(object:CategoryAdapter.itemClickListener{
            override fun onItemClick(position: Int,CategoryNum:Int) {
            }

        })
        (requireActivity().application as QuizApplication).applicationComponent.injectCategory(this)
        categoryViewModel.categoryList.observe(viewLifecycleOwner) {
            categoryAdapter.updateList(it)
        }

    }
     fun replaceFrameWithFragment(frag:Fragment,data:Bundle) {
        frag.arguments=data
        val fragManager=parentFragmentManager
        val fragTransaction=fragManager.beginTransaction()
        fragTransaction.replace(R.id.FrameLayout,frag)
        fragTransaction.commit()
    }
    fun EmptyUpandReplace(frag:Fragment,data:Bundle){
        frag.arguments=data
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        GlobalFragVM.currentFragment=frag

        val fragments = requireActivity().supportFragmentManager.fragments
        for (fragment in fragments) {
            transaction.remove(fragment)
        }

        requireActivity().supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        transaction.replace(R.id.FrameLayout, frag)

        transaction.commitNow()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (requireActivity().supportFragmentManager.backStackEntryCount == 0) {
                    requireActivity().finish() // Exit the app only when no fragments are left
                } else {
                    isEnabled = false
                    requireActivity().onBackPressed() // Continue normal back press behavior
                }
            }
        })
    }
}