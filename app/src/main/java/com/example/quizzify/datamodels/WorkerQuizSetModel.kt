package com.example.quizzify.datamodels

data class WorkerQuizSetModel(val StartFrom:String,val ValidTill:String,val UID:String){
    fun toMap():Map<String,Any>{
        return mapOf(
            "StartFrom" to StartFrom,
            "ValidTill" to ValidTill,
            "UID" to UID
        )
    }
}
