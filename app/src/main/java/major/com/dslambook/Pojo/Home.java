package major.com.dslambook.Pojo;

/**
 * Created by prati on 14-Nov-16.
 */

public class Home {
    public String postId, otherUserId, postType, time, date;

    public Home() {

    }

    public Home(String postId, String otherUserId, String postType, String time, String date) {
        this.postId = postId;
        this.otherUserId = otherUserId;
        this.postType = postType;
        this.time = time;
        this.date = date;
    }

    public String getPostId() {
        return postId;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public String getPostType() {
        return postType;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }
}
