package com.knitwit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Schema(description = "Сущность медиа файла")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "media_file")
public class MediaFile {
    @Schema(description = "ID медиа файла", example = "145")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private int fileId;

    @Schema(description = "Ключ файла", example = "9cf5e6ed585bdc3c46d064d37b5107f3")
    @Column(name = "file_key")
    private String fileKey;

    @Schema(description = "Название файла", example = "схема.png")
    @Column(name = "file_name")
    private String fileName;

    @Schema(description = "Размер файла", example = "1024")
    @Column(name = "file_size")
    private Long fileSize;

    @Schema(description = "Тип медиа файла", example = "Image")
    @Column(name = "file_type")
    private String fileType;

    @Schema(description = "Дата и время загрузки файла", example = "2024-05-14 18:38:07")
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Schema(description = "Раздел курса в котором находится файл")
    @JsonIgnore
    @ManyToMany(mappedBy = "mediaFiles")
    private Set<CourseSection> sections = new HashSet<>();
}
