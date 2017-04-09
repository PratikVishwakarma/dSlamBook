package major.com.dslambook.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import major.com.dslambook.Utility.Constant;
import major.com.dslambook.Utility.Utility;

public class otherProfileActivity extends AppCompatActivity {

    private String userEmailId, userIdByEmail, otherUserEmailId, otherUserIdByEmail;
    private Utility utility;
    private User otherUser;
    private Friend sendFriend, getFriend;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference userRef, friendRef;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    private TextView textView_otherUser_username, textView_otherUser_dob;
    private ImageView imageView_otherUser_profilePic;
    private Button button_addFriend, button_acceptFriendRequest, button_cancelFriendRequest, button_unFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        utility = new Utility();
        Intent intent = getIntent();
        otherUser = intent.getParcelableExtra(Constant.INTENT_KEY_STRING_USERLIST_TO_PROFILE);
        otherUserEmailId = otherUser.getEmail();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        userRef = mFirebaseDatabase.getReference(Constant.FIREBASE_LOCATION_USERS);
        friendRef = mFirebaseDatabase.getReference(Constant.FIREBASE_LOCATION_FRIEND);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userEmailId = sharedpreferences.getString(Constant.SHARED_PREFRENCE_USER_EMAIL_ID, null);

        userIdByEmail = utility.emailToId(userEmailId);
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
        button_unFriend= (Button) findViewById(R.id.otherUser_button_unfriend);

        button_addFriend.setVisibility(View.GONE);
        button_acceptFriendRequest.setVisibility(View.GONE);
        button_cancelFriendRequest.setVisibility(View.GONE);
        button_unFriend.setVisibility(View.GONE);
        Picasso.with(getApplicationContext())
                .load(otherUser.getImageUrl())
                .into(imageView_otherUser_profilePic);

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
                button_unFriend.setVisibility(View.VISIBLE);
            }
        });
    }

    public void addFriendRequest(){
        Date d = new Date();
        String time = new SimpleDateFormat("HH:mm:ss").format(d);
        String date = new SimpleDateFormat("dd-MM-yyyy").format(d);
        sendFriend = new Friend(userIdByEmail, otherUserIdByEmail, otherUserEmailId, time, date, Constant.FRIEND_REQUEST_TYPE_SEND_REQUEST);
        getFriend = new Friend(otherUserIdByEmail, userIdByEmail, userEmailId, time, date, Constant.FRIEND_REQUEST_TYPE_GET_REQUEST);
//        String sendFriendRequestKey = friendRef.child(userIdByEmail).push().getKey();
        friendRef.child(userIdByEmail).child(otherUserIdByEmail).setValue(sendFriend).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                String getFriendRequestKey = friendRef.child(otherUserIdByEmail).push().getKey();
                friendRef.child(otherUserIdByEmail).child(userIdByEmail).setValue(getFriend).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Friend Request Sent", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void checkFriendRequestStatus(){
        Log.e("friend status "," user: "+userIdByEmail+" friend : "+otherUserIdByEmail);
        FirebaseDatabase database = friendRef.child(userIdByEmail).child(otherUserIdByEmail).getDatabase();
        Log.e("friend status "," user: friend not exist : "+database+"");
//        if(database){
//            Log.e("friend status "," user: friend not exist : "+otherUserIdByEmail);
//        }

        friendRef.child(userIdByEmail).child(otherUserIdByEmail).addListenerForSingleValueEvent(new ValueEventListener() {
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
                            break;
                        case Constant.FRIEND_REQUEST_TYPE_FRIENDS:
                            button_acceptFriendRequest.setVisibility(View.VISIBLE);
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
        friendRef.child(userIdByEmail).child(otherUserIdByEmail).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                friendRef.child(otherUserIdByEmail).child(userIdByEmail).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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
        sendFriend = new Friend(userIdByEmail, otherUserIdByEmail, otherUserEmailId, time, date, Constant.FRIEND_REQUEST_TYPE_FRIENDS);
        getFriend = new Friend(otherUserIdByEmail, userIdByEmail, userEmailId, time, date, Constant.FRIEND_REQUEST_TYPE_FRIENDS);
//        String sendFriendRequestKey = friendRef.child(userIdByEmail).push().getKey();
        friendRef.child(userIdByEmail).child(otherUserIdByEmail).setValue(sendFriend).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                String getFriendRequestKey = friendRef.child(otherUserIdByEmail).push().getKey();
                friendRef.child(otherUserIdByEmail).child(userIdByEmail).setValue(getFriend).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Friend Request accepted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
