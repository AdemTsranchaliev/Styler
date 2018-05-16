package softuniBlog.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "hashTags")
public class Hashtags {

    private Integer id;

    private String hashTag;

    private Set<Images> images;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    @Column(name = "hashTag", nullable = false,unique = true)
    public String getHashTag() {
        return hashTag;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "image_hashtag", joinColumns = @JoinColumn(name = "hashTags_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "image_id", referencedColumnName = "id"))
    public Set<Images> getImages() {
        return images;
    }

    public void setImages(Set<Images> images) {
        this.images = images;
    }

    public Hashtags(String hashTag) {
        this.hashTag = hashTag;

    }
    public void addImage(Images image) {
        this.images.add(image);
    }

    public Hashtags() {
    }
}
