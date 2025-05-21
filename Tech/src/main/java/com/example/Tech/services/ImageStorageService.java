package com.example.Tech.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageStorageService {


    @Value("${upload.dir:uploads}") // default to "uploads" folder
    private String uploadDir;

    public List<String> storeImages(List<MultipartFile> files) {
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            try {
                // Ensure the uploads directory exists
                Path uploadPath = Paths.get(uploadDir);
                if (Files.notExists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Generate a unique filename
                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename != null && originalFilename.contains(".")
                        ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                        : "";
                String uniqueFilename = UUID.randomUUID() + extension;

                // Save the file locally
                Path filePath = uploadPath.resolve(uniqueFilename);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Construct the accessible URL (or path you return to frontend)
                String fileUrl = "/uploads/" + uniqueFilename;
                imageUrls.add(fileUrl);

            } catch (IOException e) {
                throw new RuntimeException("Failed to store image: " + file.getOriginalFilename(), e);
            }
        }

        return imageUrls;
    }
}
