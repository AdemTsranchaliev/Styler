package softuniBlog.bindingModel;

import javax.validation.constraints.NotNull;

public class SecurityEditBindingModel {
    @NotNull
    private String currentPassword;
    @NotNull
    private String newPassword;
    @NotNull
    private String confimNewPassword;

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

    public String getConfimNewPassword() {
        return confimNewPassword;
    }

    public void setConfimNewPassword(String confimNewPassword) {
        this.confimNewPassword = confimNewPassword;
    }
}
