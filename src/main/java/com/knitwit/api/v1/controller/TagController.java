package com.knitwit.api.v1.controller;

import com.knitwit.api.v1.dto.request.TagRequest;
import com.knitwit.api.v1.dto.response.TagResponse;
import com.knitwit.api.v1.dto.mapper.TagMapper;
import com.knitwit.model.Tag;
import com.knitwit.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;

    @Operation(summary = "Создание тега (ADMIN)")
    @Secured("ROLE_ADMIN")
    @PostMapping("/create")
    public ResponseEntity<TagResponse> createTag(@RequestBody TagRequest tagRequest) {
        Tag tag = tagMapper.toEntity(tagRequest);
        Tag createdTag = tagService.createTag(tag);
        TagResponse response = tagMapper.toResponse(createdTag);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Редактирование тега (ADMIN)")
    @Secured("ROLE_ADMIN")
    @PutMapping("/{tagId}")
    public ResponseEntity<TagResponse> updateTag(@PathVariable int tagId, @RequestBody TagRequest updatedTagRequest) {
        Tag updatedTag = tagMapper.toEntity(updatedTagRequest);
        Tag tag = tagService.updateTag(tagId, updatedTag);
        TagResponse response = tagMapper.toResponse(tag);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получение списка всех тегов")
    @GetMapping("/all")
    public ResponseEntity<List<TagResponse>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        List<TagResponse> responses = tagMapper.toResponseList(tags);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Получение тега по его ID (USER)")
    @GetMapping("/{tagId}")
    @Secured("ROLE_USER")
    public ResponseEntity<TagResponse> getTagById(@PathVariable int tagId) {
        Tag tag = tagService.getTagById(tagId);
        TagResponse response = tagMapper.toResponse(tag);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получение тега по его названию (USER)")
    @GetMapping("/name/{tagName}")
    @Secured("ROLE_USER")
    public ResponseEntity<TagResponse> getTagByName(@PathVariable String tagName) {
        Tag tag = tagService.getTagByName(tagName);
        TagResponse response = tagMapper.toResponse(tag);
        return ResponseEntity.ok(response);
    }
}