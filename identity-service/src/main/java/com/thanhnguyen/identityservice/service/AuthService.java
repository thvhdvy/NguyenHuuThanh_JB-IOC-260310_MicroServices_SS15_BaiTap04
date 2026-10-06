package com.thanhnguyen.identityservice.service;

import com.thanhnguyen.identityservice.dto.request.LoginRequest;
import com.thanhnguyen.identityservice.dto.request.RefreshTokenRequest;
import com.thanhnguyen.identityservice.dto.response.LoginResponse;
import com.thanhnguyen.identityservice.entity.RefreshToken;
import com.thanhnguyen.identityservice.entity.User;
import com.thanhnguyen.identityservice.exception.InvalidCredentialsException;
import com.thanhnguyen.identityservice.exception.InvalidRefreshTokenException;
import com.thanhnguyen.identityservice.repository.RefreshTokenRepository;
import com.thanhnguyen.identityservice.repository.UserRepository;
import com.thanhnguyen.identityservice.security.JwtService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    @Value("${app.jwt.refresh-token-expiration}") private long refreshTokenExpiration;

    public LoginResponse login(LoginRequest request)
    {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(InvalidCredentialsException::new);
        if(!passwordEncoder.matches(request.getPassword(), user.getHashedPassword()))
        {
            throw new InvalidCredentialsException();
        }
        String accessToken = jwtService.generateAccessToken(user.getUsername(), user.getRole());
        RefreshToken refreshToken = createRefreshToken(user);
        refreshTokenRepository.save(refreshToken);
        return new LoginResponse(accessToken, refreshToken.getToken());
    }

    @Transactional
    public LoginResponse refresh(RefreshTokenRequest refreshTokenRequest)
    {
        String oldRefreshToken = refreshTokenRequest.getRefreshToken();
        RefreshToken stored = refreshTokenRepository.findByToken(oldRefreshToken)
                .orElseThrow(InvalidRefreshTokenException::new);

        if(stored.getExpiryDate().isBefore(Instant.now()))
        {
            throw new InvalidRefreshTokenException();
        }

        User user = stored.getUser();
        int deleted = refreshTokenRepository.deleteByToken(oldRefreshToken);
        if(deleted == 0)
        {
            throw new InvalidRefreshTokenException();
        }

        String accessToken = jwtService.generateAccessToken(user.getUsername(), user.getRole());

        RefreshToken newRefreshToken = createRefreshToken(user);
        refreshTokenRepository.save(newRefreshToken);
        return new LoginResponse(accessToken, newRefreshToken.getToken());
    }

    private RefreshToken createRefreshToken(User user)
    {
        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setToken(UUID.randomUUID().toString());
        newRefreshToken.setUser(user);
        newRefreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpiration));
        return newRefreshToken;
    }
}
