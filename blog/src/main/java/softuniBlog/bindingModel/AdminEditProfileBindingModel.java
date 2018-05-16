package softuniBlog.bindingModel;

import softuniBlog.entity.Images;
import softuniBlog.entity.Role;
import softuniBlog.entity.User;

import java.util.Set;

public class AdminEditProfileBindingModel {


    private String email;

    private String fullName;

    private String profilePicture;

    private String facebook;

    private String twitter;

    private String instagram;

    private String ownWeb;

    private String location;

    private String motto;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getOwnWeb() {
        return ownWeb;
    }

    public void setOwnWeb(String ownWeb) {
        this.ownWeb = ownWeb;
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
