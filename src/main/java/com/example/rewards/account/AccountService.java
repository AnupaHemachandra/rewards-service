package com.example.rewards.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class AccountService {

    private final AccountRepository accounts;

    public AccountService(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @Transactional
    public Mono<AccountResponse> earn(String accountId, long points) {
        return accounts.addPoints(accountId, points)
                .flatMap(balance -> accounts.recordTransaction(accountId, TransactionType.EARN, points)
                        .thenReturn(new AccountResponse(accountId, balance)));
    }

    public Mono<AccountResponse> get(String accountId) {
        return accounts.findById(accountId)
                .map(account -> new AccountResponse(account.id(), account.balance()))
                .switchIfEmpty(Mono.error(() -> new AccountNotFoundException(accountId)));
    }
}
