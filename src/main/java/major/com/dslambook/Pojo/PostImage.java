package major.com.dslambook.Pojo;

/**
 * Created by prati on 11-Sep-16.
 */
public class PostImage {

    private String image;
    private int like;

    public PostImage() {}

    public PostImage(String image, int like) {
        this.image = image;
        this.like = like;
    }

    public String getImage() {
        return image;
    }

    public int getLike() {
        return like;
    }
}
