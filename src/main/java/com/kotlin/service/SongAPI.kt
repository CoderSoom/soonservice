package com.kotlin.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SongAPI {
    @Autowired

    // co autiwired nen khong can new SongService
    private lateinit var service: SongService


    /****************************************
     *    SEARCH SONG
     ****************************************/
    @GetMapping("/api/searchSong")
    fun searchSong(@RequestParam("nameSong", required = false) namesSong: String): Any {
        return service.searchSong(namesSong)
    }




    /****************************************
     *    CATEGORIES
     ****************************************/
    @GetMapping("/api/getCategories")
    fun getCategories():Any{
        return service.addCategories()
    }


    /****************************************
     *    GET INFO SONG, VIDEOS, ALBUMS
     ****************************************/
    @GetMapping("/api/getInfo")
    fun getInfo(@RequestParam("link") link: String?): Any? {
        return service.getInfo(link)
    }



    /****************************************
     *    GET NEW ALBUMS, NEW SONGS AND RELATE ALBUMS AND SONG VIDEO
     ****************************************/
    @GetMapping("/api/newAlbums")
    fun newAlbums(): Any {
        return service.addAlbumList()
    }

    @GetMapping("/api/AlbumsChil")
    fun getAlbumsChil(@RequestParam("linkAlbums") linkAlbums: String): Any {
        return service.getAlbumsChil(linkAlbums)
    }

    @GetMapping("/api/newSongs")
    fun newSongs(): Any {
        return service.newSong()
    }

    @GetMapping("/api/getRelate")
    fun getRelate(@RequestParam("linkRelate") linkRelate: String): Any {
        return service.getRelate(linkRelate)
    }



    /****************************************
     *    GET SUGGESTIONS BELOW SONG VIDEO ALBUMS
     ****************************************/
    @GetMapping("/api/getSuggestions")
    fun getSuggestions(@RequestParam("linkAlbumsSong") linkRelate: String): Any {
        return service.getSuggestions(linkRelate)
    }



    /****************************************
     *    GET RANKING
     ****************************************/
    @GetMapping("/api/weeklyRankings")
    fun weeklyRankings(): Any {
        return service.weeklyRankings()
    }

    @GetMapping("/api/rankMusicCountry")
    fun rankMusicCountry(@RequestParam("country") country: String): Any {
        return service.rankMusicCountry(country)
    }


    @GetMapping("/api/rankVideoCountry")
    fun rankVideoCountry(@RequestParam("country") country: String): Any {
        return service.rankVideoCountry(country)
    }


}