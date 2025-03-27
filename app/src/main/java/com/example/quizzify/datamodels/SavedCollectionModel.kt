package com.example.quizzify.datamodels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SavedCollectionModel(
    val TitleName:String,
    val isPrivate:Boolean,
    val Author:String,
    var QuestionIDs:List<String>):Parcelable{
    fun toMap(): Map<String, Any> {
        return mapOf(
            "TitleName" to TitleName,
            "isPrivate" to isPrivate,
            "Author" to Author,
            "QuestionIDs" to QuestionIDs
        )
    }
}
