package com.kotlin.service

import org.springframework.stereotype.Service


interface SongService{
    fun searchSong(nameSong: String): Any
    fun albumsSong(): Any
    fun newSong(): Any
    fun weeklyRankings(): Any
    fun linkMusic(linkMusic: String?): Any?
    fun rankMusicCountry(country: String): Any
    fun rankVideoCountry(country: String): Any
}