package softuniBlog.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.jws.soap.SOAPBinding;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    private Integer id;

    private String email;

    private String fullName;

    private String password;

    private Set<Role> roles;

    private Set<Images> images;

    private Set<User> followingUser;

    private Set<User> userFollowers;

    private String profilePicture;

    private String facebook;

    private String twitter;

    private String instagram;

    private String ownWeb;

    private String location;

    private String motto;

    private Set<Images> likeImages;


    public User(String email, String fullName, String password) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;

        this.roles = new HashSet<>();
        this.images = new HashSet<>();
        this.followingUser = new HashSet<>();
        this.likeImages=new HashSet<>();
    }

    public User() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "email", unique = true, nullable = false)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "fullName", nullable = false)
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Column(name = "password", length = 60, nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles")
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    @OneToMany(mappedBy = "user")
    public Set<Images> getImages() {
        return images;
    }

    public void setImages(Set<Images> images) {
        this.images = images;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_following", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "following_id", referencedColumnName = "id"))
    public Set<User> getFollowingUser() {
        return followingUser;
    }

    public void setFollowingUser(Set<User> followingUser) {
        this.followingUser = followingUser;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "follower_user", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "follower_id", referencedColumnName = "id"))
    public Set<User> getUserFollowers() {
        return userFollowers;
    }

    public void setUserFollowers(Set<User> userFollowers) {
        this.userFollowers = userFollowers;
    }

    public void addFollow(User user) {
        this.followingUser.add(user);
    }

    public void addFollower(User user) {
        this.userFollowers.add(user);
    }

    @Column(name = "profilePicturePath", nullable = false)

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

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_photo_like", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "images_id", referencedColumnName = "id"))
    public Set<Images> getLikeImages() {
        return likeImages;
    }

    public void setLikeImages(Set<Images> likeImages) {
        this.likeImages = likeImages;
    }
}