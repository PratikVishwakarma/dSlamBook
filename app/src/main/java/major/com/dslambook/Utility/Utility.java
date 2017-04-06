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

}
