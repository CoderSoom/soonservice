package com.kotlin.service

import com.kotlin.model.ItemMusicOnline


interface SongService {
    fun searchSong(nameSong: String): Any

    fun addCategories(): Any
    fun categoriesContry(): Any
    fun categoriesStatus(): Any


    fun addAlbumList(): Any
    fun albumsSongNews(): Any
    fun albumsSongYear(): Any
    fun albumsVideos(): Any

    //get AlbumsChil
    fun getAlbumsChil(linkAlbums: String): Any
    fun getRelate(linkSongAlbums: String): Any
    fun getMVSong(linkSongAlbums: String): Any
    fun getSongSinger(linkSongAlbums: String): Any
    fun getVideoSinger(linkSongAlbums: String): Any
    fun getSuggestions(linkSongAlbums: String): Any

    fun newSong(): Any
    fun weeklyRankings(): Any
    fun getInfo(linkSong: String?): Any?
    fun rankMusicCountry(country: String): Any
    fun rankVideoCountry(country: String): Any
}