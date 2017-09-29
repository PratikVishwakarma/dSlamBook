package major.com.dslambook.Pojo;

/**
 * Created by prati on 11-Sep-16.
 */
public class PostImage {

    private String image, imageUrl;
    private int like;

    public PostImage() {}

    public PostImage(String image, String imageUrl, int like) {
        this.image = image;
        this.imageUrl = imageUrl;
        this.like = like;
    }

    public String getImage() {
        return image;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public int getLike() {
        return like;
    }
}
