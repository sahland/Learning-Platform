package com.knitwit.api.v1.controller;

import com.knitwit.model.MediaFile;
import com.knitwit.service.MediaFileService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/media")
public class MediaFileController {

    @Autowired
    private MediaFileService mediaFileService;

    @Operation(summary = "Сохранение медиа-файла")
    @PostMapping("/upload")
    public ResponseEntity<MediaFile> uploadMediaFile(@RequestParam("file") MultipartFile file) {
        try {
            MediaFile savedMediaFile = mediaFileService.saveMediaFile(file);
            return ResponseEntity.ok(savedMediaFile);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Получение всех медиа-файлов")
    @GetMapping("/all")
    public ResponseEntity<List<MediaFile>> getAllMediaFiles() {
        List<MediaFile> mediaFiles = mediaFileService.getAllMediaFiles();
        return ResponseEntity.ok(mediaFiles);
    }

    @Operation(summary = "Получение медиа-файла по его ID")
    @GetMapping("/{fileId}")
    public ResponseEntity<MediaFile> getMediaFileById(@PathVariable int fileId) {
        MediaFile mediaFile = mediaFileService.getMediaFileById(fileId);
        return ResponseEntity.ok(mediaFile);
    }

    @Operation(summary = "Удаление медиа-файла по его ID")
    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteMediaFileById(@PathVariable int fileId) {
        mediaFileService.deleteMediaFileById(fileId);
        return ResponseEntity.ok().build();
    }
}
