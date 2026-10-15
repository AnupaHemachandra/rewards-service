package com.example.rewards.account;

import jakarta.validation.constraints.Positive;

public record EarnRequest(@Positive long points) {
}
