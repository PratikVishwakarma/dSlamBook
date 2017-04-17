package major.com.dslambook.Utility;

/**
 * Created by prati on 26-Mar-17.
 */

public class Utility {

    public String emailToId(String email){
        return email.replace(".",",");
    }

    public String[] getIdFromPostId(String postId){
        return postId.split(Constant.STRING_POSTID_DIFFERENTIATOR);
    }

    public String convertTimeForChat(String time){
        String[] split = time.split(":");
        int hours = Integer.parseInt(split[0]);
        if(hours > 11){
            if(hours == 12){
                return 12+":"+split[1]+" PM";
            } else {
                return hours - 12 + ":" + split[1] + " PM";
            }
        } else {
            return split[0]+":"+split[1]+" AM";
        }
    }
}
