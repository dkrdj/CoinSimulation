package com.coinsimulation.exception;

import org.springframework.security.core.AuthenticationException;

public class LoginException extends AuthenticationException {
    public LoginException() {
        super("올바른 로그인 요청이 아닙니다.");
    }
}
