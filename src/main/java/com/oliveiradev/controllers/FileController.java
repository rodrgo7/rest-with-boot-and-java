package com.oliveiradev.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.oliveiradev.data.vo.v1.UploadFileResponseVO;
import com.oliveiradev.services.FileStorageService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "File Endpoint")
@RestController
@RequestMapping("/api/file/v1")
public class FileController {
    private Logger logger = Logger.getLogger(FileController.class.getName());

    @Autowired
    private FileStorageService service;

    @PostMapping("/uploadFile")
    public UploadFileResponseVO uploadFile(@RequestParam("file") MultipartFile file) {
        logger.info("Storing file to disk");

        var filename = service.storeFile(file);

        String fileDownaloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/file/v1/downloadFile/")
            .path(filename)
            .toUriString();
        
        return new UploadFileResponseVO(filename, fileDownaloadUri, file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadFiles")
    public List<UploadFileResponseVO> uploadFMultiplesile(@RequestParam("files") MultipartFile[] files) {
        logger.info("Storing files   to disk");

        return Arrays.asList(files)
            .stream()
            .map(file -> uploadFile(file))
            .collect(Collectors.toList());
    }

    @GetMapping("/downloadFile/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename, HttpServletRequest request) {
        logger.info("Reading file on disk");

        Resource resource = service.loadFileAsResource(filename);
        String contentTtype = "";

        try {
            contentTtype = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (Exception e) {
            logger.info("Could not determine file type!");
        }
        
        if (contentTtype.isBlank()) contentTtype = "application/octet-stream";

            return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentTtype))
            .header(
                HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\""
                )
            .body(resource);
    }

    @GetMapping("/downloadFiles")
    public List<UploadFileResponseVO> downloadFMultiplesile(@RequestParam("files") MultipartFile[] files) {
        logger.info("Storing files   to disk");

        return Arrays.asList(files)
            .stream()
            .map(file -> uploadFile(file))
            .collect(Collectors.toList());
    }
}