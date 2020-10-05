package com.kotlin.viewmodel

import com.kotlin.model.ItemCategories
import com.kotlin.model.ItemMusicInfo
import com.kotlin.model.ItemMusicList
import com.kotlin.model.ItemMusicOnline
import com.kotlin.service.SongService
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import org.springframework.stereotype.Service
import java.io.IOException
import java.util.*


@Service
class SongManager : SongService {
    private lateinit var doc: Document
    private val URL = "https://chiasenhac.vn"
    private val URL_LOGIN = "$URL/login"
    var userAgent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36"
    fun loginCNS(URLLink: String?) {
        val headers: MutableMap<String, String> = HashMap()
        headers["Content-Type"] = "application/x-www-form-urlencoded"

        val loginForm = Jsoup.connect(URL_LOGIN).userAgent(userAgent).method(Connection.Method.GET).execute()
        var cookies = loginForm.cookies()
        val html = loginForm.parse()
        var authToken = ""
        val metaTags = html.getElementsByTag("meta")
        for (metaTag in metaTags) {
            val content = metaTag.attr("content")
            val name = metaTag.attr("name")
            if (name == "csrf-token") {
                authToken = content
            }
        }
//        println("Found authToken:$authToken")
        val formData: MutableMap<String, String> = HashMap()
        formData["_token"] = authToken
        formData["email"] = "CoderSoom"
        formData["password"] = "123456789"
//        println("cookies before login:")
//        println(cookies)

        val afterLoginPage = Jsoup.connect(URL_LOGIN).cookies(cookies).headers(headers)
                .userAgent(userAgent).data(formData).method(Connection.Method.POST).referrer(URL_LOGIN).execute()
        // update cookies
        // update cookies
        cookies = afterLoginPage.cookies()
//        println("cookies after login:")
//        println(cookies)

        //Get homepage with login

        //Get homepage with login
        val homePage = Jsoup.connect(URLLink).cookies(cookies).method(Connection.Method.GET).userAgent(userAgent)
                .referrer(URL_LOGIN).followRedirects(true).referrer(URL_LOGIN).headers(headers).execute()

        doc = homePage.parse()
    }


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


    ///add Cateogories

    override fun addCategories(): Any {
        var result = mutableListOf<ItemMusicList<ItemCategories>>()
        result.add(ItemMusicList("Categories Contry", categoriesContry()))
        result.add(ItemMusicList("Categories Status", categoriesStatus()))
        return result
    }

    override fun categoriesContry(): MutableList<ItemCategories> {
        val listCategory: MutableList<ItemCategories> = ArrayList()
        try {
            val doc: Document = Jsoup.connect("https://chiasenhac.vn").get()
            val category = doc.select("div.box_catalog").select("a")
            for (i in 0..7) {
                val linkCategories = "https://chiasenhac.vn" + category.select("a")[i].attr("href")
                val imgCategories = category.select("a")[i].attr("style").replace("background: url('", "").replace("')", "").replace("no-repeat;", "")
                val nameCategories = category.select("span")[i].text()
                listCategory.add(ItemCategories(linkCategories, imgCategories, nameCategories))
            }
        } catch (e: IOException) {
        }
        return listCategory
    }

    override fun categoriesStatus(): MutableList<ItemCategories> {
        val listCategory: MutableList<ItemCategories> = ArrayList()
        try {
            val doc: Document = Jsoup.connect("https://chiasenhac.vn").get()
            val category = doc.select("div.box_catalog").select("a")
            for (i in 8 until category.size) {
                val linkCategories = "https://chiasenhac.vn" + category.select("a")[i].attr("href")
                val imgCategories = "http://chiasenhac.vn" + category.select("a")[i].attr("style").replace("background: url('", "").replace("')", "").replace("no-repeat;", "")
                val nameCategories = category.select("span")[i].text()
                listCategory.add(ItemCategories(linkCategories, imgCategories, nameCategories))
            }
        } catch (e: IOException) {
        }
        return listCategory
    }


    override fun addAlbumList(): Any {
        var result = mutableListOf<ItemMusicList<ItemMusicOnline>>()
        var albumsSongYear = albumsSongYear()
        var albumsVideos = albumsVideos()
        var albumSongNews = albumsSongNews()
        result.add(ItemMusicList("Albums Mới Nhất 2020", albumsSongYear))
        result.add(ItemMusicList("Albums Mới", albumSongNews))
        result.add(ItemMusicList("Video Mới", albumsVideos))

        return result


    }

    override fun albumsSongNews(): MutableList<ItemMusicOnline> {
        val listAlbumsNews: MutableList<ItemMusicOnline> = ArrayList()
        try {
            val doc: Document = Jsoup.connect("https://chiasenhac.vn/album-moi.html").get()
            val albumsNew = doc.select("div.content-wrap").select("div.col")
            for (child in albumsNew) {
                val linkAlbumsSong = "https://chiasenhac.vn" + child.select("h3.card-title")
                        .select("a").attr("href")
                val imgAlbumsSong = child.select("div.card-header").attr("style").replace("background-image: url(", "").replace(");", "")
                val nameAlbumsSong = child.select("h3.card-title").select("a").attr("title")
                val nameAlbumsSingle = child.select("p.card-text").select("a").text()
                listAlbumsNews.add(ItemMusicOnline(null, imgAlbumsSong, nameAlbumsSong, nameAlbumsSingle, linkAlbumsSong, null, null, null))
            }
        } catch (e: IOException) {
        }
        return listAlbumsNews


    }

    override fun albumsSongYear(): MutableList<ItemMusicOnline> {
        val listAlbumsYear: MutableList<ItemMusicOnline> = ArrayList()
        try {
            val doc: Document = Jsoup.connect("https://chiasenhac.vn/mp3/vietnam.html").get()
            val albumsNew = doc.select("div.content-wrap").select("div.col")
            for (child in albumsNew) {
                val linkAlbumsSong = "https://chiasenhac.vn" + child.select("h3.card-title")
                        .select("a").attr("href")
                val imgAlbumsSong = child.select("div.card-header").attr("style").replace("background-image: url(", "").replace(");", "")
                val nameAlbumsSong = child.select("h3.card-title").select("a").attr("title")
                val nameAlbumsSingle = child.select("p.card-text").select("a").text()
                listAlbumsYear.add(ItemMusicOnline(null, imgAlbumsSong, nameAlbumsSong, nameAlbumsSingle, linkAlbumsSong, null, null, null))
            }
        } catch (e: IOException) {
        }
        return listAlbumsYear
    }

    override fun albumsVideos(): MutableList<ItemMusicOnline> {
        val listAlbumsVideos: MutableList<ItemMusicOnline> = ArrayList()
        try {
            val doc: Document = Jsoup.connect("https://chiasenhac.vn/video-moi.html").get()
            val albumsNew = doc.select("div.content-wrap").select("div.col")
            for (child in albumsNew) {
                val linkAlbumsSong = "https://chiasenhac.vn" + child.select("h3.card-title")
                        .select("a").attr("href")
                val imgAlbumsSong = child.select("div.card-header").attr("style").replace("background-image: url(", "").replace(");", "")
                val nameAlbumsSong = child.select("h3.card-title").select("a").attr("title")
                val nameAlbumsSingle = child.select("p.card-text").select("a").text()
                listAlbumsVideos.add(ItemMusicOnline(null, imgAlbumsSong, nameAlbumsSong, nameAlbumsSingle, linkAlbumsSong, null, null, null))
            }
        } catch (e: IOException) {
        }
        return listAlbumsVideos
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


    override fun getInfo(link: String?): Any? {
        ////LOGIN CNS
        loginCNS(link)

        var txtAuthorSong = "Sáng tác"
        var txtYear = "Năm phát hành"
        var txtAlbum = "Album"
        var positonSinger: Int
        var positionAlbum: Int
        var positionYear: Int
        val listInfoSong: MutableList<ItemMusicInfo> = ArrayList()
        try {
            val c: Document = Jsoup.connect(link).get()
            val info: Elements = c.select("div.col-md-4")
            var lyricKaraoke = doc.select("div.rabbit-lyrics").text()

            var text = c.select("div.col-md-4").select("ul.list-unstyled").text()
            val els: Elements = c.select("div.tab-content").first().select("a.download_item")
            for (child in info) {
                val imgSong: String = info.select("img").attr("src")
                val nameSong: String = info.select("h2.card-title").text()
                var findTextAuthor = text.contains(txtAuthorSong)
                var findTextYear = text.contains(txtYear)
                var findTextAlbum = text.contains(txtAlbum)
                positionYear = if (findTextYear) {
                    text.indexOf(txtYear)
                } else {
                    text.length
                }
                positionAlbum = if (findTextAlbum) {
                    text.indexOf(txtAlbum)
                } else {
                    positionYear
                }
                positonSinger = if (findTextAuthor) {
                    text.indexOf(txtAuthorSong)
                } else {
                    positionAlbum
                }

                var singerSong = text.removeRange(positonSinger, text.length)
                var authorSong = text.substring(positonSinger, positionAlbum)
                var albumSong = text.substring(positionAlbum, positionYear)
                var yearSong = text.substring(positionYear, text.length)


                val lyricsSong: String = c.select("div.tab-content.tab-lyric").text()
                var txtLine = c.select("span.d-flex.listen").text().replace("headset ", "")
                var findTxt = txtLine.indexOf(" ")
                var category = c.select("li.breadcrumb-item")[1].text().replace("...", "")
                var listenSong = txtLine.removeRange(findTxt, txtLine.length)
                var linkMusic: String? = if (els.size >= 2) {
                    els[1].attr("href")
                } else {
                    els[0].attr("href")
                }
                listInfoSong.add(ItemMusicInfo(linkMusic, imgSong, nameSong, singerSong, authorSong, albumSong, yearSong, lyricsSong ,lyricKaraoke,  listenSong, category))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return listInfoSong
    }


    //getAlbumsChil
    override fun getAlbumsChil(linkAlbums: String): Any {
        val listAlbumsChil: MutableList<ItemMusicOnline> = ArrayList()
        try {
            val doc: Document = Jsoup.connect(linkAlbums).get()
            val els: Elements = doc.select("div.d-table")
            val childEls: Elements = els.select("div.card-footer")
            for (child in childEls) {
                val linkSongChild = "https://chiasenhac.vn" + child.select("a").first().attr("href")
                val imgSongChild: String = doc.select("div.row").select("img").attr("src")
                val nameSongChil: String = child.select("a").first().attr("title")
                val singerSongChild: String = child.select("div.author-ellepsis").text()
                listAlbumsChil.add(ItemMusicOnline(null, imgSongChild, nameSongChil, singerSongChild, linkSongChild, null, null, null))
            }
        } catch (e: IOException) {
        }
        return listAlbumsChil
    }

    override fun getRelate(linkSongAlbums: String): Any {
        var result = mutableListOf<ItemMusicList<ItemMusicOnline>>()
        var mvSong = getMVSong(linkSongAlbums)
        var songSinger = getSongSinger(linkSongAlbums)
        var videoSinger = getVideoSinger(linkSongAlbums)
        result.add(ItemMusicList("MV Bài Hát", mvSong))
        result.add(ItemMusicList("Bài Hát Cùng Ca Sĩ", songSinger))
        result.add(ItemMusicList("Video Cùng Ca Sĩ", videoSinger))

        return result

    }

    override fun getMVSong(linkSongAlbums: String): MutableList<ItemMusicOnline> {
        val listMVSong: MutableList<ItemMusicOnline> = ArrayList()
        try {
            val doc: Document = Jsoup.connect(linkSongAlbums).get()
            var imgVideos = doc.select("ul.list-unstyled.mv_sing").select("img").attr("src")
            var linkVideos = doc.select("ul.list-unstyled.mv_sing").select("a").attr("href")
            var nameVideos = doc.select("ul.list-unstyled.mv_sing").select("h5.mt-0").text()
            var singerVideos = doc.select("ul.list-unstyled.mv_sing").select("div.author").text()

            listMVSong.add(ItemMusicOnline(null, imgVideos, nameVideos, singerVideos, linkVideos, null, null, null))
        } catch (e: IOException) {
        }
        return listMVSong
    }

    override fun getSongSinger(linkSongAlbums: String): MutableList<ItemMusicOnline> {
        val listSongSinger: MutableList<ItemMusicOnline> = ArrayList()
        try {
            val doc: Document = Jsoup.connect(linkSongAlbums).get()
            val songSinger = doc.select("div.row.row10px.float-col-width").select("div.col")
            for (i in 0..4) {
                val linkSongSinger = "https://chiasenhac.vn" + songSinger.select("h3.card-title")
                        .select("a").attr("href")
                val imgSongSinger = songSinger.select("div.card-header").eq(i).attr("style").replace("background-image: url(", "").replace(");", "")
                val nameSongSinger = songSinger.select("h3.card-title").eq(i).select("a").attr("title")
                val singerSongSinger = songSinger.select("p.card-text").eq(i).select("a").text()
                listSongSinger.add(ItemMusicOnline(null, imgSongSinger, nameSongSinger, singerSongSinger, linkSongSinger, null, null, null))
            }
        } catch (e: IOException) {
        }
        return listSongSinger
    }

    override fun getVideoSinger(linkSongAlbums: String): MutableList<ItemMusicOnline> {
        val listVideoSinger: MutableList<ItemMusicOnline> = ArrayList()
        try {
            val doc: Document = Jsoup.connect(linkSongAlbums).get()
            val songSinger = doc.select("div.row.row10px.float-col-width").select("div.col")
            for (i in 5..9) {
                val linkVideoSinger = "https://chiasenhac.vn" + songSinger.select("h3.card-title")
                        .select("a").attr("href")
                val imgVideosSinger = songSinger.select("div.card-header").eq(i).attr("style").replace("background-image: url(", "").replace(");", "")
                val nameVideoSinger = songSinger.select("h3.card-title").eq(i).select("a").attr("title")
                val singerVideoSinger = songSinger.select("p.card-text").eq(i).select("a").text()
                listVideoSinger.add(ItemMusicOnline(null, imgVideosSinger, nameVideoSinger, singerVideoSinger, linkVideoSinger, null, null, null))
            }
        } catch (e: IOException) {
        }
        return listVideoSinger
    }

    override fun getSuggestions(linkSongAlbums: String): Any {
        var result = mutableListOf<ItemMusicList<ItemMusicOnline>>()
        var listSuggestion = mutableListOf<ItemMusicOnline>()
        try {
            val doc: Document = Jsoup.connect(linkSongAlbums).get()
            val els: Elements = doc.select("ul.list-unstyled.list_music.sug_music")
            for (i in 0 until els.size) {
                val e: Element = els.get(i)
                val childEls: Elements = e.select("li.media")
                for (child in childEls) {
                    val linkSong = "https://chiasenhac.vn" + child.select("a").first().attr("href")
                    val imgSong: String = child.select("a").first().select("img").attr("src")
                    val nameSong: String = child.select("a").first().attr("title")
                    val singerSong: String = child.select("div.author").text()
                    val qualitySong = child.select("small.type_music").text()
                    var views = child.select("div.media-right").select("small.time_stt").text().replace("play_arrow ", "")
                    listSuggestion.add(ItemMusicOnline(null, imgSong, nameSong, singerSong, linkSong, qualitySong, null, views))

                }
            }
            result.add(ItemMusicList("Gợi Ý", listSuggestion))
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return result
    }


    override fun rankMusicCountry(country: String): Any {
        var number = 1
        when (country) {
            "viet-nam" -> number = 1
            "us-uk" -> number = 2
            "chinese" -> number = 3
            "korea" -> number = 4
            "japan" -> number = 5
            "france" -> number = 6
            "other" -> number = 7
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
                    var views = child.select("div.media-right").select("small.time_stt").text().replace("play_arrow ", "")
                    listRankMusicContry.add(ItemMusicOnline(null, imgSong, nameSong, singerSong, linkSong, null, null, views))
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
            "us-uk" -> number = 1
            "chinese" -> number = 2
            "korea" -> number = 3
            "japan" -> number = 4
            "other" -> number = 5
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