package softuniBlog.entity;

import javax.persistence.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "images")
public class Images {
    private Integer id;

    private String title;

    private String description;

    private String pathToImage;

    private User user;

    private Date uploaded_on;

    private Set<Hashtags> hashtags;

    private Set<User> userLikes;

    public Images(String title, String description, User user, Date date) {
        this.title = title;
        this.description = description;
        this.user = user;
        this.uploaded_on = date;

    }

    public Images(String title, String description, User user, Date date, HashSet<Hashtags> hashtag) {
        this.title = title;
        this.description = description;
        this.user = user;
        this.uploaded_on = date;
        this.hashtags = hashtag;
    }

    public Images() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "title", nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "description", nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne()
    @JoinColumn(nullable = false, name = "userId")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "uploaded_on")
    public Date getUploaded_on() {
        return uploaded_on;
    }

    public void setUploaded_on(Date uploaded_on) {
        this.uploaded_on = uploaded_on;
    }

    @ManyToMany(mappedBy = "images")
    public Set<Hashtags> getHashtags() {
        return hashtags;
    }

    public void setHashtags(Set<Hashtags> hashtags) {
        this.hashtags = hashtags;
    }

    public void addHashTag(Hashtags hashtag) {
        this.hashtags.add(hashtag);
    }
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_photo_like", joinColumns = @JoinColumn(name = "images_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    public Set<User> getUserLikes() {
        return userLikes;
    }

    public void setUserLikes(Set<User> userLikes) {
        this.userLikes = userLikes;
    }


    public void addLike(User user) {
        this.userLikes.add(user);
    }

    public String getPathToImage() {
        return pathToImage;
    }

    public void setPathToImage(String pathToImage) {
        this.pathToImage = pathToImage;
    }
}
