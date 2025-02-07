package com.example.quizzify.datamodels

data class QuizSetModel(val Duration:String,val PassCode:String,val QuizSetId:String,val RoomName:String,val StartFrom:String,val CreatorUID:String,val ValidTill:String){
    fun toMap():Map<String,String>{
        return mapOf(
            "Duration" to Duration,
            "PassCode" to PassCode,
            "QuizSetId" to QuizSetId,
            "RoomName" to RoomName,
            "StartFrom" to StartFrom,
            "CreatorUID" to CreatorUID,
            "ValidTill" to ValidTill
        )
    }
    companion object {
        fun fromMap(map: Map<String, Any>): QuizSetModel {
            return QuizSetModel(
                Duration = map["Duration"] as String,
                PassCode = map["PassCode"] as String,
                QuizSetId = map["QuizSetId"] as String,
                RoomName = map["RoomName"] as String,
                StartFrom = map["StartFrom"] as String,
                CreatorUID = map["CreatorUID"] as String,
                ValidTill = map["ValidTill"] as String
            )
        }
    }
}
