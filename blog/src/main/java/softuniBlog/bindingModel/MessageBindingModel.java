package softuniBlog.bindingModel;

import javax.validation.constraints.NotNull;

public class MessageBindingModel {
    @NotNull
    private String email;
    @NotNull
    private String message;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
