package com.knitwit.service;

import com.knitwit.model.Tag;
import com.knitwit.repository.TagRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Schema(description = "Сервис для работы с тегами")
@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    public Tag createTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Transactional
    public void deleteTag(int tagId) {
        tagRepository.deleteById(tagId);
    }

    @Transactional
    public Tag updateTag(int tagId, Tag updatedTag) {
        Optional<Tag> optionalTag = tagRepository.findById(tagId);
        if (optionalTag.isPresent()) {
            Tag tag = optionalTag.get();
            tag.setTagName(updatedTag.getTagName());
            return tagRepository.save(tag);
        } else {
            throw new IllegalArgumentException("Тег не найден по id: " + tagId);
        }
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public Tag getTagByName(String tagName) {
        List<Tag> tags = tagRepository.findByTagName(tagName);
        if (tags.isEmpty()) {
            return null;
        }
        return tags.get(0);
    }


    public Tag getTagById(int tagId) {
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("Тег не найден по id: " + tagId));
    }
}
