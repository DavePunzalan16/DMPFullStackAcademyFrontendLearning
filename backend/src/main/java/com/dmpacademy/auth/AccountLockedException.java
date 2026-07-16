package com.dmpacademy.auth;

public class AccountLockedException extends RuntimeException {

    private final long remainingMinutes;

    public AccountLockedException(long remainingMinutes) {
        super(String.format("Account is locked. Try again in %d minutes.", remainingMinutes));
        this.remainingMinutes = remainingMinutes;
    }

    public long getRemainingMinutes() {
        return remainingMinutes;
    }
}
