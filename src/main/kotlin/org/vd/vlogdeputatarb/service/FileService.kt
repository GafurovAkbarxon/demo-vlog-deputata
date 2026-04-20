package org.vd.vlogdeputatarb.service

import org.springframework.core.io.FileSystemResource
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.vd.vlogdeputatarb.util.util.properties.StorageProperties
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.UUID

@Service
class FileService   (
    private val storageProperties: StorageProperties
) {

    private val storageRoot: Path by lazy {
        Paths.get(storageProperties.root).toAbsolutePath().normalize()
    }

    private val imageDir: Path by lazy {
        storageRoot.resolve("uploads").resolve("images")
    }

    private val videoDir: Path by lazy {
        storageRoot.resolve("uploads").resolve("videos")
    }
//    private val imageDir: Path = Paths.get("C:/vd/upload/images")
//    private val videoDir: Path = Paths.get("C:/vd/upload/videos")

    init {
        Files.createDirectories(imageDir)
        Files.createDirectories(videoDir)
    }

     fun saveImage(file: MultipartFile): String {
        val extension = when (file.contentType) {
            "image/jpeg" -> "jpg"
            "image/png"  -> "png"
            "image/webp" -> "webp"
            else         -> "bin"
        }

        val fileName = "${UUID.randomUUID()}.$extension"
        val target = imageDir.resolve(fileName)
        file.transferTo(target)
        return fileName

    }

     fun downloadAndSaveImage(imageUrl: String): String {
        val url = URL(imageUrl)
        val connection = url.openConnection()
        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        val contentType = connection.contentType ?: "image/jpeg"
        if (!contentType.startsWith("image/")) {
            throw IllegalArgumentException("URL does not point to an image")
        }
        val extension = when (contentType) {
            "image/jpeg" -> "jpg"
            "image/png"  -> "png"
            "image/webp" -> "webp"
            "image/gif"  -> "gif"
            else         -> "jpg"
        }

        val fileName = "${UUID.randomUUID()}.$extension"
        val target = imageDir.resolve(fileName)

        url.openStream().use { input ->
            Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING)
        }

        return fileName
    }

     fun saveVideo(file: MultipartFile): String {
        val fileName = UUID.randomUUID().toString() + "_" + file.originalFilename
        val target = videoDir.resolve(fileName)
        file.transferTo(target)
        return fileName
    }

    // ------------------------- LOAD -------------------------
     fun loadImage(fileName: String): Pair<FileSystemResource, MediaType>? {


        val file = imageDir.resolve(fileName).toFile()
        if (!file.exists() || !file.isFile) {
            println("⚠️ loadImage: файл не найден или не является файлом: $fileName")
            return null
        }

        val resource = FileSystemResource(file)
        val contentType = Files.probeContentType(file.toPath())
            ?.let { MediaType.parseMediaType(it) }
            ?: MediaType.APPLICATION_OCTET_STREAM

        return resource to contentType
    }

     fun loadVideo(fileName: String): Pair<FileSystemResource, MediaType>? {
        val file = videoDir.resolve(fileName).toFile()
        if (!file.exists()) return null
        val resource = FileSystemResource(file)
        val contentType = Files.probeContentType(file.toPath())?.let { MediaType.parseMediaType(it) }
            ?: MediaType.APPLICATION_OCTET_STREAM
        return resource to contentType
    }

    // ------------------------- DELETE -------------------------
     fun deleteImage(fileName: String): Boolean {
        val file = imageDir.resolve(fileName).toFile()
        return file.exists() && file.delete()
    }

     fun deleteVideo(fileName: String): Boolean {
        val file = videoDir.resolve(fileName).toFile()
        return file.exists() && file.delete()
    }

}