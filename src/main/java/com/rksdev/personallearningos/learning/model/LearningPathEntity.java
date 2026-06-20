package com.rksdev.personallearningos.learning.model;

import com.rksdev.personallearningos.learning.model.enums.PathStatus;
import com.rksdev.personallearningos.shared.model.Auditable;
import com.rksdev.personallearningos.user.model.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "learning_paths")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PathStatus status = PathStatus.PLANNED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "path", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<LearningModuleEntity> modules = new ArrayList<>();
}
