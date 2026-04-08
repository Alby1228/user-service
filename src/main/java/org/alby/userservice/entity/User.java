package org.alby.userservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_user", indexes = {
        @Index(name = "idx_username", columnList = "username", unique = true),
        @Index(name = "idx_phone", columnList = "phone", unique = true)
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64, unique = true)
    private String username;

    @Column(nullable = false, length = 128)
    private String password;

    @Column(length = 20, unique = true)
    private String phone;

    @Column(length = 128)
    private String email;

    @Column(length = 64)
    private String nickname;

    @Column(length = 512)
    private String avatar;

    @Column(nullable = false)
    private Integer status;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
}
