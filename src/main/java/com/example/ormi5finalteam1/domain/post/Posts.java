package com.example.ormi5finalteam1.domain.post;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "posts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Posts {

    @Id
    @Column(name = "posts_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column
    private Integer view_count;

    @Column
    private Timestamp created_at;

    @Column
    private Timestamp updated_at;

    @Column
    private Timestamp deleted_at;

}
