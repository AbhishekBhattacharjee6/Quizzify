package com.example.quizzify.datamodels

data class FullQuizSetModel(val Duration:String,val PassCode:String,val QuestionIds:List<String>,val QuizSetUid:String,val RoomName:String,val SaveAllowed:Boolean,val StartTime:String,val UserUid:String,val ValidTill:String){
  fun toMap():Map<String,Any> {
      return mapOf(
          "Duration" to Duration,
          "PassCode" to PassCode,
          "QuestionIds" to QuestionIds,
          "QuizSetUid" to QuizSetUid,
          "RoomName" to RoomName,
          "SaveAllowed" to SaveAllowed,
          "StartTime" to StartTime,
          "UserUid" to UserUid,
          "ValidTill" to ValidTill
      )
  }
}
