package com.banking.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public final class ApiModels {
    private ApiModels() {}

    public record RegisterRequest(
            @NotBlank String name,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 6) String password) {}

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
    public record UserResponse(Long id, String name, String email) {}
    public record LoginResponse(Long userId, String name, String email, Long accountNumber) {}

    public record CreateAccountRequest(
            @NotBlank String fullName,
            @NotBlank @Email String email,
            @NotNull @DecimalMin("0.00") BigDecimal initialBalance,
            @NotBlank @Size(min = 4, max = 20) String pin) {}

    public record MoneyRequest(
            @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
            @NotBlank String pin) {}

    public record TransferRequest(
            @NotNull Long receiverAccountNumber,
            @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
            @NotBlank String pin) {}

    public record PinRequest(@NotBlank String pin) {}
    public record AccountResponse(Long accountNumber, String fullName, String email, BigDecimal balance) {}
    public record TransactionResponse(String message, AccountResponse account) {}
    public record BalanceResponse(Long accountNumber, BigDecimal balance) {}
    public record ErrorResponse(int status, String error, String message) {}
}
