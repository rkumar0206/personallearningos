package com.rksdev.personallearningos.learning.model;

import com.rksdev.personallearningos.learning.model.enums.TopicStatus;
import com.rksdev.personallearningos.shared.model.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "learning_topics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "module", callSuper = true)
@EqualsAndHashCode(exclude = "module", callSuper = true)
public class LearningTopicEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private LearningModuleEntity module;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private TopicStatus status = TopicStatus.NOT_STARTED;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LearningResourceEntity> resources = new ArrayList<>();
}