package major.com.dslambook.Utility;

/**
 * Created by prati on 26-Mar-17.
 */

public class Constant {
    /**
     * Constants related to locations in Firebase, such as the name of the node
     * where active lists are stored (ie "activeLists")
     */

    public static final String FIREBASE_REFERENCE_USERS = "users";
    public static final String FIREBASE_REFERENCE_POST= "Post";
    public static final String FIREBASE_REFERENCE_HOME = "home";
    public static final String FIREBASE_LOCATION_HOME = "home";
    public static final String FIREBASE_LOCATION_FRIEND = "friend";
    public static final String FIREBASE_REFERENCE_LIKE = "like";
    public static final String FIREBASE_LOCATION_LIKE = "like";
    public static final String FIREBASE_LOCATION_CHAT = "chat";
    public static final String FIREBASE_REFERENCE_COMMENT = "comment";

    public static final String FIREBASE_IMAGE_REFERENCE_USER = "user";
    public static final String FIREBASE_IMAGE_REFERENCE_POST = "post";
    public static final String FIREBASE_LOCATION_STORAGE_POSTIMAGE = "postImage";
    public static final String FIREBASE_LOCATION_STORAGE_DISPLAYPICTURE = "displayPicture";

    public static final int IS_PROVIDE_BASCI_INFORMATION = 1;
    public static final int IS_NOT_PROVIDE_BASCI_INFORMATION = 0;

    public static final String STRING_POSTID_DIFFERENTIATOR = "*d^f+";
    public static final String STRING_POST_IMAGE_DIFFERENTIATOR = "*d^f+";
    public static final String STRING_POST_IMAGE_LIKE_DIFFERENTIATOR = "*Ld^f+";


    public static final String INTENT_KEY_STRING_HOME_CALLING = "homeIntent";
    public static final String INTENT_VALUE_STRING_HOME_CALLING_NORMAL = "normal";
    public static final String INTENT_VALUE_STRING_HOME_CALLING_AFTER_POST = "afterPost";
    public static final String INTENT_KEY_TO_OTHER_USER_PROFILE = "ToOtherUserProfile";
    public static final String INTENT_KEY_TO_SINGLE_CHAT_ACTIVITY = "ToSingleChatActivity";

    /*
    * Constants for formats like time and date
    * */
    public static final String FORMATE_ADD_POST_DATE = "dd-MMM-yyyy";
    public static final String FORMATE_ADD_POST_TIME = "HH:mm";
    public static final String FORMATE_ADD_POST_TIME_WITH_SECONDS = "HH:mm:ss";


    public static final String TYPE_POST_TYPE_POST_BY_YOU = "byYou";
    public static final String TYPE_POST_TYPE_POST_BY_FRIEND = "byYourFriend";
    public static final String TYPE_POST_TYPE_SHARED_BY_FRIEND = "sharedByYourFriend";
    public static final String TYPE_POST_TYPE_TAGGED_BY_FRIEND = "taggedByYourFriend";


    public static final String SHARED_PREFRENCE_USER_EMAIL_ID = "userEmailId";
    public static final String SHARED_PREFRENCE_USER_USERNAME = "userUserName";
    public static final String SHARED_PREFRENCE_SIGNUP_COMPLETE = "userSignupProcessState";
    public static final String SHARED_PREFRENCE_USER_BASIC_INFORMATION_STATUS = "userBasicInfoStatus";

    public static final String SHARED_PREFRENCE_USER_ID = "userId";
    public static final String SHARED_PREFRENCE_MEMBER_NAME = "userName";

    //    LOGIN STATUS
    public final static String SHARED_PREFRENCE_LOGIN_STATUS = "loginStatus";
    public final static String LOGIN_STATUS_LOGIN = "login";
    public final static String LOGIN_STATUS_LOGOUT = "logout";


    public static final int FRIEND_REQUEST_TYPE_SEND_REQUEST = 1;
    public static final int FRIEND_REQUEST_TYPE_GET_REQUEST = 2;
    public static final int FRIEND_REQUEST_TYPE_FRIENDS = 3;
    public static final int FRIEND_REQUEST_TYPE_DELETE_REQUEST = 4;
    public static final int FRIEND_REQUEST_TYPE_NOT_NOW_REQUEST = 5;


    public static final int LIKE_STATUS_LIKED = 1;
    public static final int LIKE_STATUS_NOT_LIKED = 0;

    public static final String CHAT_MESSAGE_TYPE_SENT = "messageSent";
    public static final String CHAT_MESSAGE_TYPE_RECIEVE = "messageRecieve";
    public static final String CHAT_MESSAGE_SEEN_TYPE_RECIEVE = "getMessage";
    public static final String CHAT_MESSAGE_SEEN_TYPE_SENT_SEEN = "seen";
    public static final String CHAT_MESSAGE_SEEN_TYPE_SENT_NOTSEEN = "notSeen";
    public static final String CHAT_MESSAGE_SEEN_TYPE = "seenType";
    public static final String CHAT_MESSAGE_SEEN_TIME = "seenTime";




}
