package com.example.quizzify.datamodels

data class LeaderBoardModel(val UID:String,val Name:String,val Score:Int,val TotalScore:Int,val ImgUrl:String){
    fun toMap():HashMap<String,Any>{
        return hashMapOf(
            "UID" to UID,
            "Name" to Name,
            "Score" to Score,
            "TotalScore" to TotalScore,
            "ImgUrl" to ImgUrl
        )
    }
}
