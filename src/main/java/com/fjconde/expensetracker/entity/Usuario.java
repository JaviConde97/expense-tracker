package com.fjconde.expensetracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Entidad que representa a un usuario de la aplicación.
 * Implementa UserDetails para integrarse directamente con Spring Security.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    // El email es el identificador único con el que iniciamos sesión
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // -------------------------------------------------------------------------
    // Métodos de UserDetails — necesarios para que Spring Security nos reconozca
    // -------------------------------------------------------------------------

    /**
     * Devuelve los roles del usuario. Por ahora todos tienen ROLE_USER.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * Spring Security usa este método para obtener el identificador del usuario.
     * En nuestro caso es el email.
     */
    @Override
    public String getUsername() {
        return email;
    }

    // Los tres métodos siguientes devuelven true para que la cuenta esté activa
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
