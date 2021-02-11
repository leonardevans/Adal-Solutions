package com.adalsolutions.payload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class PasswordRequest {
    @NotEmpty
    String currentPassword;

    @NotEmpty
    @Size(min = 6, message = "Password too short")
    String newPassword;

    @NotEmpty
    String confirmNewPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
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
