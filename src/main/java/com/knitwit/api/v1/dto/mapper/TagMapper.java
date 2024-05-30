package com.knitwit.api.v1.dto.mapper;

import com.knitwit.api.v1.dto.request.TagRequest;
import com.knitwit.api.v1.dto.response.TagResponse;
import com.knitwit.model.Tag;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TagMapper {
    public Tag toEntity(TagRequest tagRequest) {
        Tag tag = new Tag();
        tag.setTagName(tagRequest.getTagName());
        return tag;
    }

    public TagResponse toResponse(Tag tag) {
        TagResponse response = new TagResponse();
        response.setTagId(tag.getTagId());
        response.setTagName(tag.getTagName());
        return response;
    }

    public List<TagResponse> toResponseList(List<Tag> tags) {
        return tags.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
