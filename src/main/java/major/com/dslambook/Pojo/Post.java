package major.com.dslambook.Pojo;

/**
 * Created by prati on 05-Sep-16.
 */
public class Post {

    public final static String COLUMN_USERID = "userId";
    public final static String COLUMN_POSTID = "postId";
    public final static String COLUMN_CONTENT = "content";
    public final static String COLUMN_TIME = "time";
    public final static String COLUMN_DATE = "date";
    public final static String COLUMN_LIKE = "like";
    public final static String COLUMN_TOTALIMAGES = "totalImages";

    public String userId;
    public String postId;
    public String content;
    public String time;
    public String date;
    public int like;
    public int totalImages;

    public Post() {
    }

    public  Post(String userId, String postId, String content, String time, String date, int like, int totalImages) {
        this.userId = userId;
        this.postId = postId;
        this.content = content;
        this.time = time;
        this.date = date;
        this.like = like;
        this.totalImages = totalImages;
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

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public int getLike() {
        return like;
    }

    public int getTotalImages() {
        return totalImages;
    }
}
