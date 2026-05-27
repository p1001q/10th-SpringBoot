package com.example.week09.global.config;

import com.example.week09.global.security.exception.CustomAccessDenied;
import com.example.week09.global.security.exception.CustomEntryPoint;
import com.example.week09.global.security.filter.JwtAuthFilter;
import com.example.week09.global.security.service.CustomUserDetailsService;
import com.example.week09.global.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * [미션2] Spring Security 설정
 * - Public API : /auth/** (회원가입 등) → 인증 없이 접근 가능
 * - Private API: 그 외 모든 엔드포인트 → 로그인 필요
 * - 인증 실패(401) → CustomEntryPoint, 인가 실패(403) → CustomAccessDenied 로 응답 통일
 */
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    // [미션2] Public API: 인증 없이 접근 가능
    private final String[] allowUris = {
            "/auth/**",              // 회원가입 등 인증 불필요 엔드포인트
            "/swagger-ui/**",        // Swagger UI
            "/swagger-resources/**",
            "/v3/api-docs/**"
    };

    // JWT 필터 Bean: JwtUtil, CustomUserDetailsService를 DI받아 생성
    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtUtil, customUserDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)   // HTTP Basic 인증 비활성화
                .formLogin(AbstractHttpConfigurer::disable)   // 폼 로그인 비활성화 (REST API이므로)
                // URI 허용 여부
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(allowUris).permitAll()  // Public API 허용
                        .anyRequest().authenticated()            // 그 이외 API는 인증 필요
                )
                // 세션 비활성화 (JWT 사용이므로 서버 세션 불필요)
                .sessionManagement(AbstractHttpConfigurer::disable)
                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 삽입
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                // 로그아웃
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                // 예외 상황 핸들러
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDenied())        // 403: 권한 없음
                        .authenticationEntryPoint(customEntryPoint())     // 401: 미인증
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomAccessDenied customAccessDenied() {
        return new CustomAccessDenied();
    }

    @Bean
    public CustomEntryPoint customEntryPoint() {
        return new CustomEntryPoint();
    }
}
