package major.com.dslambook.Pojo;

/**
 * Created by prati on 22-Sep-16.
 */
public class Like {

    public String postId, time, date;

    public Like() {
    }

    public Like(String postId, String time, String date) {
        this.postId = postId;
        this.time = time;
        this.date = date;
    }


    public String getpostId() {
        return postId;
    }

    public void setpostId(String postId) {
        this.postId = postId;
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
}
