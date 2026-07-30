package com.rksdev.personallearningos.learning.model;

import com.rksdev.personallearningos.learning.model.enums.ResourceType;
import com.rksdev.personallearningos.shared.model.Auditable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "learning_resources")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "topic", callSuper = true)
@EqualsAndHashCode(exclude = "topic", callSuper = true)
public class LearningResourceEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private LearningTopicEntity topic;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ResourceType type;

    @Column(name = "url_description", nullable = false, columnDefinition = "TEXT")
    private String urlDescription;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "title", nullable = false, length = 200)
    private String title;
}
