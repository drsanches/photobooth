package ru.drsanches.photobooth.auth.data.model;

import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.drsanches.photobooth.common.token.data.Role;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Collection;
import java.util.List;

//TODO: Is it necessary to implement UserDetails?
@Setter
@ToString
@Entity
@Table(name="user_auth")
public class UserAuth implements UserDetails {

    @Id
    @Column
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    @ToString.Exclude
    private String password;

    @Column(nullable = false)
    @ToString.Exclude
    private String salt;

    @Column
    private String email;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private Role role;

    public String getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
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

    public Role getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
}
