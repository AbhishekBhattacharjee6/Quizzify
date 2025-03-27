package com.example.quizzify.datamodels

data class DaysActiveDataModel(val Day:String, var QuestionsSolved:Int){
    fun toMap():HashMap<String,Any>{
        return hashMapOf(
            "Day" to Day,
            "QuestionsSolved" to QuestionsSolved
        )
    }
}
