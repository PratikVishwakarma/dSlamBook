package major.com.dslambook.Pojo;

/**
 * Created by prati on 05-Sep-16.
 */
public class Post {

    public String userId;
    public String postId;
    public String content;
    public String time;
    public String date;
    public String image;
    public int  like;
    public Post() {
    }

    public Post(String userId, String postId, String content, String image, String time, String date, int like) {
        this.userId = userId;
        this.postId = postId;
        this.content = content;
        this.image = image;
        this.time = time;
        this.date = date;
        this.like = like;
    }

    public String getUserId() {
        return userId;
    }

    public String getPostId() {
        return postId;
    }

    public String getContent() {
        return content;
    }

    public String getImage() {
        return image;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public int getLike() {
        return like;
    }

}
