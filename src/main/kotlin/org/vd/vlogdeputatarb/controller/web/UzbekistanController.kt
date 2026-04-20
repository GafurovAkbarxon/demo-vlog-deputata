package org.vd.vlogdeputatarb.controller.web

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.vd.vlogdeputatarb.controller.web.persons.dto.PersonFilterRequest
import org.vd.vlogdeputatarb.controller.web.persons.enums.AliveFilter
import org.vd.vlogdeputatarb.controller.web.persons.enums.Season
import org.vd.vlogdeputatarb.data.wiki.enums.*
import org.vd.vlogdeputatarb.service.ArticleService
import org.vd.vlogdeputatarb.service.PersonService
import org.vd.vlogdeputatarb.service.TagService
import org.vd.vlogdeputatarb.util.enums.Language

@Controller
@RequestMapping("/{lang:ru|uz}/uzbekistan")
class UzbekistanController (
    private val articleService: ArticleService,
    private val tagService: TagService,
    private val service: PersonService
){

    @GetMapping("/tashkent")
    fun getTashkent(
        model: Model,
        @PathVariable lang: String
    ): String {
        val tagName = if (lang == "ru") {
            "ташкент"
        } else {
            "tashkent"
        }
        val articles = articleService.filterArticles(Language.from(lang),tagName=tagName)

        model.addAttribute("articles",articles)
        model.addAttribute("page", "uzbekistan")
        if (lang == "ru")
            return "pages/web/uzbekistan/city/tashkent_ru"
        else {
            return "pages/web/uzbekistan/city/tashkent_uz"
        }
    }




    @GetMapping("/economy")
    fun getEconomy(
        model: Model,
        @PathVariable lang: String
    ): String {
        val tagName = if (lang == "ru") {
            "экономика"
        } else {
            "iqtisodiyot"
        }
        val articles = articleService.filterArticles(Language.from(lang),tagName=tagName)

        model.addAttribute("articles",articles)
        model.addAttribute("page", "uzbekistan")
        if (lang == "ru")
            return "pages/web/uzbekistan/sectors/economy_ru"
        else {
            return "pages/web/uzbekistan/sectors/economy_uz"
        }
    }

//    @GetMapping("/culture")
//    fun getCulture(
//        model: Model,
//        @PathVariable lang: String
//    ): String {
//        val tagName = if (lang == "ru") {
//            "Культура"
//        } else {
//            "madaniyat"
//        }
//        val articles = articleService.filterArticles(Language.from(lang),tagName=tagName)
//
//        model.addAttribute("articles",articles)
//        model.addAttribute("page", "uzbekistan")
//        return "pages/web/uzbekistan/sectors/culture"
//    }

//    @GetMapping("/sport")
//    fun getSport(model: Model, @PathVariable lang: String): String {
//        val tagName = if (lang == "ru") {
//            "спорт"
//        } else {
//            "sport"
//        }
//        val articles = articleService.filterArticles(Language.from(lang),tagName=tagName)
//
//        model.addAttribute("articles",articles)
//        model.addAttribute("page", "uzbekistan")
//        return "pages/web/uzbekistan/sectors/sport"
//    }
    @GetMapping("/food")
    fun getFood(model: Model, @PathVariable lang: String): String {

        if (lang == "ru")
            return "pages/web/uzbekistan/sectors/food_ru"
        else {
            return "pages/web/uzbekistan/sectors/food_uz"
        }
    }
    @GetMapping("/persons")
    fun getProfiles( @PathVariable lang: String,
                    @ModelAttribute("filter") filter: PersonFilterRequest,
                    model: Model): String {
        val language = Language.from(lang)

        addEnums(model)

        val cards = service.search(filter, language)
        model.addAttribute("persons", cards)
        model.addAttribute("filter", filter)
        return "pages/web/uzbekistan/sectors/persons"
    }


    private fun addEnums(model: Model) {
        model.addAttribute("types", PersonType.entries)
        model.addAttribute("places", City.entries)
        model.addAttribute("sportTypes", SportType.entries)
        model.addAttribute("sportStatuses", SportStatus.entries)
        model.addAttribute("zodiacs", Zodiac.entries)

        model.addAttribute("aliveFilters", AliveFilter.entries)
        model.addAttribute("seasons", Season.entries)
    }
}