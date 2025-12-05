package com.stock.stockmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stock.stockmanager.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"})
})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    @Email
    @NotBlank
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;  // Role enum: ADMIN, USER

    @Column(nullable = false)
    private boolean active =true;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Override
    public Collection <? extends GrantedAuthority> getAuthorities(){
        return List.of(()-> "ROLE_"+role.name());
    }
    @Override
    public String getUsername (){
        return  email;
    }

    @Override
    public boolean isAccountNonExpired() { return active; }

    @Override
    public boolean isAccountNonLocked() { return active; }

    @Override
    public boolean isCredentialsNonExpired() { return active; }

    @Override
    public boolean isEnabled() { return active; }

}
