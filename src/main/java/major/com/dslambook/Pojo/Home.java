package major.com.dslambook.Pojo;

/**
 * Created by prati on 14-Nov-16.
 */

public class Home {
    public String postId, otherUserId;

    public Home() {}

    public Home(String postId, String otherUserId) {
        this.postId = postId;
        this.otherUserId = otherUserId;
    }

    public String getPostId() {
        return postId;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

}
