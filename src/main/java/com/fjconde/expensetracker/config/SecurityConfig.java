package com.fjconde.expensetracker.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security para aplicación web con sesión HTTP.
 *
 * Diferencia con task-manager-api: allí usábamos JWT (stateless).
 * Aquí usamos sesión de servidor (stateful) porque el navegador
 * gestiona automáticamente la cookie de sesión — no necesitamos tokens.
 *
 * El PasswordEncoder se define en AppConfig para evitar dependencia circular.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF activo (protección por defecto en apps web con formularios)
            // Solo lo desactivamos para la consola H2 en desarrollo
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )

            // Configuración de rutas
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas: páginas de auth, recursos estáticos y consola H2
                .requestMatchers("/auth/**", "/css/**", "/js/**", "/h2-console/**").permitAll()
                // Todo lo demás requiere estar autenticado
                .anyRequest().authenticated()
            )

            // Configuración del formulario de login
            // Spring Security intercepta el POST a /auth/login automáticamente
            .formLogin(form -> form
                .loginPage("/auth/login")                    // GET → muestra el formulario
                .loginProcessingUrl("/auth/login")           // POST → Spring Security lo procesa
                .defaultSuccessUrl("/dashboard", true)       // Tras login exitoso → dashboard
                .failureUrl("/auth/login?error=true")        // Tras fallo → login con mensaje
                .permitAll()
            )

            // Configuración del logout
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)    // Destruye la sesión
                .deleteCookies("JSESSIONID")    // Borra la cookie de sesión
                .permitAll()
            )

            // Necesario para la consola H2 (usa iframes)
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )

            // Proveedor de autenticación con BCrypt
            .authenticationProvider(authenticationProvider());

        return http.build();
    }

    /**
     * Proveedor de autenticación que consulta nuestra base de datos
     * y verifica la contraseña con BCrypt.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
