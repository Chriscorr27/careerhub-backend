package com.chrion.careerhub.common.validator;

import com.chrion.careerhub.common.exception.CustomException;
import com.chrion.careerhub.common.model.FileMetadata;
import com.chrion.careerhub.common.model.FileType;
import com.chrion.careerhub.common.model.ValidationConfiguration;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class FileValidator {

    private final Map<FileType, ValidationConfiguration> validationConfigMap;

    public FileValidator(){
        validationConfigMap = new HashMap<>();

        // Resume Config
        validationConfigMap.put(FileType.RESUME, new ValidationConfiguration(
                5 * 1024 * 1024,
                Set.of(
                        "application/pdf",
                        "text/plain"
                ),
                Set.of(".pdf", ".txt"),
                "File size cannot exceed 5 MB"
        ));

        // PROFILE Config
        validationConfigMap.put(FileType.PROFILE, new ValidationConfiguration(
                2 * 1024 * 1024,
                Set.of(
                        "image/jpeg",
                        "image/png",
                        "image/webp"
                ),
                Set.of(".jpg", ".jpeg", ".png", ".webp"),
                "File size cannot exceed 2 MB"
        ));

        // DOCUMENT Config
        validationConfigMap.put(FileType.DOCUMENT, new ValidationConfiguration(
                5 * 1024 * 1024,
                Set.of(
                        "application/pdf",
                        "text/plain"
                ),
                Set.of(".pdf", ".txt"),
                "File size cannot exceed 5 MB"
        ));
    }

    private ValidationConfiguration getValidationConfiguration(FileType fileType) {
        if(validationConfigMap.containsKey(fileType)) {
            return validationConfigMap.get(fileType);
        }else{
            throw new CustomException("Invalid FileTye: "+fileType);
        }
    }
    public void validateFileMetaData(FileMetadata fileMetadata){
        ValidationConfiguration validationConfiguration = getValidationConfiguration(fileMetadata.getFileType());


        if (fileMetadata.getFileSize() > validationConfiguration.MAX_FILE_SIZE()) {
            throw new CustomException(validationConfiguration.FILE_SIZE_ERROR(), HttpStatus.BAD_REQUEST);
        }

        String lowerCaseFilename = getLowerCaseFilename(
                fileMetadata.getOriginalFilename(),
                fileMetadata.getContentType(),
                validationConfiguration
        );

        boolean validExtension = validationConfiguration.ALLOWED_EXTENSIONS().stream()
                .anyMatch(lowerCaseFilename::endsWith);

        if (!validExtension) {
            throw new CustomException(
                    "Unsupported file extension ALLOWED_EXTENSIONS: "+validationConfiguration.ALLOWED_EXTENSIONS(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
    public void validate(MultipartFile file, FileType fileType) throws CustomException {
        ValidationConfiguration validationConfiguration = getValidationConfiguration(fileType);

        if (file == null || file.isEmpty()) {
            throw new CustomException("File cannot be empty", HttpStatus.BAD_REQUEST);
        }

        if (file.getSize() > validationConfiguration.MAX_FILE_SIZE()) {
            throw new CustomException(validationConfiguration.FILE_SIZE_ERROR(), HttpStatus.BAD_REQUEST);
        }

        String lowerCaseFilename = getLowerCaseFilename(
                file.getOriginalFilename(),
                file.getContentType(),
                validationConfiguration
        );

        boolean validExtension = validationConfiguration.ALLOWED_EXTENSIONS().stream()
                .anyMatch(lowerCaseFilename::endsWith);

        if (!validExtension) {
            throw new CustomException(
                    "Unsupported file extension ALLOWED_EXTENSIONS: "+validationConfiguration.ALLOWED_EXTENSIONS(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    private static @NonNull String getLowerCaseFilename(
            String filename,
            String contentType,
            ValidationConfiguration validationConfiguration
    ) {

        if (contentType == null || !validationConfiguration.ALLOWED_CONTENT_TYPES().contains(contentType)) {
            throw new CustomException(
                    "Unsupported file type ALLOWED_CONTENT_TYPES: "+validationConfiguration.ALLOWED_CONTENT_TYPES(),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (filename == null || filename.isBlank()) {
            throw new CustomException("Filename is required", HttpStatus.BAD_REQUEST);
        }

        return filename.toLowerCase();
    }
}
