package com.bookstore.model;


public class ResetPasswordViewModel {
    private String email;
    private String verificationCode;
    private String hiddenSendVerificationCode;
    private String newPassword;
    private String confirmPassword;


    public ResetPasswordViewModel() {}

    public ResetPasswordViewModel(String email ) {
        this.email = email;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getHiddenSendVerificationCode() {
        return hiddenSendVerificationCode;
    }

    public void setHiddenSendVerificationCode(String hiddenSendVerificationCode) {
        this.hiddenSendVerificationCode = hiddenSendVerificationCode;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    // Getters and Setters
}
