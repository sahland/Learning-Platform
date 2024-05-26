package com.knitwit.api.v1.controller;

import com.knitwit.model.Tag;
import com.knitwit.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
@SecurityRequirement(name = "Keycloak")
public class TagController {

    private final TagService tagService;

    @Operation(summary = "Создание тега")
    @PostMapping("/create")
    //admin
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        Tag createdTag = tagService.createTag(tag);
        return ResponseEntity.ok(createdTag);
    }

    @Operation(summary = "Удаление тега по его ID")
    @DeleteMapping("/{tagId}")
    //admin
    public ResponseEntity<?> deleteTag(@PathVariable int tagId) {
        tagService.deleteTag(tagId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Редактирование тега")
    @PutMapping("/{tagId}")
    //admin
    public ResponseEntity<Tag> updateTag(@PathVariable int tagId, @RequestBody Tag updatedTag) {
        Tag tag = tagService.updateTag(tagId, updatedTag);
        return ResponseEntity.ok(tag);
    }

    @Operation(summary = "Получение списка всех тегов")
    @GetMapping("/all")
    //user
    public ResponseEntity<List<Tag>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    @Operation(summary = "Получение тега по его ID")
    @GetMapping("/{tagId}")
    //admin
    public ResponseEntity<Tag> getTagById(@PathVariable int tagId) {
        Tag tag = tagService.getTagById(tagId);
        return ResponseEntity.ok(tag);
    }

    @Operation(summary = "Получение тега по его названию")
    @GetMapping("/name/{tagName}")
    //admin
    public ResponseEntity<Tag> getTagByName(@PathVariable String tagName) {
        Tag tag = tagService.getTagByName(tagName);
        return ResponseEntity.ok(tag);
    }
}
