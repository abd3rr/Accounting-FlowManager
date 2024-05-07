package fr.uha.AccountingFlowManager.service;

import jakarta.annotation.PostConstruct;
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
        Path filePath = storagePath.resolve(originalFileName);

        try {
            multipartFile.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again.", e);
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
}
