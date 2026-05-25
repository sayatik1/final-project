package com.example.secureapp.controller;

import com.example.secureapp.service.S3Service;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class FileController {

    private final S3Service s3Service;

    public FileController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping("/")
    public String home() {
        return "Secure App Running";
    }

    @GetMapping("/files")
    @PreAuthorize("hasAnyRole('VIEWER','EDITOR','ADMIN')")
    public List<String> files() {

        return s3Service.listFiles();
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        s3Service.uploadFile(file);

        return ResponseEntity.ok("File uploaded successfully");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin() {

        return "Admin endpoint";
    }
}