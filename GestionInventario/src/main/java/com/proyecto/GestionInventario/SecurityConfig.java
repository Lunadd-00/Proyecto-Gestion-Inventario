package com.proyecto.GestionInventario;

import com.proyecto.GestionInventario.domain.Ruta;
import com.proyecto.GestionInventario.service.RutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 *
 * @author abbyc
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, @Lazy RutaService rutaService)
            throws Exception {

        var rutas = rutaService.getRutas();

        http.authorizeHttpRequests(requests -> {

            requests.requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/img/**",
                    "/webjars/**"
            ).permitAll();

            for (Ruta ruta : rutas) {

                if (ruta.isRequiereRol() && ruta.getRol() != null) {
                    requests.requestMatchers(ruta.getRuta())
                            .hasRole(ruta.getRol().name());
                } else {
                    requests.requestMatchers(ruta.getRuta())
                            .permitAll();
                }
            }

            requests.anyRequest().authenticated();
        });
        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
        );
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        );
        http.exceptionHandling(exceptions -> exceptions
                .accessDeniedPage("/acceso_denegado")
        );

        http.sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configurerGlobal(AuthenticationManagerBuilder build,
            @Lazy PasswordEncoder passwordEncoder,
            @Lazy UserDetailsService userDetailsService) throws Exception {

        build.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }
}
