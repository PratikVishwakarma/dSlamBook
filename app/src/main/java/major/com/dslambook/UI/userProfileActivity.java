package major.com.dslambook.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import major.com.dslambook.Adapter.PostsListAdapter;
import major.com.dslambook.R;
import major.com.dslambook.Utility.Constant;
import major.com.dslambook.Utility.Utility;

public class userProfileActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference postsRef, homeRef, userRef, likeRef;

    private String userIdByEmail;
    private Utility utility;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    private ImageView imageViewUserProfile, imageViewAddPost, imageViewHome, imageViewSearch, imageViewFriends;

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
    }
}
