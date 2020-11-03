package com.kotlin.service

import com.kotlin.model.ItemMusicOnline


interface SongService {
    fun searchSong(nameSong: String): Any


    //
    fun categoriesCountry(): Any
    fun categoriesStatus(): Any

    //
    fun getTopResults():Any

    fun addAlbumList(): Any
    fun albumsSongNews(): Any
    fun albumsSongYear(): Any
    fun albumsVideos(): Any

    //get AlbumsChil
    fun getAlbumsChil(linkAlbums: String): Any
    fun getRelateSong(linkSongAlbums: String): Any
    fun getMVSong(linkSongAlbums: String): Any
    fun getSongSinger(linkSongAlbums: String): Any
    fun getRelateVideo(linkSongAlbums: String): Any
    fun getSuggestions(linkSongAlbums: String): Any

    //
    fun getAlbumChilSinger(linkSinger: String): Any
    //
    fun readSoomNew(): Any



    fun newSong(): Any
    fun weeklyRankings(): Any
    fun getInfo(linkSong: String?): Any?
    fun rankMusicCountry(country: String): Any
    fun rankVideoCountry(country: String): Any

    //
    fun outstandingSinger(): Any

    //
    fun searchAlbum(nameSong: String?): Any
    fun searchVideo(nameSong: String?): Any
    fun mostSearched():Any
}