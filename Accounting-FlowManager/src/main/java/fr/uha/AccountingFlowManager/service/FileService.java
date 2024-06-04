package fr.uha.AccountingFlowManager.service;
import fr.uha.AccountingFlowManager.model.File;
import org.apache.commons.io.FilenameUtils;

import fr.uha.AccountingFlowManager.repository.FileRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;
    @Value("${file.max-size}")
    private long MAX_FILE_SIZE;
    @Value("${file.accepted-extensions}")

    private String[] ACCEPTED_EXTENSIONS;

    public FileService(FileRepository fileRepository, FileStorageService fileStorageService) {
        this.fileRepository = fileRepository;
        this.fileStorageService = fileStorageService;
    }


    public File storeFile(@Valid MultipartFile multipartFile) {
        String originalFileName = multipartFile.getOriginalFilename();
        if (originalFileName == null || originalFileName.contains("..")) {
            throw new IllegalArgumentException("Invalid file name.");
        }

        String fileExtension = FilenameUtils.getExtension(originalFileName);

        List<String> acceptedExtensionsList = Arrays.asList(ACCEPTED_EXTENSIONS);
        if (!acceptedExtensionsList.contains(fileExtension)) {
            throw new IllegalArgumentException("Invalid file type.");
        }

        if (multipartFile.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds limit.");
        }

        Path filePath = fileStorageService.storeFile(multipartFile);

        String fileName = filePath.getFileName().toString();
        String contentType = multipartFile.getContentType();
        long fileSize = multipartFile.getSize();

        File file = new File();
        file.setFileName(fileName);
        file.setFilePath(filePath.toString());
        file.setSize(fileSize);
        file.setContentType(contentType);
        file.setUploadDateTime(LocalDateTime.now());

        return fileRepository.save(file);
    }


}
