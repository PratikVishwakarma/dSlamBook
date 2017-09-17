package major.com.dslambook.Pojo;

/**
 * Created by prati on 15-Apr-17.
 */

public class Chat {
    public String userId, otherUserId, time, date, messageType, content;
    public String seenType, seenTime;

    public Chat() {
    }

    public  Chat(String userId, String otherUserId, String time, String date, String messageType, String content, String seenType, String seenTime) {
        this.userId = userId;
        this.otherUserId = otherUserId;
        this.time = time;
        this.date = date;
        this.messageType = messageType;
        this.content = content;
        this.seenType = seenType;
        this.seenTime = seenTime;
    }

    public String getUserId() {
        return userId;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getContent() {
        return content;
    }

    public String getSeenType() {
        return seenType;
    }

    public String getSeenTime() {
        return seenTime;
    }
}
