package com.lancer.service.impl;

import com.lancer.common.exception.BusinessException;
import com.lancer.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private static final long MAX_IMAGE_BYTES = 5L * 1024 * 1024;
    private final Path uploadRoot;

    public FileServiceImpl(@Value("${app.upload.path}") String uploadPath) {
        this.uploadRoot = Paths.get(uploadPath).toAbsolutePath().normalize();
    }

    @Override
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "请选择要上传的文件");
        }

        if (file.getSize() > MAX_IMAGE_BYTES) {
            throw new BusinessException(400, "图片不能超过5MB");
        }

        String extension = detectImageExtension(file);
        String newFileName = UUID.randomUUID().toString().replace("-", "") + extension;
        Path target = safeResolve(newFileName);
        Path temporary = safeResolve(newFileName + ".uploading");
        try {
            Files.createDirectories(uploadRoot);
            try (InputStream input = file.getInputStream()) {
                Files.copy(input, temporary, StandardCopyOption.REPLACE_EXISTING);
            }
            try {
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            tryDelete(temporary);
            throw new BusinessException(500, "文件上传失败，请稍后重试");
        }

        return newFileName;
    }

    @Override
    public void delete(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }
        try {
            tryDelete(safeResolve(fileName));
        } catch (BusinessException ignored) {
            // 历史脏数据中的危险路径只忽略，绝不触碰上传目录之外的文件。
        }
    }

    private String detectImageExtension(MultipartFile file) {
        byte[] header;
        try (InputStream input = file.getInputStream()) {
            header = input.readNBytes(12);
        } catch (IOException exception) {
            throw new BusinessException(400, "无法读取上传图片");
        }
        int length = header.length;

        if (length >= 3 && (header[0] & 0xff) == 0xff && (header[1] & 0xff) == 0xd8 && (header[2] & 0xff) == 0xff) {
            return ".jpg";
        }
        if (length >= 8 && matches(header, new byte[]{(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a}, 0)) {
            return ".png";
        }
        if (length >= 6 && (matchesAscii(header, "GIF87a", 0) || matchesAscii(header, "GIF89a", 0))) {
            return ".gif";
        }
        if (length >= 12 && matchesAscii(header, "RIFF", 0) && matchesAscii(header, "WEBP", 8)) {
            return ".webp";
        }
        throw new BusinessException(400, "只支持真实的 JPG、PNG、GIF、WEBP 图片");
    }

    private boolean matchesAscii(byte[] source, String expected, int offset) {
        return matches(source, expected.getBytes(java.nio.charset.StandardCharsets.US_ASCII), offset);
    }

    private boolean matches(byte[] source, byte[] expected, int offset) {
        if (source.length < offset + expected.length) {
            return false;
        }
        for (int index = 0; index < expected.length; index++) {
            if (source[offset + index] != expected[index]) {
                return false;
            }
        }
        return true;
    }

    private Path safeResolve(String fileName) {
        Path resolved = uploadRoot.resolve(fileName).normalize();
        if (!resolved.startsWith(uploadRoot)) {
            throw new BusinessException(400, "文件路径不正确");
        }
        return resolved;
    }

    private void tryDelete(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // 文件清理由后续运维任务兜底，不能让主业务因清理失败回滚。
        }
    }
}
