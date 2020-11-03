package com.kotlin.modelmanager

import com.kotlin.model.*
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
        var result = mutableListOf<ItemMusicList<ItemMusicOnline>>()
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
                        val linkSong: String = "https://chiasenhac.vn" + child.select("a").first().attr("href")
                        val imgSong: String = child.select("a").first().select("img").attr("src")
                        val nameSong: String = child.select("a").first().attr("title")
                        val singerSong: String = child.select("div.author").text()
                        listNameSong.add(ItemMusicOnline(imgSong = imgSong, nameSong = nameSong, singerSong = singerSong, linkSong = linkSong))

                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }
                result.add(ItemMusicList("Bài Hát", listNameSong))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return result
    }


    ///add Cateogories


    override fun categoriesCountry(): Any {
        var result = mutableListOf<ItemMusicList<ItemCategories>>()
        val listCategory: MutableList<ItemCategories> = ArrayList()
        try {
            val doc: Document = Jsoup.connect("https://chiasenhac.vn").get()
            val category = doc.select("div.box_catalog").select("a")
            for (i in 0..7) {
                val linkCategories = "https://chiasenhac.vn" + category.select("a")[i].attr("href")
                val imgCategories = category.select("a")[i].attr("style").replace("background: url('", "").replace("')", "").replace(" no-repeat;", "")
                val nameCategories = category.select("span")[i].text()
                listCategory.add(ItemCategories(linkCategories, imgCategories, nameCategories))

            }
            result.add(ItemMusicList("Bảng Xếp Hạng", listCategory))
        } catch (e: IOException) {
        }
        return result
    }

    override fun categoriesStatus(): Any {
        var result = mutableListOf<ItemMusicList<ItemCategories>>()
        val listCategory: MutableList<ItemCategories> = ArrayList()
        try {
            val doc: Document = Jsoup.connect("https://chiasenhac.vn").get()
            val category = doc.select("div.box_catalog").select("a")
            for (i in 8 until category.size) {
                val linkCategories = "https://chiasenhac.vn" + category.select("a")[i].attr("href")
                val imgCategories = "https://chiasenhac.vn" + category.select("a")[i].attr("style").replace("background: url('", "").replace("')", "").replace(" no-repeat;", "")
                val nameCategories = category.select("span")[i].text()
                listCategory.add(ItemCategories(linkCategories, imgCategories, nameCategories))


            }
            result.add(ItemMusicList("Tâm Trạng & Hoạt Động", listCategory))
        } catch (e: IOException) {
        }
        return result
    }

    override fun getTopResults(): Any {
        var result = mutableListOf<ItemMusicList<ItemMusicOnline>>()
        val listTopResult: MutableList<ItemMusicOnline> = ArrayList()
        try {
            val doc: Document = Jsoup.connect("https://chiasenhac.vn").get()
            val albumsNew = doc.select("div.item")
            for (child in albumsNew) {
                val linkTopResult = "https://chiasenhac.vn" + child.select("a").attr("href")
                val imgTopResult = child.select("div.card-header").attr("style").replace("background-image: url(", "").replace(");", "")
                val nameTopResult = child.select("a").text()
                val nameSingerTopResult = child.select("p.card-text").text()
                listTopResult.add(ItemMusicOnline(imgSong = imgTopResult, nameSong = nameTopResult, singerSong = nameSingerTopResult, linkSong = linkTopResult))

            }
            result.add(ItemMusicList("Đang Thịnh Hành", listTopResult))
        } catch (e: IOException) {
        }
        return result
    }


    override fun addAlbumList(): Any {
        var result = mutableListOf<ItemMusicList<ItemMusicOnline>>()
        var albumsSongYear = albumsSongYear()
        var albumSongNews = albumsSongNews()
        result.add(ItemMusicList("Dành Riêng Cho Bạn", albumsSongYear))
        result.add(ItemMusicList("Albums Hot", albumSongNews))


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
                listAlbumsNews.add(ItemMusicOnline(imgSong = imgAlbumsSong, nameSong = nameAlbumsSong, singerSong = nameAlbumsSingle, linkSong = linkAlbumsSong))
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
                listAlbumsYear.add(ItemMusicOnline(imgSong = imgAlbumsSong, nameSong = nameAlbumsSong, singerSong = nameAlbumsSingle, linkSong = linkAlbumsSong))
            }
        } catch (e: IOException) {
        }
        return listAlbumsYear
    }

    override fun albumsVideos(): Any {
        var result = mutableListOf<ItemMusicList<ItemMusicOnline>>()
        val listAlbumsVideos: MutableList<ItemMusicOnline> = ArrayList()
        try {
            val doc: Document = Jsoup.connect("https://chiasenhac.vn/video-moi.html").get()
            val albumsNew = doc.select("div.content-wrap").select("div.col")
            for (child in albumsNew) {
                val linkAlbumsVideo = child.select("h3.card-title")
                        .select("a").attr("href")
                val timeVideo = child.select("span.time").text()
                val imgAlbumsVideo = child.select("div.card-header").attr("style").replace("background-image: url(", "").replace(");", "")
                val nameAlbumsVideo = child.select("h3.card-title").select("a").attr("title")
                val nameAlbumsSingle = child.select("p.card-text").select("a").text()
                listAlbumsVideos.add(ItemMusicOnline(imgSong = imgAlbumsVideo, nameSong = nameAlbumsVideo, singerSong = nameAlbumsSingle, linkSong = linkAlbumsVideo, time = timeVideo))

            }
            result.add(ItemMusicList("Video Hot", listAlbumsVideos))
        } catch (e: IOException) {
        }
        return result
    }


    override fun newSong(): Any {
        var result = mutableListOf<ItemMusicList<ItemMusicOnline>>()
        val listNewSong: MutableList<ItemMusicOnline> = ArrayList()
        try {
            val doc: Document = Jsoup.connect("https://chiasenhac.vn/bai-hat-moi.html").get()
            val childEls: Elements = doc.select("section.content-current").select("li.media")
            for (child in childEls) {
                val nameSong = child.select("h5.media-title").text()
                val imgSong = child.select("img").attr("src")
                val singerSong = child.select("div.author").text()
                val qualitySong = child.select("small.type_music").text()
                val linkSong = child.select("a").attr("href")
                val hourAgo = (child.select("div.media-right").select("i")[0].parentNode().childNodes()[1] as TextNode).text()
                var views = child.select("div.media-right").select("small.time_stt")[1].text().replace("headset ", "")
                listNewSong.add(ItemMusicOnline(imgSong = imgSong, nameSong = nameSong, singerSong = singerSong, linkSong = linkSong, quality = qualitySong, hoursAgo = hourAgo, views = views))

            }
            result.add(ItemMusicList("Bài Hát Mới Nhất", listNewSong))
        } catch (e: IOException) {
        }
        return result
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
                listWeekly.add(ItemMusicOnline(imgSong = imgSong, nameSong = nameSong, singerSong = singerSong, linkSong = linkSong))
            }
        } catch (e: IOException) {
        }
        return listWeekly
    }


    override fun getInfo(link: String?): Any? {
        ////LOGIN CNS
        loginCNS(link)


        var singerSong = ""
        var authorSong = ""
        var albumSong = ""
        var lyricsSong = ""
        var yearSong = ""
        var imgSong = ""
        var category = ""
        var listenSong = ""
        var linkMusic = ""
        var nameSong = ""
        var lyricKaraoke = ""
        var txtAuthorSong = "Sáng tác"
        var txtYear = "Năm phát hành"
        var txtAlbum = "Album"

        var positonSinger: Int
        var positionAlbum: Int
        var a =""
        var b=""
        var positionYear: Int
        try {
            val c: Document = Jsoup.connect(link).get()
            val info: Elements = c.select("div.col-md-4")
            lyricKaraoke = doc.select("div.rabbit-lyrics").text()

            var text = c.select("div.col-md-4").select("ul.list-unstyled").text()
            val els: Elements = c.select("div.tab-content").first().select("a.download_item")
            imgSong = info.select("img").attr("src")
            nameSong = info.select("h2.card-title").text()
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

            singerSong = text.removeRange(positonSinger, text.length)
            authorSong = text.substring(positonSinger, positionAlbum)
            albumSong = text.substring(positionAlbum, positionYear)
            yearSong = text.substring(positionYear, text.length)


            lyricsSong = c.select("div.tab-content.tab-lyric").select("div[id]")[2].toString().replace("<br>", "").replace("<div id=\"fulllyric\">", "").replace("</div>", "")
            var txtLine = c.select("span.d-flex.listen").text().replace("headset ", "")
            var findTxt = txtLine.indexOf(" ")
            category = "Thể loại: " + c.select("li.breadcrumb-item")[1].text().replace("...", "")

            listenSong = txtLine.removeRange(findTxt, txtLine.length)

            a = if (els.size >= 2) {
                els[1].attr("href")
            } else {
                els[0].attr("href").substring(0, linkMusic.length-5)
            }
            b = a.substring(0, a.length-5)


        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return ItemInfo(b, imgSong, nameSong, singerSong, authorSong, albumSong, yearSong, lyricsSong, lyricKaraoke, listenSong, category)
    }


    //getAlbumsChil
    override fun getAlbumsChil(linkAlbums: String): Any {
        var textName: String
        val listAlbumsChil: MutableList<ItemMusicOnline> = ArrayList()
        try {
            val doc: Document = Jsoup.connect(linkAlbums).get()
            val els: Elements = doc.select("div.d-table")
            val childEls: Elements = els.select("div.card-footer")
            for (child in childEls) {
                var textID = child.select("a").first().text()
                var findID = textID.indexOf(".")
                var id = textID.substring(0, findID)
                val linkSongChild = child.select("a").first().attr("href")
                val imgSongChild: String = doc.select("div.row").select("img").attr("src")
                textName = child.select("a").first().text()
                var findTextName = textName.indexOf(" ")
                var nameSongChil = textName.substring(findTextName + 1, textName.length)
                val singerSongChild: String = child.select("div.author-ellepsis").text()
                listAlbumsChil.add(ItemMusicOnline(id = id, imgSong = imgSongChild, nameSong = nameSongChil, singerSong = singerSongChild, linkSong = linkSongChild))
            }
        } catch (e: IOException) {
        }
        return listAlbumsChil
    }

    override fun getRelateSong(linkSongAlbums: String): Any {
        var result = mutableListOf<ItemMusicList<ItemMusicOnline>>()
        var mvSong = getMVSong(linkSongAlbums)
        var songSinger = getSongSinger(linkSongAlbums)
        result.add(ItemMusicList("", mvSong))
        result.add(ItemMusicList("Bài Hát Cùng Ca Sĩ", songSinger))
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

            listMVSong.add(ItemMusicOnline(imgSong = imgVideos, nameSong = nameVideos, singerSong = singerVideos, linkSong = linkVideos))
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
                listSongSinger.add(ItemMusicOnline(imgSong = imgSongSinger, nameSong = nameSongSinger, singerSong = singerSongSinger, linkSong = linkSongSinger))
            }
        } catch (e: IOException) {
        }
        return listSongSinger
    }

    override fun getRelateVideo(linkSongAlbums: String): Any {
        val listVideoSinger: MutableList<ItemMusicOnline> = ArrayList()
        try {
            val doc: Document = Jsoup.connect(linkSongAlbums).get()
            val songSinger = doc.select("div.row.row10px.float-col-width").select("div.col")
            for (i in 5..9) {
                val linkVideoSinger = songSinger.select("h3.card-title").eq(i)
                        .select("a").attr("href")
                val imgVideosSinger = songSinger.select("div.card-header").eq(i).attr("style").replace("background-image: url(", "").replace(");", "")
                val nameVideoSinger = songSinger.select("h3.card-title").eq(i).select("a").attr("title")
                val singerVideoSinger = songSinger.select("p.card-text").eq(i).select("a").text()
                listVideoSinger.add(ItemMusicOnline(imgSong = imgVideosSinger, nameSong = nameVideoSinger, singerSong = singerVideoSinger, linkSong = linkVideoSinger))
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
                    listSuggestion.add(ItemMusicOnline(imgSong = imgSong, nameSong = nameSong, singerSong = singerSong, linkSong = linkSong, quality = qualitySong, views = views))

                }
            }
            result.add(ItemMusicList("Gợi Ý", listSuggestion))
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return result
    }

    override fun getAlbumChilSinger(linkSinger: String): Any {
        val listAlbumSinger: MutableList<ItemMusicOnline> = ArrayList()
        try {
            val doc: Document = Jsoup.connect(linkSinger).get()
            val albumsNew = doc.select("ul.list-unstyled.list_music").select("li.media.align-items-stretch.not")
            for (child in albumsNew) {
                val linkSinger = child.select("a").attr("href")
                val imgSinger = child.select("img").attr("src")
                val nameSong = child.select("a").attr("title")
                val nameSinger = child.select("div.author").text()
                val views = child.select("div.media-right").select("small.time_stt").text().replace("play_arrow ", "")
                listAlbumSinger.add(ItemMusicOnline(linkSong = linkSinger, imgSong = imgSinger, nameSong = nameSong, singerSong = nameSinger, views = views))

            }
        } catch (e: IOException) {
        }
        return listAlbumSinger
    }

    override fun readSoomNew(): Any {
        var findImgBegin: Int
        var findImgEnd: Int
        var findLinkBegin: Int
        var findLinkEnd: Int
        var findDescriptionBegin: Int
        var findDescriptionEnd: Int
        var findTitleBegin: Int
        var findTitleEnd: Int
        var findDateBegin :Int
        var findDateEnd :Int
        var findTimeEnd :Int

        val listNewspapers: MutableList<ItemNewspapers> = ArrayList()
        try {
            val doc: Document = Jsoup.connect("https://cdn.24h.com.vn/upload/rss/canhacmtv.rss").get()
            val albumsNew = doc.select("description")
            for (i in 2 until albumsNew.size) {
                val contentDes = doc.select("description")[i-1].toString()
                val contentTitle = doc.select("title")[i].toString()
                val contentTime = doc.select("pubDate")[i-1].toString()

                findTitleBegin = contentTitle.indexOf("<title>")+"<title>".length
                findTitleEnd = contentTitle.indexOf("</title>")
                findImgBegin = contentDes.indexOf("https://image.24h.com.vn")
                findImgEnd = contentDes.indexOf("' alt=")
                findLinkBegin = contentDes.indexOf("https://www.24h.com.vn")
                findLinkEnd = contentDes.indexOf("' title")
                findDescriptionBegin = contentDes.indexOf("</a><br />")+"</a><br />".length
                findDescriptionEnd = contentDes.indexOf("\n]]>")
                findDateBegin = contentTime.indexOf("<pubDate>\n")+"<pubDate>\n".length+1
                findTimeEnd = contentTime.indexOf("\n</pubDate>")
                findDateEnd = contentTime.indexOf(":")
                var imgContent = contentDes.substring(findImgBegin, findImgEnd)
                var linkContent = contentDes.substring(findLinkBegin, findLinkEnd)
                var description = contentDes.substring(findDescriptionBegin, findDescriptionEnd)
                var titleContent = contentTitle.substring(findTitleBegin, findTitleEnd)
                var dateContent = contentTime.substring(findDateBegin, findDateEnd-3)
                var timeContent  = contentTime.substring(findDateEnd-2, findTimeEnd)
                var timeandDate = "$dateContent | $timeContent"
                listNewspapers.add(ItemNewspapers(linkContent, titleContent, imgContent, description, timeandDate))

            }
        } catch (e: IOException) {
        }
        return listNewspapers
    }


    override fun rankMusicCountry(country: String): Any {
        var number = 0
        when (country) {
            "viet-nam" -> number = 0
            "us-uk" -> number = 1
            "chinese" -> number = 2
            "korea" -> number = 3
            "japan" -> number = 4
            "france" -> number = 5
            "other" -> number = 6
            "play-back" -> number =7
        }
        var listRankMusicContry = mutableListOf<ItemMusicOnline>()
        try {
            val doc: Document = Jsoup.connect("https://chiasenhac.vn/bang-xep-hang/tuan.html").get()
            val els: Elements = doc.select("div.tab-pane.fade.show.active.tab_bxh")[number].select("ul.list-unstyled")
            for (i in 0 until els.size) {
                val e: Element = els.get(i)
                val childEls: Elements = e.select("li.media")
                for (child in childEls) {
                    val id = child.select("span.counter").text()
                    val linkSong = "https://chiasenhac.vn" + child.select("a").first().attr("href")
                    val imgSong: String = child.select("a").first().select("img").attr("src")
                    val nameSong: String = child.select("a").first().attr("title")
                    val singerSong: String = child.select("div.author").text()
                    var views = child.select("div.media-right").select("small.time_stt").text().replace("play_arrow ", "")
                    listRankMusicContry.add(ItemMusicOnline(id = id, imgSong = imgSong, nameSong = nameSong, singerSong = singerSong, linkSong = linkSong, views = views))
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
                    val id = child.select("span.counter").text()
                    val linkVideo = "https://chiasenhac.vn" + child.select("a").first().attr("href")
                    val imgVideo = child.select("div.card-header").attr("style").replace("background-image: url(", "").replace(");", "")
                    val nameVideo = child.select("a").first().attr("title")
                    val singerVideo = child.select("div.author").text()
                    var views = child.select("div.media-right").select("small.time_stt").text().replace("play_arrow ", "")
                    listRankVideoContry.add(ItemMusicOnline(id = id, imgSong = imgVideo, nameSong = nameVideo, singerSong = singerVideo, linkSong = linkVideo, views = views))
                }
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return listRankVideoContry
    }

    override fun outstandingSinger(): Any {
        var result = mutableListOf<ItemMusicList<ItemCategories>>()
        val listoutstandingSinger: MutableList<ItemCategories> = ArrayList()
        try {
            val doc: Document = Jsoup.connect("https://chiasenhac.vn").get()
            val albumsNew = doc.select("div.singer_grid").select("a")
            for (child in albumsNew) {
                val linkSinger = "https://chiasenhac.vn" + child.select("a").attr("href")
                val imgSinger = child.select("a").attr("style").replace("background-image: url(", "").replace(");", "")
                val nameSinger = child.text()
                listoutstandingSinger.add(ItemCategories(linkSinger, imgSinger, nameSinger))


            }
            result.add(ItemMusicList("Ca Sĩ Nổi Bật", listoutstandingSinger))
        } catch (e: IOException) {
        }
        return result
    }


    override fun searchAlbum(nameSong: String?): Any {
        var result = mutableListOf<ItemMusicList<ItemMusicOnline>>()
        val listSearchAlbum: MutableList<ItemMusicOnline> = ArrayList()
        try {
            val c: Document = Jsoup.connect(("https://chiasenhac.vn/tim-kiem?q="
                    + nameSong?.replace(" ", "+")) +
                    "&page_album=" + 1.toString() + "&filter=all").get()
            val els: Elements = c.select("div.row.row10px.float-col-width").first().select("div.col")
            for (child in els) {
                try {
                    val linkSong: String = "https://chiasenhac.vn" + child.select("a").first().attr("href")
                    val imgSong: String = child.select("div.card-header").attr("style").replace("background-image: url(", "").replace(");", "")
                    val nameSong: String = child.select("a").first().attr("title")
                    val singerSong: String = child.select("p.card-text").text()
                    listSearchAlbum.add(ItemMusicOnline(imgSong = imgSong, nameSong = nameSong, singerSong = singerSong, linkSong = linkSong))

                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
            }
            result.add(ItemMusicList("Albums & Playlist", listSearchAlbum))

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return result
    }

    override fun searchVideo(nameSong: String?): Any {
        var result = mutableListOf<ItemMusicList<ItemMusicOnline>>()
        val listSearchVideo: MutableList<ItemMusicOnline> = ArrayList()
        try {
            val c: Document = Jsoup.connect(("https://chiasenhac.vn/tim-kiem?q="
                    + nameSong?.replace(" ", "+")) +
                    "&page_video=" + 1.toString() + "#").get()
            val els: Elements = c.select("div.row.row10px.float-col-width-video").first().select("div.col")
            for (child in els) {
                try {
                    val linkSong: String = child.select("a").first().attr("href")
                    val imgSong: String = child.select("div.card-header").attr("style").replace("background-image: url(", "").replace(");", "")
                    val nameSong: String = child.select("a").first().attr("title")
                    val time: String = child.select("p.time").text()
                    val singerSong: String = child.select("p.card-text")[1].text()
                    listSearchVideo.add(ItemMusicOnline(imgSong = imgSong, nameSong = nameSong, singerSong = singerSong, linkSong = linkSong, time = time))

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            result.add(ItemMusicList("MV", listSearchVideo))

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return result
    }

    override fun mostSearched(): Any {
        var result = mutableListOf<ItemMusicList<ItemMusicOnline>>()
        val listMostSearched: MutableList<ItemMusicOnline> = ArrayList()
        try {
            val doc: Document = Jsoup.connect("https://chiasenhac.vn").get()
            val albumsNew = doc.select("a.search-line.parent-line.search-line-music")
            for (child in albumsNew) {
                val linkSong = child.select("a").attr("href")
                val nameSong = child.select("h5").attr("title")
                val nameSinger = child.select("div.author").text()
                listMostSearched.add(ItemMusicOnline(linkSong = linkSong, nameSong = nameSong, singerSong = nameSinger))

            }
            result.add(ItemMusicList("Tìm Kiếm Nhiều Nhất", listMostSearched))
        } catch (e: IOException) {
        }
        return result
    }

}