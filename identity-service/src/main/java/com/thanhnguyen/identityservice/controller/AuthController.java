package com.thanhnguyen.identityservice.controller;

import com.thanhnguyen.identityservice.dto.request.LoginRequest;
import com.thanhnguyen.identityservice.dto.request.RefreshTokenRequest;
import com.thanhnguyen.identityservice.dto.response.LoginResponse;
import com.thanhnguyen.identityservice.entity.RefreshToken;
import com.thanhnguyen.identityservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request)
    {
        return new ResponseEntity<>(authService.login(request), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest)
    {
        return new ResponseEntity<>(authService.refresh(refreshTokenRequest), HttpStatus.OK);
    }
}
