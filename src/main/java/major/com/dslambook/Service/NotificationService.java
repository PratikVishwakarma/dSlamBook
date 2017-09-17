package major.com.dslambook.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import major.com.dslambook.Pojo.User;
import major.com.dslambook.Utility.Constant;
import major.com.dslambook.Utility.Utility;

/**
 * Created by prati on 20-Apr-17.
 */

public class NotificationService extends IntentService{

    private String userEmailId, userIdByEmail, otherUserEmailId, otherUserIdByEmail;
    private String sentKey, recieveKey;
    private Utility utility;
    private User otherUser;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference userRef, chatRef;
    ValueEventListener oneTimeChatEventListener;

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        utility = new Utility();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        userRef = mFirebaseDatabase.getReference(Constant.FIREBASE_REFERENCE_USERS);
        chatRef = mFirebaseDatabase.getReference(Constant.FIREBASE_LOCATION_CHAT);

        Log.e("IntentService ", "Service starts ");

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userEmailId = sharedpreferences.getString(Constant.SHARED_PREFRENCE_USER_EMAIL_ID, null);

        userIdByEmail = utility.emailToId(userEmailId);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(NotificationService.this, "Called ", Toast.LENGTH_SHORT).show();
                Log.e("IntentService ", "Called  ");
            }
        }, 5000);
        chatRef.child(userIdByEmail).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DatabaseReference ref = dataSnapshot.getRef();
                Log.e("IntentService ", "Data add on ref " + ref);
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
}
