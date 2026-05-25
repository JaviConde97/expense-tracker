package com.fjconde.expensetracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración general de la aplicación.
 *
 * El PasswordEncoder se define aquí (y no en SecurityConfig) para evitar
 * una dependencia circular:
 *   SecurityConfig → UsuarioService → PasswordEncoder → SecurityConfig
 *
 * Al separarlo en esta clase, Spring puede crear el PasswordEncoder
 * de forma independiente antes de construir cualquier otro bean.
 */
@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
