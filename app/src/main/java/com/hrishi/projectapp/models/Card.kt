package com.hrishi.projectapp.models

import android.os.Parcel
import android.os.Parcelable

data class Card(
    var name:String="",
    var createdBy:String="",
    var assignedTo:ArrayList<String> = ArrayList(),
    var date:String="",
    var members:ArrayList<User> = ArrayList()
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(User.CREATOR)!!
    ) {
    }
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(createdBy)
        parcel.writeStringList(assignedTo)
        parcel.writeString(date)
        parcel.writeTypedList(members)
    }
    override fun describeContents(): Int {
        return 0
    }
    companion object CREATOR : Parcelable.Creator<Card> {
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }
        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }
}
