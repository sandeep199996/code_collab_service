package com.mentorplatform.Backend.entity;
import jakarta.persistence.*;
import lombok.Data;
import com.mentorplatform.Backend.entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Force the "ROLE_" prefix so Spring Security stops rejecting it!
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));


        // Note: If your role in the DB is saved as a String (not an Enum),
        // change `this.role.name()` to just `this.role`
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
