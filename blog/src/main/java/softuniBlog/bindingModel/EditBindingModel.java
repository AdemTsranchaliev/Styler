package softuniBlog.bindingModel;

import javax.validation.constraints.NotNull;

public class EditBindingModel {


    @NotNull
    private String fullName;

    private String location;

    private String motto;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }
}
