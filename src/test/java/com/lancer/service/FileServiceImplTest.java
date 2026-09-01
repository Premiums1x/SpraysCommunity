package com.lancer.service;

import com.lancer.common.exception.BusinessException;
import com.lancer.service.impl.FileServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileServiceImplTest {

    Path uploadDirectory;

    @BeforeEach
    void createWorkspaceTemporaryDirectory() throws Exception {
        Files.createDirectories(Path.of("target"));
        uploadDirectory = Files.createTempDirectory(Path.of("target"), "test-uploads-");
    }

    @AfterEach
    void removeWorkspaceTemporaryDirectory() throws Exception {
        if (uploadDirectory == null || !Files.exists(uploadDirectory)) {
            return;
        }
        try (var files = Files.walk(uploadDirectory)) {
            files.sorted(java.util.Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (Exception ignored) {
                }
            });
        }
    }

    @Test
    void derivesExtensionFromFileSignatureAndCanDeleteIt() throws Exception {
        FileServiceImpl service = new FileServiceImpl(uploadDirectory.toString());
        byte[] png = new byte[]{(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a, 0, 0, 0, 0};
        MockMultipartFile file = new MockMultipartFile("file", "not-an-image.exe", "application/octet-stream", png);

        String fileName = service.upload(file);

        assertThat(fileName).endsWith(".png");
        assertThat(Files.exists(uploadDirectory.resolve(fileName))).isTrue();
        service.delete(fileName);
        assertThat(Files.exists(uploadDirectory.resolve(fileName))).isFalse();
    }

    @Test
    void rejectsContentThatOnlyPretendsToBeAnImage() {
        FileServiceImpl service = new FileServiceImpl(uploadDirectory.toString());
        MockMultipartFile file = new MockMultipartFile("file", "fake.png", "image/png", "not an image".getBytes());

        assertThatThrownBy(() -> service.upload(file))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("真实");
    }
}
