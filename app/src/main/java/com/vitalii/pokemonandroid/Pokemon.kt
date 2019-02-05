package com.vitalii.pokemonandroid

import android.location.Location

open class Pokemon{
    var name:String? = null
    var des:String? = null
    var image:Int? = null
    var power:Double? = null
    var location:Location?=null
    var isCatched:Boolean = false
    constructor(image:Int, name:String, des:String, power: Double, latitude:Double, longitude:Double, isCatched:Boolean=false){
        this.name = name
        this.des = des
        this.image = image
        this.location = Location(name)
        this.location!!.latitude = latitude
        this.location!!.longitude = longitude
        this.power = power
        this.isCatched = isCatched

    }
}