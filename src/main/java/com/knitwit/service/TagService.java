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

    @Autowired
    private TagRepository tagRepository;

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
            throw new IllegalArgumentException("Tag not found with id: " + tagId);
        }
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public Tag getTagByName(String tagName) {
        return tagRepository.findByTagName(tagName);
    }

    public Tag getTagById(int tagId) {
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found with id: " + tagId));
    }
}
