package com.eleven.logistics.auth.application.service;

import com.eleven.logistics.auth.application.dto.SignInCommand;
import com.eleven.logistics.auth.application.dto.SignUpCommand;
import com.eleven.logistics.auth.application.dto.UserResponseDto;
import com.eleven.logistics.auth.domain.entity.User;
import com.eleven.logistics.auth.domain.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${service.jwt.access-expiration}")
    private Long accessExpiration;

    @Value("${service.jwt.secret-key}")
    private String secretKeyValue;

    private SecretKey secretKey;


    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKeyValue));
    }

    public String signIn(SignInCommand signInCommand) {

        User user = userRepository.findByUsername(signInCommand.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("등록되지 않은 사용자입니다.")
        );

        user.tryToSignIn(signInCommand.getUsername(), signInCommand.getPassword(), passwordEncoder);

        return createAccessToken(user);
    }

    public String createAccessToken(User user) {
        return Jwts.builder()
                // 사용자 ID를 클레임으로 설정
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .claim("slackAccount", user.getSlackAccount())
                // JWT 발행자를 설정
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                // JWT 만료 시간을 설정
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                // SecretKey를 사용하여 HMAC-SHA512 알고리즘으로 서명
                .signWith(secretKey, Jwts.SIG.HS512)
                // JWT 문자열로 컴팩트하게 변환
                .compact();
    }


    @Transactional
    public UserResponseDto signUp(SignUpCommand signUpCommand) {

        // 중복 체크
        if (userRepository.findByUsername(signUpCommand.getUsername()).isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // 비밀번호 해싱
        String hashedPassword = passwordEncoder.encode(signUpCommand.getPassword());

        // User 엔티티에 객체 생성에 대한 책임을 부여합니다.
        User user = User.create(
                signUpCommand.getUsername(),
                hashedPassword,
                signUpCommand.getSlackAccount(),
                signUpCommand.getRole()
        );

        userRepository.save(user);

        // Dirty Checking 으로 User 엔티티의 Id가 추가되어 반환됩니다.
        return UserResponseDto.of(user);
    }

}