package com.rksdev.personallearningos.learning.model;

import com.rksdev.personallearningos.shared.model.Auditable;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "learning_modules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "path", callSuper = true)
@EqualsAndHashCode(exclude = "path", callSuper = true)
public class LearningModuleEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "path_id", nullable = false)
    private LearningPathEntity path;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<LearningTopicEntity> topics = new ArrayList<>();
}
