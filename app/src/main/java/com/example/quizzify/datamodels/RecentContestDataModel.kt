package com.example.quizzify.datamodels

data class RecentContestDataModel(val RoomID:String,val RoomName:String,val Total:Int,val Correct:Int,val Wrong:Int,val Validity:String){
    fun toMap(): HashMap<String, Any> {
        return hashMapOf(
            "roomID" to RoomID,
            "roomName" to RoomName,
            "totalQuestions" to Total,
            "correctAnswers" to Correct,
            "wrongAnswers" to Wrong,
            "validTill" to Validity
        )
    }
}
