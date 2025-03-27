package com.example.quizzify.datamodels

data class RoomSetModel(  val StartFrom: String = "",
                          val ValidTill: String = "",
                          val Duration: String = "",
                          val UserUid: String = "",
                          val QuizSetUid: String = "",
                          val Passcode: String = "",
                          val RoomName: String = "",
                          val SaveAllowed: Boolean = true){

}
