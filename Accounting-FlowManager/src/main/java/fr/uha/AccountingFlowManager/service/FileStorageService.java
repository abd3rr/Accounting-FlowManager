package fr.uha.AccountingFlowManager.service;

import jakarta.annotation.PostConstruct;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {
    @Value("${filestorage.base-dir:uploads/}")
    private String fileStorageLocation;
    private Path storagePath;

    @PostConstruct
    public void init() {
        storagePath = Paths.get(fileStorageLocation).toAbsolutePath().normalize();
        try {
            FileUtils.forceMkdir(storagePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Could not create the directory for file storage.", e);
        }
    }

    public Path storeFile(MultipartFile multipartFile) {
        String originalFileName = multipartFile.getOriginalFilename();
        assert originalFileName != null;
        String uniqueFileName = generateUniqueFileName(originalFileName);
        Path filePath = storagePath.resolve(uniqueFileName);

        try {
            multipartFile.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + uniqueFileName + ". Please try again.", e);
        }

        return filePath;
    }

    public void deleteFile(String filePath) {
        try {
            FileUtils.forceDelete(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file " + filePath + ". Please try again later.", e);
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        String baseName = FilenameUtils.getBaseName(originalFileName);
        String fileExtension = FilenameUtils.getExtension(originalFileName);

        // Add a timestamp or a UUID to the file name
        String uniqueIdentifier = String.valueOf(System.currentTimeMillis());

        return baseName + "_" + uniqueIdentifier + "." + fileExtension;
    }
}