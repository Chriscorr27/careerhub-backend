package com.chrion.careerhub.security;

import com.chrion.careerhub.common.model.FileMetadata;
import com.chrion.careerhub.common.model.FileType;
import com.chrion.careerhub.common.model.UploadStatus;
import com.chrion.careerhub.common.repository.FileMetadataRepository;
import com.chrion.careerhub.integration.BaseIntegrationTest;
import com.chrion.careerhub.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;


@SpringBootTest
public class SecurityFileAccessTest extends BaseIntegrationTest {
    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    private User mockUserB;
    private User mockHrUser;

    private FileMetadata fileMetadata;

    @BeforeEach
    void setup() {

        mockUserB = new User();
        mockUserB.setEmail("file-owner@example.com");
        mockUserB.setPasswordHash(passwordEncoder.encode(password));

        mockUserB = userRepository.save(mockUserB);

        mockHrUser = new User();
        mockHrUser.setEmail("file-hr@example.com");
        mockHrUser.setRole(User.Role.HR);
        mockHrUser.setPasswordHash(passwordEncoder.encode(password));

        mockHrUser = userRepository.save(mockHrUser);

        fileMetadata = FileMetadata.builder()
                .userId(mockUserB.getId())
                .s3Key("test/user/file.pdf")
                .originalFilename("resume.pdf")
                .contentType("application/pdf")
                .fileSize(1024L)
                .fileType(FileType.RESUME)
                .uploadedAt(Instant.now())
                .uploadStatus(UploadStatus.UPLOADED)
                .build();

        fileMetadata = fileMetadataRepository.save(fileMetadata);
    }

    @AfterEach
    void tearDown() {
        fileMetadataRepository.delete(fileMetadata);
        userRepository.delete(mockUserB);
        userRepository.delete(mockHrUser);
    }

    @Test
    void userCannotAccessAnotherUsersFile() throws Exception {

        mockMvc.perform(
            get("/api/v1/files/" + fileMetadata.getId())
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + jwtToken
                )
        )
        .andExpect(status().isNotFound());
    }

    @Test
    void userCanAccessOwnFile() throws Exception {

        String userBToken = jwtService.generateAccessToken(mockUserB);

        mockMvc.perform(
            get("/api/v1/files/" + fileMetadata.getId())
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + userBToken
                )
        )
        .andExpect(status().isOk());
    }

    @Test
    void hrUserCanAccessWatermarkFile() throws Exception {

        String hrUserToken = jwtService.generateAccessToken(mockHrUser);

        mockMvc.perform(
                        get("/api/v1/files/" + fileMetadata.getId())
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + hrUserToken
                                )
                )
                .andExpect(status().isOk());
    }
}
