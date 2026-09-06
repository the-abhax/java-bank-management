package com.banking.account;

import com.banking.api.ApiException;
import com.banking.api.ApiModels.AccountResponse;
import com.banking.api.ApiModels.BalanceResponse;
import com.banking.api.ApiModels.CreateAccountRequest;
import com.banking.api.ApiModels.MoneyRequest;
import com.banking.api.ApiModels.TransactionResponse;
import com.banking.api.ApiModels.TransferRequest;
import com.banking.user.User;
import com.banking.user.UserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
    private static final long FIRST_ACCOUNT_NUMBER = 10_000_100L;
    private static final long ACCOUNT_NUMBER_RANGE = 89_999_900L;

    private final AccountRepository accounts;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final SecureRandom random = new SecureRandom();

    public AccountService(AccountRepository accounts, UserRepository users, PasswordEncoder encoder) {
        this.accounts = accounts;
        this.users = users;
        this.encoder = encoder;
    }

    @Transactional
    public AccountResponse create(CreateAccountRequest request) {
        String email = request.email().trim().toLowerCase();
        if (accounts.existsByUserEmailIgnoreCase(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "An account already exists for this user");
        }
        User user = users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Registered user not found"));
        Account account = new Account(nextAccountNumber(), request.fullName().trim(), user,
                money(request.initialBalance()), encoder.encode(request.pin()));
        return response(accounts.save(account));
    }

    @Transactional(readOnly = true)
    public AccountResponse findByEmail(String email) {
        return response(accounts.findByUserEmailIgnoreCase(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found")));
    }

    @Transactional(readOnly = true)
    public AccountResponse findByNumber(long accountNumber) {
        return response(find(accountNumber));
    }

    @Transactional
    public TransactionResponse credit(long accountNumber, MoneyRequest request) {
        Account account = locked(accountNumber);
        verifyPin(account, request.pin());
        account.credit(money(request.amount()));
        return new TransactionResponse("Money credited successfully", response(account));
    }

    @Transactional
    public TransactionResponse debit(long accountNumber, MoneyRequest request) {
        Account account = locked(accountNumber);
        verifyPin(account, request.pin());
        BigDecimal amount = money(request.amount());
        if (account.getBalance().compareTo(amount) < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }
        account.debit(amount);
        return new TransactionResponse("Money debited successfully", response(account));
    }

    @Transactional
    public TransactionResponse transfer(long senderNumber, TransferRequest request) {
        if (senderNumber == request.receiverAccountNumber()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Sender and receiver accounts must be different");
        }

        long firstNumber = Math.min(senderNumber, request.receiverAccountNumber());
        long secondNumber = Math.max(senderNumber, request.receiverAccountNumber());
        Account first = locked(firstNumber);
        Account second = locked(secondNumber);
        Account sender = first.getAccountNumber() == senderNumber ? first : second;
        Account receiver = first.getAccountNumber() == senderNumber ? second : first;

        verifyPin(sender, request.pin());
        BigDecimal amount = money(request.amount());
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }
        sender.debit(amount);
        receiver.credit(amount);
        return new TransactionResponse("Money transferred successfully", response(sender));
    }

    @Transactional
    public BalanceResponse balance(long accountNumber, String pin) {
        Account account = locked(accountNumber);
        verifyPin(account, pin);
        return new BalanceResponse(accountNumber, account.getBalance());
    }

    private Account find(long accountNumber) {
        return accounts.findById(accountNumber)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found"));
    }

    private Account locked(long accountNumber) {
        return accounts.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found"));
    }

    private void verifyPin(Account account, String rawPin) {
        String stored = account.getSecurityPin();
        boolean valid = stored.startsWith("$2") ? encoder.matches(rawPin, stored) : rawPin.equals(stored);
        if (!valid) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid security PIN");
        }
        if (!stored.startsWith("$2")) {
            account.setSecurityPin(encoder.encode(rawPin));
        }
    }

    private long nextAccountNumber() {
        for (int attempt = 0; attempt < 100; attempt++) {
            long candidate = FIRST_ACCOUNT_NUMBER + random.nextLong(ACCOUNT_NUMBER_RANGE);
            if (!accounts.existsById(candidate)) return candidate;
        }
        throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not allocate an account number");
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_EVEN);
    }

    private AccountResponse response(Account account) {
        return new AccountResponse(account.getAccountNumber(), account.getFullName(),
                account.getUser().getEmail(), account.getBalance());
    }
}
