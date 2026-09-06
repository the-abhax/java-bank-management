package com.banking.account;

import com.banking.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;

@Entity
@Table(name = "Accounts")
public class Account {
    @Id
    @Column(name = "account_number")
    private Long accountNumber;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "email", referencedColumnName = "email", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "security_pin", nullable = false, length = 100)
    private String securityPin;

    @Version
    private Long version;

    protected Account() {}

    public Account(Long accountNumber, String fullName, User user, BigDecimal balance, String securityPin) {
        this.accountNumber = accountNumber;
        this.fullName = fullName;
        this.user = user;
        this.balance = balance;
        this.securityPin = securityPin;
    }

    public Long getAccountNumber() { return accountNumber; }
    public String getFullName() { return fullName; }
    public User getUser() { return user; }
    public BigDecimal getBalance() { return balance; }
    public String getSecurityPin() { return securityPin; }
    public void setSecurityPin(String securityPin) { this.securityPin = securityPin; }
    public void credit(BigDecimal amount) { balance = balance.add(amount); }
    public void debit(BigDecimal amount) { balance = balance.subtract(amount); }
}
