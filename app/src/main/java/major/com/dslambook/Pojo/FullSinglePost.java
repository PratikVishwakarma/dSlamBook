package major.com.dslambook.Pojo;

/**
 * Created by prati on 31-Mar-17.
 */

public class FullSinglePost {
    public String userId;
    public String postId;
    public String content;
    public String time;
    public String date;
    public String image;
    public int  like;
    public String userName;
    public String userProfilePic;

    public FullSinglePost() {
    }

    public FullSinglePost(String userId, String postId, String content, String time, String date, String image,
                          int like, String userName, String userProfilePic) {
        this.userId = userId;
        this.postId = postId;
        this.content = content;
        this.time = time;
        this.date = date;
        this.image = image;
        this.like = like;
        this.userName = userName;
        this.userProfilePic = userProfilePic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }
}
