package com.banking.user;

import com.banking.account.AccountRepository;
import com.banking.api.ApiException;
import com.banking.api.ApiModels.LoginRequest;
import com.banking.api.ApiModels.LoginResponse;
import com.banking.api.ApiModels.RegisterRequest;
import com.banking.api.ApiModels.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository users;
    private final AccountRepository accounts;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository users, AccountRepository accounts, PasswordEncoder encoder) {
        this.users = users;
        this.accounts = accounts;
        this.encoder = encoder;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        }
        User saved = users.save(new User(request.name().trim(), email, encoder.encode(request.password())));
        return new UserResponse(saved.getId(), saved.getName(), saved.getEmail());
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = users.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!matches(request.password(), user.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        if (!user.getPassword().startsWith("$2")) {
            user.setPassword(encoder.encode(request.password()));
        }
        Long accountNumber = accounts.findByUserEmailIgnoreCase(user.getEmail())
                .map(account -> account.getAccountNumber()).orElse(null);
        return new LoginResponse(user.getId(), user.getName(), user.getEmail(), accountNumber);
    }

    private boolean matches(String raw, String stored) {
        return stored.startsWith("$2") ? encoder.matches(raw, stored) : raw.equals(stored);
    }
}
