package major.com.dslambook.Pojo;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by prati on 05-Apr-17.
 */

public class Friend {
    public String userId, otherUserId;
    public String otherUserEmail;
    public String time;
    public String date;
    public int requestType;

    public Friend() {
    }

    public Friend(String userId, String otherUserId, String otherUserEmail, String time, String date, int requestType) {
        this.userId = userId;
        this.otherUserId = otherUserId;
        this.otherUserEmail = otherUserEmail;
        this.time = time;
        this.date = date;
        this.requestType = requestType;
    }

    public String getUserId() {
        return userId;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public String getOtherUserEmail() {
        return otherUserEmail;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public int getRequestType() {
        return requestType;
    }
}
