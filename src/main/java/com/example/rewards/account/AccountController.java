package com.example.rewards.account;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /** Adds points to an account, creating the account on first earn. */
    @PostMapping("/{accountId}/earn")
    public Mono<AccountResponse> earn(@PathVariable String accountId,
                                      @Valid @RequestBody EarnRequest request) {
        return accountService.earn(accountId, request.points());
    }

    @GetMapping("/{accountId}")
    public Mono<AccountResponse> get(@PathVariable String accountId) {
        return accountService.get(accountId);
    }
}
