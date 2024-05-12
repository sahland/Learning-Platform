package com.knitwit.api.v1.controller;

import com.knitwit.model.Tag;
import com.knitwit.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @Operation(summary = "Создание тега")
    @PostMapping("/create")
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        Tag createdTag = tagService.createTag(tag);
        return ResponseEntity.ok(createdTag);
    }

    @Operation(summary = "Удаление тега по его ID")
    @DeleteMapping("/{tagId}")
    public ResponseEntity<?> deleteTag(@PathVariable int tagId) {
        tagService.deleteTag(tagId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Редактирование тега")
    @PutMapping("/{tagId}")
    public ResponseEntity<Tag> updateTag(@PathVariable int tagId, @RequestBody Tag updatedTag) {
        Tag tag = tagService.updateTag(tagId, updatedTag);
        return ResponseEntity.ok(tag);
    }

    @Operation(summary = "Получение списка всех тегов")
    @GetMapping("/all")
    public ResponseEntity<List<Tag>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    @Operation(summary = "Получение тега по его ID")
    @GetMapping("/{tagId}")
    public ResponseEntity<Tag> getTagById(@PathVariable int tagId) {
        Tag tag = tagService.getTagById(tagId);
        return ResponseEntity.ok(tag);
    }

    @Operation(summary = "Получение тега по его названию")
    @GetMapping("/name/{tagName}")
    public ResponseEntity<Tag> getTagByName(@PathVariable String tagName) {
        Tag tag = tagService.getTagByName(tagName);
        return ResponseEntity.ok(tag);
    }
}
