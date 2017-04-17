package major.com.dslambook.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import major.com.dslambook.Adapter.PostsListAdapter;
import major.com.dslambook.Pojo.User;
import major.com.dslambook.R;
import major.com.dslambook.Utility.Constant;
import major.com.dslambook.Utility.Utility;

public class userProfileActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference postsRef, homeRef, userRef, likeRef;

    private String userEmailId, userIdByEmail;
    private Utility utility;
    private User user;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    private ImageView imageViewUserProfile, imageViewAddPost, imageViewHome, imageViewSearch, imageViewFriends;
    private ImageView imageViewUserProfilePic;
    private TextView textViewUserUsername, textViewUserDateOfBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        utility = new Utility();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        postsRef = mFirebaseDatabase.getReference(Constant.FIREBASE_LOCATION_POST);
        homeRef = mFirebaseDatabase.getReference(Constant.FIREBASE_LOCATION_HOME);
        userRef = mFirebaseDatabase.getReference(Constant.FIREBASE_LOCATION_USERS);
        likeRef = mFirebaseDatabase.getReference(Constant.FIREBASE_LOCATION_LIKE);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userEmailId = sharedpreferences.getString(Constant.SHARED_PREFRENCE_USER_EMAIL_ID, null);
        userIdByEmail = utility.emailToId(userEmailId);

        userRef.child(userIdByEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                textViewUserUsername.setText(user.getUserName());
                textViewUserDateOfBirth.setText(user.getDob());
                Picasso.with(getApplicationContext())
                        .load(user.getImageUrl())
                        .into(imageViewUserProfilePic);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        initializeScreen();
    }

    @Override
    public void onBackPressed() {
        super.finish();
    }

    public void initializeScreen() {
        imageViewAddPost = (ImageView) findViewById(R.id.imageView_user_profile_add_post);
        imageViewUserProfile = (ImageView) findViewById(R.id.imageView_user_profile_user_profile);
        imageViewHome = (ImageView) findViewById(R.id.imageView_user_profile_Home);
        imageViewSearch = (ImageView) findViewById(R.id.imageView_user_profile_Search);
        imageViewFriends = (ImageView) findViewById(R.id.imageView_user_profile_Friends);

        imageViewUserProfilePic = (ImageView) findViewById(R.id.imageView_userProfile_profile_pic);
        textViewUserUsername = (TextView) findViewById(R.id.textView_userProfile_username);
        textViewUserDateOfBirth = (TextView) findViewById(R.id.textView_userProfile_dateOfBirth);

//        postsListView = (ListView) findViewById(R.id.listView_posts_list);
//        postsListAdapter = new PostsListAdapter(getApplicationContext(), R.layout.home_post_list_item, fullSinglePostsList);
//        postsListView.setAdapter(postsListAdapter);

        imageViewAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userProfileActivity.this, addPostActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userProfileActivity.this, searchUserActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        imageViewHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userProfileActivity.this, homeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        imageViewFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userProfileActivity.this, userFriendsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
