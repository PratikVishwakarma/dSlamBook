package major.com.dslambook.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import major.com.dslambook.Pojo.Friend;
import major.com.dslambook.Pojo.User;
import major.com.dslambook.R;
import major.com.dslambook.SignUpActivity;
import major.com.dslambook.Utility.Constant;
import major.com.dslambook.Utility.ImageConverter;
import major.com.dslambook.Utility.Utility;

public class otherProfileActivity extends AppCompatActivity {

    private String otherUserEmailId, otherUserIdByEmail, userId;
    private Utility utility;
    private User otherUser;
    private Friend sendFriend, getFriend;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference userRef, friendRef;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    private TextView textView_otherUser_username, textView_otherUser_dob;
    private ImageView imageView_otherUser_profilePic;
    private Button button_addFriend, button_acceptFriendRequest, button_cancelFriendRequest, button_unFriend, button_notNow;
    private FloatingActionButton fab_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        utility = new Utility();
        Intent intent = getIntent();

        otherUser = intent.getParcelableExtra(Constant.INTENT_KEY_TO_OTHER_USER_PROFILE);
        otherUserEmailId = otherUser.getEmail();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        userRef = mFirebaseDatabase.getReference(Constant.FIREBASE_REFERENCE_USERS);
        friendRef = mFirebaseDatabase.getReference(Constant.FIREBASE_LOCATION_FRIEND);

        String loginStatus = getLoginStatus();
        if(loginStatus.equals(Constant.LOGIN_STATUS_LOGIN)) {
            userId = getUserId();
            if (userId.equals("") || userId == null){
                goToSignupActivity();
            }
        } else{
            goToSignupActivity();
        }

        otherUserIdByEmail = utility.emailToId(otherUserEmailId);

        Log.e("otherProfileActivity "," user Email : "+otherUserEmailId+" user id : "+otherUserIdByEmail);
        initializeScreen();
        checkFriendRequestStatus();

    }

    @Override
    public void onBackPressed() {
        super.finish();
    }

    public void initializeScreen(){
        textView_otherUser_username = (TextView) findViewById(R.id.otherUser_textView_username);
        textView_otherUser_dob = (TextView) findViewById(R.id.otherUser_textView_dateOfBirth);
        imageView_otherUser_profilePic = (ImageView) findViewById(R.id.otherUser_imageView_profile_pic);
        button_addFriend = (Button) findViewById(R.id.otherUser_button_add_friend_request);
        button_acceptFriendRequest = (Button) findViewById(R.id.otherUser_button_accept_friend_request);
        button_cancelFriendRequest = (Button) findViewById(R.id.otherUser_button_cancel_friend_request);
        button_unFriend = (Button) findViewById(R.id.otherUser_button_unfriend);
        button_notNow = (Button) findViewById(R.id.otherUser_button_notNow);
        fab_chat = (FloatingActionButton) findViewById(R.id.otherUser_fab_chat);

        button_addFriend.setVisibility(View.GONE);
        button_acceptFriendRequest.setVisibility(View.GONE);
        button_cancelFriendRequest.setVisibility(View.GONE);
        button_unFriend.setVisibility(View.GONE);
        button_notNow.setVisibility(View.GONE);
        fab_chat.setVisibility(View.GONE);
        Picasso.with(getApplicationContext())
                .load(otherUser.getImage())
                .into(imageView_otherUser_profilePic);

//        Bitmap bitmap = ((BitmapDrawable) imageView_otherUser_profilePic.getDrawable()).getBitmap();
//        Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 180);
//        imageView_otherUser_profilePic.setImageBitmap(circularBitmap);

        textView_otherUser_username.setText(otherUser.getUserName());
        textView_otherUser_dob.setText(otherUser.getDob());

        button_addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendRequest();
                button_addFriend.setVisibility(View.GONE);
                button_cancelFriendRequest.setVisibility(View.VISIBLE);
            }
        });
        button_cancelFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelSendFriendRequest();
                button_cancelFriendRequest.setVisibility(View.GONE);
                button_addFriend.setVisibility(View.VISIBLE);
            }
        });
        button_unFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelSendFriendRequest();
                button_unFriend.setVisibility(View.GONE);
                button_addFriend.setVisibility(View.VISIBLE);
            }
        });
        button_acceptFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptFriendRequest();
                button_acceptFriendRequest.setVisibility(View.GONE);
                button_notNow.setVisibility(View.INVISIBLE);
                button_unFriend.setVisibility(View.VISIBLE);
                fab_chat.setVisibility(View.VISIBLE);
            }
        });
        button_notNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelSendFriendRequest();
                button_notNow.setVisibility(View.GONE);
                button_acceptFriendRequest.setVisibility(View.GONE);
                button_addFriend.setVisibility(View.VISIBLE);
            }
        });
        fab_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(otherProfileActivity.this, singleChatActivity.class);
                chatIntent.putExtra(Constant.INTENT_KEY_TO_SINGLE_CHAT_ACTIVITY, otherUser);
                startActivity(chatIntent);
            }
        });
    }

    public void addFriendRequest(){
        Date d = new Date();
        String time = new SimpleDateFormat("HH:mm:ss").format(d);
        String date = new SimpleDateFormat("dd-MM-yyyy").format(d);
        sendFriend = new Friend(userId, otherUserIdByEmail, otherUserEmailId, time, date, Constant.FRIEND_REQUEST_TYPE_SEND_REQUEST);
        getFriend = new Friend(otherUserIdByEmail, userId, userId, time, date, Constant.FRIEND_REQUEST_TYPE_GET_REQUEST);
//        String sendFriendRequestKey = friendRef.child(userId).push().getKey();
        friendRef.child(userId).child(otherUserIdByEmail).setValue(sendFriend).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                String getFriendRequestKey = friendRef.child(otherUserIdByEmail).push().getKey();
                friendRef.child(otherUserIdByEmail).child(userId).setValue(getFriend).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Friend Request Sent", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void checkFriendRequestStatus(){
        Log.e("friend status "," user: "+userId+" friend : "+otherUserIdByEmail);
        FirebaseDatabase database = friendRef.child(userId).child(otherUserIdByEmail).getDatabase();
        Log.e("friend status "," user: friend not exist : "+database+"");
//        if(database){
//            Log.e("friend status "," user: friend not exist : "+otherUserIdByEmail);
//        }

        friendRef.child(userId).child(otherUserIdByEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.e("friend status "," user: friend exist : "+otherUserIdByEmail);
                    Friend value = dataSnapshot.getValue(Friend.class);
                    switch (value.getRequestType()){
                        case Constant.FRIEND_REQUEST_TYPE_SEND_REQUEST:
                            button_cancelFriendRequest.setVisibility(View.VISIBLE);
                            break;
                        case Constant.FRIEND_REQUEST_TYPE_GET_REQUEST:
                            button_acceptFriendRequest.setVisibility(View.VISIBLE);
                            button_notNow.setVisibility(View.VISIBLE);
                            break;
                        case Constant.FRIEND_REQUEST_TYPE_FRIENDS:
                            button_unFriend.setVisibility(View.VISIBLE);
                            fab_chat.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                }
                else{
                    Log.e("friend status "," user: friend not exist : "+otherUserIdByEmail);
                    button_addFriend.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void cancelSendFriendRequest(){
        friendRef.child(userId).child(otherUserIdByEmail).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                friendRef.child(otherUserIdByEmail).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        button_acceptFriendRequest.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Friend Request Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void acceptFriendRequest(){
        Date d = new Date();
        String time = new SimpleDateFormat("HH:mm:ss").format(d);
        String date = new SimpleDateFormat("dd-MM-yyyy").format(d);
        sendFriend = new Friend(userId, otherUserIdByEmail, otherUserEmailId, time, date, Constant.FRIEND_REQUEST_TYPE_FRIENDS);
        getFriend = new Friend(otherUserIdByEmail, userId, userId, time, date, Constant.FRIEND_REQUEST_TYPE_FRIENDS);
//        String sendFriendRequestKey = friendRef.child(userId).push().getKey();
        friendRef.child(userId).child(otherUserIdByEmail).setValue(sendFriend).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                String getFriendRequestKey = friendRef.child(otherUserIdByEmail).push().getKey();
                friendRef.child(otherUserIdByEmail).child(userId).setValue(getFriend).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Friend Request accepted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public String getLoginStatus(){
        return sharedpreferences.getString(Constant.SHARED_PREFRENCE_LOGIN_STATUS, Constant.LOGIN_STATUS_LOGOUT);
    }
    public String getUserId(){
        return sharedpreferences.getString(Constant.SHARED_PREFRENCE_USER_ID, "");
    }
    public void printToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void goToSignupActivity(){
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
}
