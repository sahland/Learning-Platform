package com.knitwit.service;

import com.knitwit.model.Tag;
import com.knitwit.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {
    private TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    //создание тега
    @Transactional
    public Tag createTags(Tag tag) {
        return tagRepository.save(tag);
    }

    //удаление тега
    @Transactional
    public void deleteTag(int tagId) {
        tagRepository.deleteById(tagId);
    }

    //редактирование тега
    @Transactional
    public Tag updateTag(int tagId, Tag updatedTag) {
        Optional<Tag> optionalTag = tagRepository.findById(tagId);
        if (optionalTag.isPresent()) {
            updatedTag.setTagId(tagId);
            return tagRepository.save(updatedTag);
        } else {
            throw new IllegalArgumentException("Tag not found with id: " + tagId);
        }
    }

    //получения списка всех тегов
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    //получение тега по названию
    public Tag getTagByName(String tagName) {
        return tagRepository.findByTagName(tagName);
    }

    //получение тега по id
    public Tag getTagById(int tagId) {
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found with id: " + tagId));
    }
}
