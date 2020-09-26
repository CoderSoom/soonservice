package com.kotlin.viewmodel

import com.kotlin.model.ItemMusicOnline
import com.kotlin.service.SongService
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import org.springframework.stereotype.Service
import java.io.IOException
import java.util.ArrayList
import java.util.List

@Service
class SongManager : SongService {
    override fun searchSong(names: String): Any {
        val listNameSong: MutableList<ItemMusicOnline> = ArrayList()
        try {
            val c: Document = Jsoup.connect(("https://chiasenhac.vn/tim-kiem?q="
                    + names.replace(" ", "+")) +
                    "&page_music=" + 1.toString() + "&filter=all").get()
            val els: Elements = c.select("div.tab-content").first().select("ul.list-unstyled")
            for (i in 0 until els.size) {
                val e: Element = els.get(i)
                val childEls: Elements = e.select("li.media")
                for (child in childEls) {
                    try {
                        val linkSong: String = child.select("a").first().attr("href")
                        val imgSong: String = child.select("a").first().select("img").attr("src")
                        val nameSong: String = child.select("a").first().attr("title")
                        val singerSong: String = child.select("div.author").text()
                        listNameSong.add(ItemMusicOnline(null, imgSong, nameSong, singerSong, linkSong, null, null, null))
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return listNameSong
    }

    override fun albumsSong(): Any {
        val listAlbums: MutableList<ItemMusicOnline> = ArrayList()
        try {
            val doc: Document = Jsoup.connect("https://chiasenhac.vn/album-moi.html").get()
            val albumsNew = doc.select("div.content-wrap").select("div.col")
            for (child in albumsNew) {
                val linkAlbumsSong = "https://chiasenhac.vn" + child.select("h3.card-title")
                        .select("a").attr("href")
                val imgAlbumsSong = child.select("div.card-header").attr("style").replace("background-image: url(", "").replace(");", "")
                val nameAlbumsSong = child.select("h3.card-title").select("a").attr("title")
                val nameAlbumsSingle = child.select("p.card-text").select("a").text()
                listAlbums.add(ItemMusicOnline(null, imgAlbumsSong, nameAlbumsSong, nameAlbumsSingle, linkAlbumsSong, null, null, null))
            }
        } catch (e: IOException) {
        }
        return listAlbums


    }

    override fun newSong(): Any {
        val listNewSong: MutableList<ItemMusicOnline> = ArrayList()
        try {
            val doc: Document = Jsoup.connect("https://chiasenhac.vn/bai-hat-moi.html").get()
            val childEls: Elements = doc.select("section.content-current").select("li.media")
            for (child in childEls) {
                val nameSong = child.select("h5.media-title").text()
                val imgSong = child.select("img").attr("src")
                val singerSong = child.select("div.author").text()
                val qualitySong = child.select("small.type_music").text()
                val linkSong = "https://chiasenhac.vn" + child.select("a").attr("href")
                val hourAgo = (child.select("div.media-right").select("i")[0].parentNode().childNodes()[1] as TextNode).text()
                var views = child.select("div.media-right").select("small.time_stt")[1].text().replace("headset ", "")
                listNewSong.add(ItemMusicOnline(null, imgSong, nameSong, singerSong, linkSong, qualitySong, hourAgo, views))
            }
        } catch (e: IOException) {
        }
        return listNewSong
    }

    override fun weeklyRankings(): Any {
        val listWeekly: MutableList<ItemMusicOnline> = ArrayList()
        try {
            val doc: Document = Jsoup.connect("https://chiasenhac.vn/bang-xep-hang/tuan.html").get()
            val els: Elements = doc.select("div.tab-content").select("ul.list-unstyled")
            val childEls: Elements = els.select("li.media")
            for (child in childEls) {
                val linkSong = "https://chiasenhac.vn" + child.select("a").first().attr("href")
                val imgSong: String = child.select("a").first().select("img").attr("src")
                val nameSong: String = child.select("a").first().attr("title")
                val singerSong: String = child.select("div.author").text()
                listWeekly.add(ItemMusicOnline(null, imgSong, nameSong, singerSong, linkSong, null, null, null))
            }
        } catch (e: IOException) {
        }
        return listWeekly
    }


    override fun linkMusic(linkSong: String?): Any? {
        try {
            val c: Document = Jsoup.connect(linkSong).get()
            val els: Elements = c.select("div.tab-content").first().select("a.download_item")
            return if (els.size >= 2) {
                ItemMusicOnline(els[1].attr("href"), "", "", "", "", null, null, null)
            } else {
                ItemMusicOnline(els[0].attr("href"), "", "", "", "", null, null, null)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return null
    }

    override fun rankMusicCountry(country: String): Any {
        var number = 1
        when (country) {
            "viet-nam" -> number = 1
            "us-uk" ->  number = 2
            "chinese" ->  number = 3
            "korea" ->  number =4
            "japan" -> number = 5
            "france" -> number = 6
            "other" ->  number = 7
        }
        var listRankMusicContry = mutableListOf<ItemMusicOnline>()
        try {
            val doc: Document = Jsoup.connect("https://chiasenhac.vn/bang-xep-hang/tuan.html").get()
            val els: Elements = doc.select("div.tab-pane.fade.show.active.tab_bxh")[number].select("ul.list-unstyled")
            for (i in 0 until els.size) {
                val e: Element = els.get(i)
                val childEls: Elements = e.select("li.media")
                for (child in childEls) {
                    val linkSong = "https://chiasenhac.vn" + child.select("a").first().attr("href")
                    val imgSong: String = child.select("a").first().select("img").attr("src")
                    val nameSong: String = child.select("a").first().attr("title")
                    val singerSong: String = child.select("div.author").text()
                    listRankMusicContry.add(ItemMusicOnline(null, imgSong, nameSong, singerSong, linkSong, null, null, null))
                }
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return listRankMusicContry
    }
    override fun rankVideoCountry(country: String): Any {
        var number = 0
        when (country) {
            "viet-nam" -> number = 0
            "us-uk" ->  number = 1
            "chinese" ->  number = 2
            "korea" ->  number =3
            "japan" -> number = 4
            "other" ->  number = 5
        }
        var listRankVideoContry = mutableListOf<ItemMusicOnline>()
        try {
            val doc: Document = Jsoup.connect("https://chiasenhac.vn/bang-xep-hang/tuan.html").get()
            val els: Elements = doc.select("div.tab-pane.fade.show.active.tab_bxh").next()[number].select("ul.list-unstyled")
            for (i in 0 until els.size) {
                val e: Element = els.get(i)
                val childEls: Elements = e.select("li.media")
                for (child in childEls) {
                    val linkVideo = "https://chiasenhac.vn" + child.select("a").first().attr("href")
                    val imgVideo = child.select("div.card-header").attr("style").replace("background-image: url(", "").replace(");", "")
                    val nameVideo = child.select("a").first().attr("title")
                    val singerVideo = child.select("div.author").text()
                    var views = child.select("div.media-right").select("small.time_stt").text().replace("play_arrow ", "")
                    listRankVideoContry.add(ItemMusicOnline(null, imgVideo, nameVideo, singerVideo, linkVideo, null, null, views))
                }
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return listRankVideoContry
    }

}