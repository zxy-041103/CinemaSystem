package com.cinema.controller;

import com.cinema.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${file.upload.path}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            file.transferTo(new File(dir.getAbsolutePath() + File.separator + fileName));
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }

        String url = "/uploads/" + fileName;
        return Result.success(url);
    }
}
