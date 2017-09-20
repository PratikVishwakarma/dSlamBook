package major.com.dslambook.Pojo;

/**
 * Created by prati on 22-Sep-16.
 */
public class Like {

    public String postId, time, date, imageId;

    public Like() {
    }

    public Like(String postId, String time, String date, String imageId) {
        this.postId = postId;
        this.time = time;
        this.date = date;
        this.imageId = imageId;

    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getPostId() {
        return postId;
    }

    public String getImageId() {
        return imageId;
    }
}
