package com.attendance.scheduler.common.controller;

import com.attendance.scheduler.common.dto.LoginRequest;
import com.attendance.scheduler.common.dto.RefreshTokenRequest;
import com.attendance.scheduler.common.dto.TokenResponse;
import com.attendance.scheduler.infra.config.security.jwt.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * JWT 기반 인증 REST 엔드포인트.
 * 기존 세션/폼 로그인(webFilterChain)과 병행 동작한다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

            return ResponseEntity.ok(issueTokens(authentication.getName(),
                    extractRoles(authentication.getAuthorities())));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", resolveMessage(e)));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();
        if (!jwtProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "유효하지 않은 refreshToken 입니다."));
        }

        String username = jwtProvider.getUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return ResponseEntity.ok(issueTokens(username, extractRoles(userDetails.getAuthorities())));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<String> roles = extractRoles(authentication.getAuthorities());
        return ResponseEntity.ok(Map.of(
                "username", authentication.getName(),
                "roles", roles));
    }

    // Spring Security 가 자동 추가하는 인증 팩터(FACTOR_*) 등을 제외하고 ROLE_ 권한만 반환
    private List<String> extractRoles(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .toList();
    }

    private TokenResponse issueTokens(String username, List<String> roles) {
        String accessToken = jwtProvider.createAccessToken(username, roles);
        String refreshToken = jwtProvider.createRefreshToken(username);
        return TokenResponse.of(accessToken, refreshToken,
                jwtProvider.getAccessTokenValiditySeconds(), username, roles);
    }

    private String resolveMessage(AuthenticationException e) {
        if (e instanceof UsernameNotFoundException) {
            return "등록되지 않은 아이디입니다";
        } else if (e instanceof BadCredentialsException) {
            return "아이디 또는 비밀번호가 잘못되었습니다.";
        } else if (e instanceof DisabledException) {
            return "관리자의 승인을 기다려주세요";
        }
        return "로그인에 실패했습니다.";
    }
}
