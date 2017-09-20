package major.com.dslambook.UI;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import major.com.dslambook.Adapter.ChatListAdapter;
import major.com.dslambook.Adapter.UsersListAdapter;
import major.com.dslambook.Pojo.Chat;
import major.com.dslambook.Pojo.Friend;
import major.com.dslambook.Pojo.User;
import major.com.dslambook.R;
import major.com.dslambook.Utility.Constant;
import major.com.dslambook.Utility.ImageConverter;
import major.com.dslambook.Utility.Utility;

public class singleChatActivity extends AppCompatActivity {

    private ArrayList<Chat> chatList = new ArrayList<>();
    private ChatListAdapter chatListAdapter;
    private ListView listViewChatList;

    private String userEmailId, userIdByEmail, otherUserEmailId, otherUserIdByEmail;
    private String sentKey, recieveKey;
    private Utility utility;
    private User otherUser;
    private Chat sentChat, recieveChat;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference userRef, chatRef;
    ValueEventListener oneTimeChatEventListener;

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;

    private ImageView imageView_otherUser_profile_pic, single_chat_otherUser_sent_message_button;
    private TextView single_chat_otherUser_username;
    private EditText single_chat_otherUser_sent_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat);

        utility = new Utility();
        Intent intent = getIntent();
        otherUser = intent.getParcelableExtra(Constant.INTENT_KEY_TO_SINGLE_CHAT_ACTIVITY);
        otherUserEmailId = otherUser.getEmail();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        userRef = mFirebaseDatabase.getReference(Constant.FIREBASE_REFERENCE_USERS);
        chatRef = mFirebaseDatabase.getReference(Constant.FIREBASE_LOCATION_CHAT);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userEmailId = sharedpreferences.getString(Constant.SHARED_PREFRENCE_USER_EMAIL_ID, null);

        userIdByEmail = utility.emailToId(userEmailId);
        otherUserIdByEmail = utility.emailToId(otherUserEmailId);

        initializeScreen();
        fetchAllMessage();
    }

    public void initializeScreen() {
        imageView_otherUser_profile_pic = (ImageView) findViewById(R.id.single_chat_otherUser_profile_pic);
        single_chat_otherUser_sent_message_button = (ImageView) findViewById(R.id.single_chat_imageView_send_message);
        single_chat_otherUser_username = (TextView) findViewById(R.id.single_chat_otherUser_username);
        single_chat_otherUser_sent_message = (EditText) findViewById(R.id.single_chat_editText_message);

        single_chat_otherUser_username.setText(otherUser.getUserId());
        single_chat_otherUser_sent_message_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!single_chat_otherUser_sent_message.getText().toString().equals("")) {
                    sentMessage(single_chat_otherUser_sent_message.getText().toString());
                } else {

                }
            }
        });

        Picasso.with(getApplicationContext())
                .load(otherUser.getImage())
                .into(imageView_otherUser_profile_pic, new Callback() {
                    @Override
                    public void onSuccess() {
//                        Bitmap bitmap = ((BitmapDrawable) imageView_otherUser_profile_pic.getDrawable()).getBitmap();
//                        Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 180);
//                        imageView_otherUser_profile_pic.setImageBitmap(circularBitmap);
                    }

                    @Override
                    public void onError() {
                    }
                });

        listViewChatList = (ListView) findViewById(R.id.single_chat_listView);
        chatListAdapter = new ChatListAdapter(getApplicationContext(), R.layout.single_chat_message_sent_item, chatList);
        listViewChatList.setAdapter(chatListAdapter);
    }

    private void scrollMyListViewToBottom() {
        listViewChatList.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listViewChatList.setSelection(chatListAdapter.getCount() - 1);
            }
        });
    }

    public void sentMessage(final String message) {
        single_chat_otherUser_sent_message.setText("");
        Date d = new Date();
        String time = new SimpleDateFormat("HH:mm:ss").format(d);
        String date = new SimpleDateFormat("dd-MM-yyyy").format(d);
        long timeKey = System.currentTimeMillis();
        sentKey = timeKey+"";
        recieveKey = timeKey+"";
        sentChat = new Chat(userIdByEmail, otherUserIdByEmail, time, date, Constant.CHAT_MESSAGE_TYPE_SENT, message, Constant.CHAT_MESSAGE_SEEN_TYPE_SENT_NOTSEEN, time );
        recieveChat = new Chat(otherUserIdByEmail, userIdByEmail, time, date, Constant.CHAT_MESSAGE_TYPE_RECIEVE, message, Constant.CHAT_MESSAGE_SEEN_TYPE_RECIEVE, time);
        chatRef.child(userIdByEmail).child(otherUserIdByEmail).child(sentKey).setValue(sentChat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                chatRef.child(otherUserIdByEmail).child(userIdByEmail).child(recieveKey).setValue(recieveChat).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        fetchAllMessage();
                    }
                });
            }
        });
    }

    public void fetchAllMessage() {
        DatabaseReference child = chatRef.child(userIdByEmail).child(otherUserIdByEmail);
        oneTimeChatEventListener = chatRef.child(userIdByEmail).child(otherUserIdByEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long childrenCount = dataSnapshot.getChildrenCount();
                    int tempCount = 1;
                    chatList.clear();
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        Chat value = childDataSnapshot.getValue(Chat.class);
                        if (value.getMessageType().equals(Constant.CHAT_MESSAGE_TYPE_RECIEVE)) {
                            String key = childDataSnapshot.getKey();
                            Date d = new Date();
                            String time = new SimpleDateFormat("HH:mm:ss").format(d);
                            if(!value.getSeenType().equals(Constant.CHAT_MESSAGE_SEEN_TYPE_SENT_SEEN)){
                                chatRef.child(otherUserIdByEmail).child(userIdByEmail).child(key).child(Constant.CHAT_MESSAGE_SEEN_TIME).setValue(time);
                                chatRef.child(otherUserIdByEmail).child(userIdByEmail).child(key).child(Constant.CHAT_MESSAGE_SEEN_TYPE).setValue(Constant.CHAT_MESSAGE_SEEN_TYPE_SENT_SEEN);
                                chatRef.child(userIdByEmail).child(otherUserIdByEmail).child(key).child(Constant.CHAT_MESSAGE_SEEN_TIME).setValue(time);
                                chatRef.child(userIdByEmail).child(otherUserIdByEmail).child(key).child(Constant.CHAT_MESSAGE_SEEN_TYPE).setValue(Constant.CHAT_MESSAGE_SEEN_TYPE_SENT_SEEN);
                                Log.e("Notification status ", "message type recieve " + value.getContent() + tempCount + " and ccount = " + (int) childrenCount);
                            }
                            if (tempCount == (int) childrenCount) {
//                                createNotification(value.getContent(), utility.convertTimeForChat(value.getTime()));
                            }
                        }
                        tempCount++;
                        chatList.add(value);
//                        chatRef.child(userIdByEmail).child(otherUserIdByEmail).removeEventListener(oneTimeChatEventListener);
                    }
                    chatListAdapter.notifyDataSetChanged();
                    scrollMyListViewToBottom();
                } else {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
//        startChildListner();
    }
    public void startChildListner(){
        chatRef.child(userIdByEmail).child(otherUserIdByEmail).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    Chat value = dataSnapshot.getValue(Chat.class);
                    Log.e("Childtest notify ", "new message " + value.getContent() + dataSnapshot.getChildrenCount() + " ");
//                    chatList.add(value);
//                    chatListAdapter.notifyDataSetChanged();
                    if (value.getMessageType().equals(Constant.CHAT_MESSAGE_TYPE_RECIEVE)) {
//                                createNotification(value.getContent(), utility.convertTimeForChat(value.getTime()));
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        chatRef.child(userIdByEmail).removeEventListener(oneTimeChatEventListener);
        super.onPause();
    }

    public void createNotification(String message, String time) {
        Log.e("Notification status ", "inside Notification");
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.app_icon_main)
                        .setContentTitle("Message from " + otherUser.getUserId())
                        .setContentText(message + " at " + time);
        Intent resultIntent = new Intent(this, singleChatActivity.class);
        resultIntent.putExtra(Constant.INTENT_KEY_TO_SINGLE_CHAT_ACTIVITY, otherUser);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
// Sets an ID for the notification
        int mNotificationId = 001;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
