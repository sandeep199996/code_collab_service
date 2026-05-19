package com.mentorplatform.Backend.entity;
import jakarta.persistence.*;
import lombok.Data;
import com.mentorplatform.Backend.entity.Role;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    //role can be "MENTOR" or "MENTEE"
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // "MENTOR" or "MENTEE"
    @Column(columnDefinition ="TEXT")
    private String bio;

    private double hourlyRate; // Applicable for mentors

}
