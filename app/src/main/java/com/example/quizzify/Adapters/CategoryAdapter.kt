package com.example.quizzify.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.R
import com.example.quizzify.datamodels.TriviaCategory

class CategoryAdapter(val fragment:Fragment, var categoryList:List<TriviaCategory>): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    private lateinit var itemListener:itemClickListener
    interface itemClickListener{
        fun onItemClick(position: Int,CategoryNum:Int)
    }
    fun setOnItemClickListener(listener: itemClickListener){
        itemListener=listener
    }
    class CategoryViewHolder(itemView: View,private val itemlistener: itemClickListener): RecyclerView.ViewHolder(itemView){
        val categoryName=itemView.findViewById<TextView>(R.id.categoryName)
            fun bind(CategoryNum:Int){
                itemView.setOnClickListener{
                    itemlistener.onItemClick(adapterPosition,CategoryNum)
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view= LayoutInflater.from(fragment.requireContext()).inflate(R.layout.category_item,parent,false)
        return CategoryViewHolder(view,itemListener)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    fun updateList(newList:List<TriviaCategory>){
        this.categoryList=newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val text=categoryList[position].name
        val num=categoryList[position].id
        holder.categoryName.text=text
        holder.bind(num);
    }

}