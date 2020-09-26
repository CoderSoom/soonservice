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

    @GetMapping("/api/searchSong")
    fun searchSong(@RequestParam("nameSong", required = false) names: String): Any {
        return service.searchSong(names)
    }

    @GetMapping("/api/linkMusic")
    fun linkMusic(@RequestParam("linkSong") linkSong: String?): Any? {
        return service.linkMusic(linkSong)
    }

    @GetMapping("/api/albumsSong")
    fun albumsSong(): Any {
        return service.albumsSong()
    }
    @GetMapping("/api/newSongs")
    fun newSongs(): Any{
        return service.newSong()
    }
    @GetMapping("/api/weeklyRankings")
    fun weeklyRankings(): Any{
        return service.weeklyRankings()
    }
    @GetMapping("/api/rankMusicCountry")
    fun rankMusicCountry(@RequestParam("country") country: String): Any{
        return service.rankMusicCountry(country)
    }
    @GetMapping("/api/rankVideoCountry")
    fun rankVideoCountry(@RequestParam("country") country: String): Any{
        return service.rankVideoCountry(country)
    }


}