package org.vd.vlogdeputatarb.controller.account

import org.springframework.core.io.FileSystemResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.vd.vlogdeputatarb.service.FileService

@RestController
@RequestMapping("/uploads")
class FileUploadController(private val fileService: FileService) {

    // ------------------ UPLOAD ------------------
    @PostMapping("/images")
    fun uploadImage(@RequestParam("image") file: MultipartFile): Map<String, String> {
        val fileName = fileService.saveImage(file)
        return mapOf("filename" to fileName)
    }
//
//    @PostMapping("/videos")
//    fun uploadVideo(@RequestParam("video") file: MultipartFile): Map<String, String> {
//        val fileName = fileService.saveVideo(file)
//        return mapOf("url" to "/uploads/videos/$fileName")
//    }

    // ------------------ GET ------------------
    @GetMapping("/images/{fileName:.+}")
    fun serveImage(@PathVariable fileName: String): ResponseEntity<FileSystemResource> {
        val resourceWithType = fileService.loadImage(fileName)
        return if (resourceWithType != null) {
            ResponseEntity.ok().contentType(resourceWithType.second).body(resourceWithType.first)
        } else {
            ResponseEntity.notFound().build() // безопасно
        }
    }

//    @GetMapping("/videos/{fileName:.+}")
//    fun serveVideo(@PathVariable fileName: String): ResponseEntity<FileSystemResource> {
//        val resourceWithType = fileService.loadVideo(fileName) ?: return ResponseEntity.notFound().build()
//        return ResponseEntity.ok().contentType(resourceWithType.second).body(resourceWithType.first)
//    }

    // ------------------ DELETE ------------------
    @DeleteMapping("/images/{fileName:.+}")
    fun deleteImage(@PathVariable fileName: String): ResponseEntity<Void> {
        return if (fileService.deleteImage(fileName)) ResponseEntity.ok().build()
        else ResponseEntity.notFound().build()
    }

//    @DeleteMapping("/videos/{fileName:.+}")
//    fun deleteVideo(@PathVariable fileName: String): ResponseEntity<Void> {
//        return if (fileService.deleteVideo(fileName)) ResponseEntity.ok().build()
//        else ResponseEntity.notFound().build()
//    }
}