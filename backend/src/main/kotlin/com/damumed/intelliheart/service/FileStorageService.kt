package com.damumed.intelliheart.service

import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.UUID

@Service
class FileStorageService {
    private val uploadDir: Path = Paths.get("uploads/analyses").toAbsolutePath().normalize()

    init {
        try {
            Files.createDirectories(uploadDir)
        } catch (ex: Exception) {
            throw RuntimeException("Could not create the directory where the uploaded files will be stored.", ex)
        }
    }

    fun storeFile(file: MultipartFile): String {
        val originalFilename = file.originalFilename ?: "unknown.pdf"
        val fileExtension = originalFilename.substringAfterLast('.', "pdf")
        val fileName = "${UUID.randomUUID()}.$fileExtension"
        
        try {
            if (fileName.contains("..")) {
                throw RuntimeException("Sorry! Filename contains invalid path sequence $fileName")
            }
            
            val targetLocation = uploadDir.resolve(fileName)
            Files.copy(file.inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)
            
            return fileName
        } catch (ex: Exception) {
            throw RuntimeException("Could not store file $fileName. Please try again!", ex)
        }
    }

    fun loadFileAsResource(fileName: String): Resource {
        try {
            val filePath = uploadDir.resolve(fileName).normalize()
            val resource = UrlResource(filePath.toUri())
            if (resource.exists()) {
                return resource
            } else {
                throw RuntimeException("File not found $fileName")
            }
        } catch (ex: Exception) {
            throw RuntimeException("File not found $fileName", ex)
        }
    }
}
