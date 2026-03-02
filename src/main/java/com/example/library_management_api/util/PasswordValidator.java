package com.example.library_management_api.util;

import java.util.regex.Pattern;

public class PasswordValidator {

    private static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$";

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile(PASSWORD_REGEX);

    public static boolean isValid(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static String errorMessage() {
        return "Mật khẩu phải tối thiểu 8 ký tự, bao gồm ít nhất 1 chữ hoa, 1 chữ thường và 1 chữ số";
    }
}
