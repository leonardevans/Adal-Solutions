package com.adalsolutions.payload;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class ResetPasswordRequest {
    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    private String newPassword;
    @NotEmpty
    private String confirmNewPassword;

    public ResetPasswordRequest(@Email @NotEmpty String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
