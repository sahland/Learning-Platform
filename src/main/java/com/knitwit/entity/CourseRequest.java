package com.knitwit.entity;

import com.knitwit.enums.RequestType;
import com.knitwit.enums.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "course_request")
public class CourseRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "inspector_id", referencedColumnName = "id")
    private User inspector;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type")
    private RequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Column(name = "comment")
    private String comment;
}