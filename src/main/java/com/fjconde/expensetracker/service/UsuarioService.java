package com.fjconde.expensetracker.service;

import com.fjconde.expensetracker.dto.RegistroDto;
import com.fjconde.expensetracker.entity.Usuario;
import com.fjconde.expensetracker.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio de usuarios.
 *
 * Implementa UserDetailsService para que Spring Security pueda cargar
 * un usuario por su email en el momento del login.
 */
@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Spring Security llama a este método automáticamente durante el login.
     * Busca al usuario por email y lanza excepción si no existe.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }

    /**
     * Registra un nuevo usuario tras validar que el email no esté ya en uso.
     *
     * @throws IllegalArgumentException si el email ya existe
     */
    public void registrar(RegistroDto dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Ya existe una cuenta con ese email");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        // Nunca guardamos la contraseña en texto plano — BCrypt la cifra
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        usuarioRepository.save(usuario);
    }
}
