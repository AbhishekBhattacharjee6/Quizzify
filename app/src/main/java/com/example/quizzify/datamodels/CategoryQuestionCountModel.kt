package com.example.quizzify.datamodels

data class CategoryQuestionCountModel(val id:Int,val total:Int,val easy:Int,val medium:Int,val hard:Int,var expanded:Boolean)
