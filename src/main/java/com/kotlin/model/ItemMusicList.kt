package com.kotlin.model

class ItemMusicList<T>{
    var nameCategories = ""
    var values = mutableListOf<T>()
    constructor(name:String, values:MutableList<T>){
        this.nameCategories = name
        this.values = values
    }
    constructor()
}