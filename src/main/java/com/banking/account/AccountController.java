package com.banking.account;

import com.banking.api.ApiModels.AccountResponse;
import com.banking.api.ApiModels.BalanceResponse;
import com.banking.api.ApiModels.CreateAccountRequest;
import com.banking.api.ApiModels.MoneyRequest;
import com.banking.api.ApiModels.PinRequest;
import com.banking.api.ApiModels.TransactionResponse;
import com.banking.api.ApiModels.TransferRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService service;

    public AccountController(AccountService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    AccountResponse create(@Valid @RequestBody CreateAccountRequest request) {
        return service.create(request);
    }

    @GetMapping("/{accountNumber}")
    AccountResponse find(@PathVariable long accountNumber) {
        return service.findByNumber(accountNumber);
    }

    @GetMapping
    AccountResponse findByEmail(@RequestParam String email) {
        return service.findByEmail(email);
    }

    @PostMapping("/{accountNumber}/credit")
    TransactionResponse credit(@PathVariable long accountNumber, @Valid @RequestBody MoneyRequest request) {
        return service.credit(accountNumber, request);
    }

    @PostMapping("/{accountNumber}/debit")
    TransactionResponse debit(@PathVariable long accountNumber, @Valid @RequestBody MoneyRequest request) {
        return service.debit(accountNumber, request);
    }

    @PostMapping("/{accountNumber}/transfer")
    TransactionResponse transfer(@PathVariable long accountNumber, @Valid @RequestBody TransferRequest request) {
        return service.transfer(accountNumber, request);
    }

    @PostMapping("/{accountNumber}/balance")
    BalanceResponse balance(@PathVariable long accountNumber, @Valid @RequestBody PinRequest request) {
        return service.balance(accountNumber, request.pin());
    }
}
