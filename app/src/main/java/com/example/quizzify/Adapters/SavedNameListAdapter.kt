package com.example.quizzify.Adapters

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Dialogs.SaveListEditDialog
import com.example.quizzify.Fragments.EditQuestionFragment
import com.example.quizzify.Fragments.QuizQuestionFragment
import com.example.quizzify.Fragments.SaveCollectionsFragment
import com.example.quizzify.Fragments.SavedCollectionQuestionList
import com.example.quizzify.R
import com.example.quizzify.datamodels.SavedCollectionModel
import com.google.android.material.textfield.TextInputEditText
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import org.w3c.dom.Text

class SavedNameListAdapter(val fragment:SaveCollectionsFragment,var savedNameList:MutableList<SavedCollectionModel>):RecyclerView.Adapter<SavedNameListAdapter.ViewHolder> (){
    private var stateofexpandedItem = MutableList(savedNameList.size) { false }
    var Selected_Icon=0
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val ListName=itemView.findViewById<TextView>(R.id.listNameText)
        val isPrivateorGlobal=itemView.findViewById<ImageView>(R.id.lockIcon)
        val Author=itemView.findViewById<TextView>(R.id.authorText)
        val PracticeBtn=itemView.findViewById<Button>(R.id.practiceButton)
        val DelBtn=itemView.findViewById<ImageButton>(R.id.deleteButton)
        val viewBtn=itemView.findViewById<ImageView>(R.id.viewButton)
        val EditBtn=itemView.findViewById<ImageView>(R.id.editButton)
        val ExpandablLL=itemView.findViewById<LinearLayout>(R.id.headerLayout)
        val ExpandingCV=itemView.findViewById<LinearLayout>(R.id.expandableContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(fragment.requireContext()).inflate(R.layout.list_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d("Adapter_Debug", "Item Count: ${savedNameList.size}")
       return savedNameList.size
    }
    fun updateList(newList:List<SavedCollectionModel>){
        Log.d("List","newList"+newList.toString())
        savedNameList=newList.toMutableList()
        Log.d("List","savedNameList"+savedNameList.toString())
        stateofexpandedItem = MutableList(savedNameList.size) { false }
       notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val originalPosition=savedNameList.indexOf(savedNameList[position])
        if(stateofexpandedItem[originalPosition]){
            holder.ExpandingCV.visibility=View.VISIBLE
        }
        else{
            holder.ExpandingCV.visibility=View.GONE
        }
        val BackgroundGradientArray= listOf(
            R.drawable.sunset_vibes_gradient,R.drawable.ocean_depths_gradient,R.drawable.forest_mist_gradient,R.drawable.northern_lights_gradient,R.drawable.royal_gold_gradient)
        Log.d("Adapter_Debug", "Binding position: $position, Title: ${savedNameList[position].TitleName}")
       holder.ListName.text=savedNameList[position].TitleName
        holder.Author.text="by "+savedNameList[position].Author.take(6)+" • "+savedNameList[position].QuestionIDs.size.toString()+" Questions"
        if(savedNameList[position].isPrivate){
            holder.isPrivateorGlobal.setImageResource(R.drawable.ic_lock)
        }
        else{
            holder.isPrivateorGlobal.setImageResource(R.drawable.ic_globe)
        }
        holder.ExpandablLL.setBackgroundResource(getRandomBackgroundFromList(BackgroundGradientArray)!!)
        holder.ExpandablLL.setOnClickListener{
            if(stateofexpandedItem[originalPosition]){
                val slideUp = AnimationUtils.loadAnimation(fragment.requireContext(), R.anim.slide_up)
                slideUp.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationEnd(animation: Animation?) {
                       holder.ExpandingCV.visibility = View.GONE
                    }
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationRepeat(animation: Animation?) {}
                })
                holder.ExpandingCV.startAnimation(slideUp)
                stateofexpandedItem[originalPosition]=false
            }
            else{
                holder.ExpandingCV.visibility=View.VISIBLE
                holder.ExpandingCV.startAnimation(
                    AnimationUtils.loadAnimation(fragment.requireContext(), R.anim.slide_down)
                )
                stateofexpandedItem[originalPosition]=true
            }
        }
        holder.EditBtn.setOnClickListener {
          fragment.EditList(savedNameList,savedNameList[position],savedNameList[position].TitleName,savedNameList[position].isPrivate,savedNameList[position].QuestionIDs)
        }

        holder.DelBtn.setOnClickListener {
            fragment.DeleteList(savedNameList,savedNameList[position])
        }


        holder.PracticeBtn.setOnClickListener {
            fragment.getQuestions(savedNameList[position].QuestionIDs)
        }
        holder.viewBtn.setOnClickListener{
            val Data=Bundle()
            Data.putStringArrayList("QuestionIDs", ArrayList(savedNameList[position].QuestionIDs))
            Data.putParcelableArrayList("SavedList",ArrayList(savedNameList))
            Data.putInt("Position",originalPosition)
            fragment.ReplaceFrag(SavedCollectionQuestionList(),Data)
        }
    }

    fun getRandomBackgroundFromList(background: List<Int>): Int? {
        return if (background.isNotEmpty()) background.random() else null
    }
}