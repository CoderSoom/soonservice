package com.kotlin.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SongAPI {
    @Autowired

    private lateinit var service: SongService


    /****************************************
     *    GET INFO SONG, VIDEOS, ALBUMS
     ****************************************/
    @GetMapping("/api/getInfo")
    fun getInfo(@RequestParam("link") link: String?): Any? {
        return service.getInfo(link)
    }

    /****************************************
     *    GET TOP RESULTS
     ****************************************/

    @GetMapping("/api/topResult")
    fun topResult(): Any? {
        return service.getTopResults()
    }

    /****************************************
     *    GET NEW ALBUMS, NEW SONGS AND RELATE ALBUMS AND SONG VIDEO
     ****************************************/
    @GetMapping("/api/newAlbumsSong")
    fun newAlbumsSong(): Any {
        return service.addAlbumList()
    }

    @GetMapping("/api/newAlbumsVideo")
    fun newAlbumsVideo(): Any {
        return service.albumsVideos()
    }

    @GetMapping("/api/AlbumsChil")
    fun getAlbumsChil(@RequestParam("linkAlbums") linkAlbums: String): Any {
        return service.getAlbumsChil(linkAlbums)
    }

    @GetMapping("/api/AlbumsChilSinger")
    fun getAlbumsChilSinger(@RequestParam("linkSinger") linkSinger: String): Any {
        return service.getAlbumChilSinger(linkSinger)
    }

    @GetMapping("/api/newSongs")
    fun newSongs(): Any {
        return service.newSong()
    }



    ///RelateSong

    @GetMapping("/api/getRelateSongs")
    fun getRelate(@RequestParam("linkSong") linkSong: String): Any {
        return service.getSongSinger(linkSong)
    }

    @GetMapping("/api/getMVSongs")
    fun getMVSong(@RequestParam("linkSong") linkSong: String): Any {
        return service.getMVSong(linkSong)
    }

    @GetMapping("/api/getRelateVideos")
    fun getRelateVideo(@RequestParam("linkRelate") linkRelate: String): Any {
        return service.getRelateVideo(linkRelate)
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

    /****************************************
     *    GET CATEGORIES
     ****************************************/

    @GetMapping("/api/categoriesCountry")
    fun categoriesCountry(): Any {
        return service.categoriesCountry()
    }


    @GetMapping("/api/categoriesStatus")
    fun categoriesStatus(): Any {
        return service.categoriesStatus()
    }

    @GetMapping("/api/themesStatus")
    fun chilCategoriesStatus(@RequestParam("status") status: String): Any {
        return service.chilCategoriesStatus(status)
    }


    /****************************************
     *    OUTSTANDING SINGER
     ****************************************/

    @GetMapping("/api/outstandingSinger")
    fun outstandingSinger(): Any {
        return service.outstandingSinger()
    }

    /****************************************
     *    SEARCH
     ****************************************/
    @GetMapping("/api/searchAlbums")
    fun searchAlbum(@RequestParam("nameSearch") nameSearch: String?): Any {
        return service.searchAlbum(nameSearch)
    }

    @GetMapping("/api/searchVideos")
    fun searchVideo(@RequestParam("nameSearch") nameSearch: String?): Any {
        return service.searchVideo(nameSearch)
    }

    @GetMapping("/api/searchSong")
    fun searchSong(@RequestParam("nameSearch") namesSong: String): Any {
        return service.searchSong(namesSong)
    }

    @GetMapping("/api/mostSearched")
    fun mostSearch(): Any {
        return service.mostSearched()
    }

    /****************************************
     *   READ
     ****************************************/
    @GetMapping("/api/readSoomNew")
    fun newspapers(): Any {
        return service.readSoomNew()
    }
}