package com.example.quizzify.datamodels

import android.os.Parcel
import android.os.Parcelable

data class QuestionModel(
    val Question: String,
    val CorrectAnswer: String,
    val WrongAnswer1: String,
    val WrongAnswer2: String,
    val WrongAnswer3: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Question)
        parcel.writeString(CorrectAnswer)
        parcel.writeString(WrongAnswer1)
        parcel.writeString(WrongAnswer2)
        parcel.writeString(WrongAnswer3)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<QuestionModel> {
            override fun createFromParcel(parcel: Parcel): QuestionModel {
                return QuestionModel(parcel)
            }

            override fun newArray(size: Int): Array<QuestionModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}

