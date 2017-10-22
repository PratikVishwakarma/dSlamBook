package major.com.dslambook.Pojo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import major.com.dslambook.Utility.Constant;

/**
 * Created by prati on 22-Sep-16.
 */
public class Like {

    public final static String COLUMN_POSTID = "postId";
    public final static String COLUMN_IMAGEID = "imageId";

    public String postId, time, date, imageId;

    public Like() {
    }

    public Like(String postId, String imageId) {
        this.postId = postId;
        this.imageId = imageId;

        DateFormat timef = new SimpleDateFormat(Constant.FORMATE_ADD_POST_TIME);
        this.time = timef.format(Calendar.getInstance().getTime());
        DateFormat datef = new SimpleDateFormat(Constant.FORMATE_ADD_POST_DATE);
        this.date =  datef.format(Calendar.getInstance().getTime());
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
