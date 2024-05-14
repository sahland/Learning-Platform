package com.knitwit.service;

import com.knitwit.model.MediaFile;
import com.knitwit.repository.MediaFileRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Schema(description = "Сервис для работы с фалйами")
@Service
public class MediaFileService {

    private final MediaFileRepository mediaFileRepository;

    @Autowired
    public MediaFileService(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    // Метод для сохранения медиа-файла
    public MediaFile saveMediaFile(MultipartFile file) throws IOException {

        // Заменить на логику для загрузки файла в S3 и получения его ключа
        // (AWS SDK для загрузки файла в S3 и получения его URL, создать объект MediaFile и сохранить его в репозитории)
        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();
        long fileSize = file.getSize();
        // Создаем объект MediaFile и сохраняем его в репозитории
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileName(fileName);
        mediaFile.setFileType(fileType);
        mediaFile.setFileSize(fileSize);
        return mediaFileRepository.save(mediaFile);
    }

    // Метод для получения всех медиа-файлов
    public List<MediaFile> getAllMediaFiles() {
        return mediaFileRepository.findAll();
    }

    // Метод для получения медиа-файла по его ID
    public MediaFile getMediaFileById(int fileId) {
        return mediaFileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("Media file not found with ID: " + fileId));
    }

    // Метод для удаления медиа-файла по его ID
    public void deleteMediaFileById(int fileId) {
        mediaFileRepository.deleteById(fileId);
    }
}
