package org.vd.vlogdeputatarb.controller.admin

import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.vd.vlogdeputatarb.data.wiki.enums.City
import org.vd.vlogdeputatarb.data.wiki.enums.PersonType
import org.vd.vlogdeputatarb.data.wiki.enums.SportStatus
import org.vd.vlogdeputatarb.data.wiki.enums.SportType
import org.vd.vlogdeputatarb.service.PersonService
import java.time.LocalDate

@PreAuthorize("hasRole('ADMIN')")
@Controller
@RequestMapping("/ru/admin/wiki/persons")
class AdminPersonController(
    private val service: PersonService
) {

    @GetMapping("/create")
    fun createForm(model: Model): String {

        addEnums(model)
        model.addAttribute("dto", PersonRequest())
        model.addAttribute("disableLangSwitch", true);

        return "pages/admin/wiki/create"
    }
    @PostMapping("/create")
    fun create(
        @Valid @ModelAttribute("dto") dto: PersonRequest,
        br: BindingResult,
        model: Model
    ): String {
        addEnums(model)

        if (service.exist(dto.slug))
            br.rejectValue("slug", "person.slug.notUnique", "Slug должен быть уникальным")
        if (dto.file == null || dto.file!!.isEmpty) {
            br.rejectValue("file", "person.file.required", "Картинка личности обязательна")
        }
        validateCrossFields(dto.birthDate, dto.deathDate, dto.type, dto.sportType, dto.sportStatus, br)

        if (br.hasErrors()) {
            model.addAttribute("is_done", false)
            model.addAttribute("message", "Ошибка формы")
            model.addAttribute("disableLangSwitch", true);

            return "pages/admin/wiki/create"
        }

        service.create(dto)


        return  "redirect:/ru/uzbekistan/persons"
    }
    @GetMapping("/{id}/edit")
    fun editForm(@PathVariable id: Long, model: Model): String {
        addEnums(model)
        model.addAttribute("disableLangSwitch", true);

        val p = service.getEntity(id)
        val dto = PersonRequest(
            slug = p.slug,
            ruName = p.ruName,
            uzName = p.uzName,
            alt = p.alt,
            birthDate = p.birthDate,
            deathDate = p.deathDate,
            birthPlace = p.birthPlace,
            type = p.type,
            ruDescription = p.ruDescription,
            uzDescription = p.uzDescription,
            externalUrl = p.externalUrl,
            sportType = p.sportType,
            sportStatus = p.sportStatus
        )

        model.addAttribute("dto", dto)
        model.addAttribute("id", id)

        model.addAttribute("currentImageFilename", p.imageFilename)
        return "pages/admin/wiki/edit"
    }
    @PostMapping("/{id}/edit")
    fun edit(
        @PathVariable id: Long,
        @Valid @ModelAttribute("dto") dto: PersonRequest,
        br: BindingResult,
        model: Model
    ): String {

        addEnums(model)
        model.addAttribute("currentImageFilename", service.getEntity(id).imageFilename)
        model.addAttribute("id", id)
        val taken = service.slugTakenForOther(dto.slug, id)
        if (taken) {
            br.rejectValue("slug", "slug.notUnique", "Slug должен быть уникальным")
        }
        validateCrossFields(dto.birthDate, dto.deathDate, dto.type, dto.sportType, dto.sportStatus, br)

        if (br.hasErrors()) {
            model.addAttribute("is_done", false)
            model.addAttribute("message", "Ошибка формы")
            model.addAttribute("disableLangSwitch", true);

            return "pages/admin/wiki/edit"
        }

        service.update(id,dto)

        return "redirect:/ru/uzbekistan/persons"
    }


    @PostMapping("/{id}/delete")
    fun delete(
        @PathVariable id: Long
    ): String {
        service.delete(id)
        return "redirect:/ru/uzbekistan/persons"
    }




    private fun addEnums(model: Model) {
        model.addAttribute("types", PersonType.entries)
        model.addAttribute("places", City.entries)
        model.addAttribute("sportTypes", SportType.entries)
        model.addAttribute("sportStatuses", SportStatus.entries)
    }
    private fun validateCrossFields(
        birthDate: LocalDate?,
        deathDate: LocalDate?,
        type: PersonType?,
        sportType: SportType?,
        sportStatus: SportStatus?,
        br: BindingResult
    ) {

        if (birthDate != null && deathDate != null && deathDate.isBefore(birthDate)) {
            br.rejectValue("deathDate", "deathDate.beforeBirth", "Дата смерти не может быть раньше даты рождения")
        }

        if (type == PersonType.ATHLETE) {
            if (sportType == null) br.rejectValue("sportType", "sportType.required", "Для спортсмена обязателен вид спорта")
            if (sportStatus == null) br.rejectValue("sportStatus", "sportStatus.required", "Для спортсмена обязателен статус карьеры")
        } else {
            if (sportType != null) br.rejectValue("sportType", "sportType.forbidden", "Вид спорта можно указывать только для типа 'Спортсмен'")
            if (sportStatus != null) br.rejectValue("sportStatus", "sportStatus.forbidden", "Статус карьеры можно указывать только для типа 'Спортсмен'")
        }
    }
}