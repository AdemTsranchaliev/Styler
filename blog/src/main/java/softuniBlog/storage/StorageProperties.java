package softuniBlog.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * Folder location for storing files
     */
    private String location = "src\\main\\resources\\static\\img\\upload-dir";
    private String profilePictureLocation = "src\\main\\resources\\static\\img\\ProfilePictures";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProfilePictureLocation() {
        return profilePictureLocation;
    }

    public void setProfilePictureLocation(String profilePictureLocation) {
        this.profilePictureLocation = profilePictureLocation;
    }
}
