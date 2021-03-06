package major.com.dslambook.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import major.com.dslambook.Pojo.FullSinglePost;
import major.com.dslambook.Pojo.Home;
import major.com.dslambook.Pojo.Post;
import major.com.dslambook.Pojo.User;
import major.com.dslambook.PostsListAdapter;
import major.com.dslambook.R;
import major.com.dslambook.SignUpActivity;
import major.com.dslambook.Utility.Constant;
import major.com.dslambook.Utility.Utility;

public class homeActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference postsRef, homeRef, userRef;

    private String idByEmail;
    private Utility utility;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    private ImageView imageViewUserProfile, imageViewAddPost, imageViewHome, imageViewSearch, imageViewFriends;

    private ListView postsListView;
    private PostsListAdapter postsListAdapter;
    private ArrayList<Post> postsList = new ArrayList<>();
    private ArrayList<Home> homeList = new ArrayList<>();
    private ArrayList<FullSinglePost> fullSinglePostsList = new ArrayList<>();
    int postCount, userDetailCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        utility = new Utility();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        postsRef = mFirebaseDatabase.getReference(Constant.FIREBASE_LOCATION_POST);
        homeRef = mFirebaseDatabase.getReference(Constant.FIREBASE_LOCATION_HOME);
        userRef = mFirebaseDatabase.getReference(Constant.FIREBASE_LOCATION_USERS);

        initializeScreen();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String getUserEmailid = sharedpreferences.getString(Constant.SHARED_PREFRENCE_USER_EMAIL_ID, null);


        if(getUserEmailid == null){
            Toast.makeText(getApplicationContext(), "User is null", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(homeActivity.this, SignUpActivity.class);
            startActivity(intent);
        } else{
//            Toast.makeText(getApplicationContext(), "User id is "+getUserEmailid, Toast.LENGTH_SHORT).show();
            idByEmail = utility.emailToId(getUserEmailid);
            homeRef.child(idByEmail).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    homeList.clear();
                    if(dataSnapshot.exists()){
                        postCount = 0;
                        homeList.clear();
                        postsList.clear();
                        fullSinglePostsList.clear();
                        for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                            Home value = (Home) childDataSnapshot.getValue(Home.class);
                            final String[] idFromPostId = utility.getIdFromPostId(value.getPostId());
                            Log.e("Home list ","id : "+ idFromPostId[0]+ "  type : " + value.getPostType());
                            homeList.add(value);
                            postsRef.child(idFromPostId[0]).child(value.getPostId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Post value = dataSnapshot.getValue(Post.class);
                                    postsList.add(value);
                                    final FullSinglePost fullSinglePost = new FullSinglePost();
                                    fullSinglePost.setUserId(value.getUserId());
                                    fullSinglePost.setPostId(value.getPostId());
                                    fullSinglePost.setContent(value.getContent());
                                    fullSinglePost.setDate(value.getDate());
                                    fullSinglePost.setImage(value.getImage());
                                    fullSinglePost.setLike(value.getLike());
                                    fullSinglePost.setTime(value.getTime());

                                    postCount = postCount + 1;

                                    userRef.child(idFromPostId[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            User userVal = dataSnapshot.getValue(User.class);
                                            fullSinglePost.setUserName(userVal.getUserName());
                                            fullSinglePost.setUserProfilePic(userVal.getImageUrl());
                                            fullSinglePostsList.add(fullSinglePost);
                                            userDetailCount = userDetailCount + 1;
                                            Log.e("Counter ","counter called : "+postCount+" val "+postsList.size()+" hl "+homeList.size());
                                            if(postCount == homeList.size() && userDetailCount == homeList.size()){
                                                Log.e("check Counter ","called : ");
                                                Collections.reverse(fullSinglePostsList);
                                                postsListAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    Log.e("Post list ","id : "+ value.getContent()+ "  image : " + value.getImage());
//                                postCount = postCount + 1;
//                                Log.e("Counter ","counter called : "+postCount+" val "+postsList.size()+" hl "+homeList.size());
//
//                                if(postCount >= homeList.size()){
//                                    Log.e("check Counter ","called : ");
//                                    postsRef.removeEventListener(this);
//                                    Collections.reverse(postsList);
//                                    postsListAdapter.notifyDataSetChanged();
//                                }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                        homeRef.removeEventListener(this);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }


    }

    public void initializeScreen(){
        imageViewAddPost = (ImageView) findViewById(R.id.imageView_add_post);
        imageViewUserProfile= (ImageView) findViewById(R.id.imageView_user_profile);
        imageViewHome= (ImageView) findViewById(R.id.imageView_Home);
        imageViewSearch= (ImageView) findViewById(R.id.imageView_Search);
        imageViewFriends= (ImageView) findViewById(R.id.imageView_Friends);

        postsListView = (ListView) findViewById(R.id.listView_posts_list);
        postsListAdapter = new PostsListAdapter(getApplicationContext(), R.layout.home_post_list_item, fullSinglePostsList);
        postsListView.setAdapter(postsListAdapter);

        imageViewAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homeActivity.this, addPostActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homeActivity.this, searchUserActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
