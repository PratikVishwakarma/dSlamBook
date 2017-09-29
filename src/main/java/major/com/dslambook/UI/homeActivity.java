package major.com.dslambook.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import major.com.dslambook.Adapter.PostsListRecyclerAdapter;
import major.com.dslambook.Pojo.Home;
import major.com.dslambook.Pojo.Like;
import major.com.dslambook.Pojo.Post;
import major.com.dslambook.Pojo.PostImage;
import major.com.dslambook.Pojo.User;
import major.com.dslambook.Adapter.PostsListAdapter;
import major.com.dslambook.R;
import major.com.dslambook.SignUpActivity;
import major.com.dslambook.Utility.Constant;
import major.com.dslambook.Utility.Utility;

public class homeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference postsRef, homeRef, userRef, likeRef, postImageRef, commentCountRef;
    private ValueEventListener homeRefValueEventListener;

    private String userIdByEmail, userId = "";
    private Utility utility;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    private ImageView imageViewUserProfile, imageViewAddPost, imageViewHome, imageViewSearch, imageViewFriends;
    private SwipeRefreshLayout swipeRefreshLayout;

//    private ListView postsListView;
    private RecyclerView postsListView;
    private PostsListAdapter postsListAdapter;
    private PostsListRecyclerAdapter rpostsListAdapter;
    private ArrayList<Post> postsList = new ArrayList<>();
    private ArrayList<Home> homeList = new ArrayList<>();
    private Map<String, Integer> postPositionList = new HashMap<>();
    private Map<String, User> userList = new HashMap<>();
    private Map<String, String> postImageList = new HashMap<>();
    private Map<String, Integer> commentCountList = new HashMap<>();
    private Map<String, Like> likeStatusList = new HashMap<>();

    int postDetailCount = 0, commentCount = 0, likeCount = 0, userCount = 0, postImageCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        utility = new Utility();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        postsRef = mFirebaseDatabase.getReference(Constant.FIREBASE_REFERENCE_POST);
        homeRef = mFirebaseDatabase.getReference(Constant.FIREBASE_REFERENCE_HOME);
        userRef = mFirebaseDatabase.getReference(Constant.FIREBASE_REFERENCE_USERS);
        likeRef = mFirebaseDatabase.getReference(Constant.FIREBASE_REFERENCE_LIKE);

        String loginStatus = getLoginStatus();
        if(loginStatus.equals(Constant.LOGIN_STATUS_LOGIN)) {
            userId = getUserId();
            if (userId.equals("") || userId == null){
                goToSignupActivity();
            }
            if(isNetworkAvailable()) {
                Log.e("Post Act"," "+ userId);
                initializeScreen();
                loadAllHomePost();
            }else{

            }
        } else{
            goToSignupActivity();
        }
//        homeRefValueEventListener = homeRef.child(userId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                homeList.clear();
//                if (dataSnapshot.exists()) {
//                    postCount = 0;
//                    homeList.clear();
//                    postsList.clear();
//                    fullSinglePostsList.clear();
//                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
//                        Home value = (Home) childDataSnapshot.getValue(Home.class);
//                        final String[] idFromPostId = utility.getIdFromPostId(value.getPostId());
//                        Log.e("Home list ", "id : " + idFromPostId[0] + "  type : " + value.getPostType());
//                        homeList.add(value);
//                        postsRef.child(idFromPostId[0]).child(value.getPostId()).addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                final Post value = dataSnapshot.getValue(Post.class);
//                                postsList.add(value);
//                                final FullSinglePost fullSinglePost = new FullSinglePost();
//                                fullSinglePost.setUserId(value.getUserId());
//                                fullSinglePost.setPostId(value.getPostId());
//                                fullSinglePost.setContent(value.getContent());
//                                fullSinglePost.setDate(value.getDate());
//                                fullSinglePost.setLike(value.getLike());
//                                fullSinglePost.setTime(value.getTime());
//
//                                postCount = postCount + 1;
//
//                                userRef.child(idFromPostId[0]).addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        User userVal = dataSnapshot.getValue(User.class);
//                                        fullSinglePost.setUserName(userVal.getUserName());
//                                        fullSinglePost.setUserProfilePic(userVal.getImage());
//
//                                        userDetailCount = userDetailCount + 1;
//                                        likeRef.child(userIdByEmail).child(value.getPostId()).addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                if (dataSnapshot.exists()) {
//                                                    fullSinglePost.setLikeStatus(Boolean.TRUE);
//                                                    fullSinglePostsList.add(fullSinglePost);
//                                                } else {
//                                                    fullSinglePost.setLikeStatus(Boolean.FALSE);
//                                                    fullSinglePostsList.add(fullSinglePost);
//                                                }
//                                                likeStatusCount = likeStatusCount + 1;
//                                                Log.e("Counter ", "counter called : " + postCount + " val " + postsList.size() + " hl " + homeList.size());
//                                                if (postCount == homeList.size() && userDetailCount == homeList.size() && likeStatusCount == homeList.size()) {
//                                                    Log.e("check Counter ", "called : ");
//                                                    Collections.reverse(fullSinglePostsList);
//                                                    postsListAdapter.notifyDataSetChanged();
////                                                        rpostsListAdapter.notifyDataSetChanged();
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//
//                                            }
//                                        });
//
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
//
//                                Log.e("Post list ", "id : " + value.getContent() + "  image : " + value.getImage());
////                                postCount = postCount + 1;
////                                Log.e("Counter ","counter called : "+postCount+" val "+postsList.size()+" hl "+homeList.size());
////
////                                if(postCount >= homeList.size()){
////                                    Log.e("check Counter ","called : ");
////                                    postsRef.removeEventListener(this);
////                                    Collections.reverse(postsList);
////                                    postsListAdapter.notifyDataSetChanged();
////                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//                            }
//                        });
//                    }
//                    homeRef.removeEventListener(this);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
    }

    public void loadAllHomePost(){
        swipeRefreshLayout.setRefreshing(true);
        postDetailCount = 0;
        postImageCount = 0;
        commentCount = 0;
        likeCount = 0;
        userCount = 0;
        rpostsListAdapter.notifyDataSetChanged();
        Log.e("homeAct ", "check 1");
        homeRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                homeList.clear();
                postsList.clear();
                userList.clear();
                commentCountList.clear();
                postImageList.clear();
                likeStatusList.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                        Home home = childDataSnapshot.getValue(Home.class);
                        homeList.add(home);
                    }
                    Collections.reverse(homeList);
                    loadPostDetailsFromHome();
                } else{}
                homeRef.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void loadPostDetailsFromHome(){
        if(postDetailCount < homeList.size()){
            postsRef.child(homeList.get(postDetailCount).getOtherUserId()).
                    child(homeList.get(postDetailCount).getPostId()).
                    addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                Post singlePost = dataSnapshot.getValue(Post.class);
                                postsList.add(singlePost);
                                postPositionList.put(singlePost.getPostId(),postDetailCount);
                                postDetailCount++;
                                if(homeList.size() == postDetailCount){
                                    loadPostImageDetailsFromPost();
                                }else{
                                    loadPostDetailsFromHome();
                                }
                            } else{}
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
        }
    }

    public void loadPostImageDetailsFromPost(){
        if(postImageCount < homeList.size()){
            postImageRef = postsRef.child(homeList.get(postImageCount).getOtherUserId()).
                    child(homeList.get(postImageCount).getPostId()).
                    child("image");
            postImageRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.e("homeAct ", "called like image");
                            if (dataSnapshot.exists()) {
                                String allImageWithLike = "";
                                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                    PostImage postImage = childDataSnapshot.getValue(PostImage.class);
                                    allImageWithLike = allImageWithLike + postImage.getImage() + Constant.STRING_POST_IMAGE_LIKE_DIFFERENTIATOR +
                                            postImage.getLike() + Constant.STRING_POST_IMAGE_LIKE_DIFFERENTIATOR +
                                            postImage.getImageUrl() + Constant.STRING_POST_IMAGE_DIFFERENTIATOR;
                                }
                                postImageRef.removeEventListener(this);
                                postImageList.put(postsList.get(postImageCount).getPostId(), allImageWithLike);
                                postImageCount++;
                                if (homeList.size() == postImageCount) {
                                    loadUserDetailsFromHome();
                                } else {
                                    loadPostImageDetailsFromPost();
                                }
                            } else {
                                postImageRef.removeEventListener(this);
                            }
                            postImageRef.removeEventListener(this);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    public void loadUserDetailsFromHome(){
        if(userCount < homeList.size()){
            userRef.child(homeList.get(userCount).getOtherUserId()).
                    addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                User user = dataSnapshot.getValue(User.class);
                                userList.put(homeList.get(userCount).getPostId(),user);
                                userCount++;
                                if(homeList.size() == userCount){
                                    loadAllCommentCount();
                                }else{
                                    loadUserDetailsFromHome();
                                }
                            } else{}
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
        }
    }

    public void loadAllCommentCount(){
        if(commentCount < postsList.size()){
            commentCountRef = postsRef.child(postsList.get(commentCount).getUserId()).
                    child(postsList.get(commentCount).getPostId()).
                    child(Constant.FIREBASE_REFERENCE_COMMENT);
            commentCountRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                int totalLastComment = (int) dataSnapshot.getChildrenCount();
                                commentCountList.put(postsList.get(commentCount).getPostId(), totalLastComment);
                                commentCount++;
                                if(postsList.size() == commentCount){
                                    loadPostLikeStatus();
                                }else{
                                    loadAllCommentCount();
                                }
                            } else{
                                commentCountList.put(postsList.get(commentCount).getPostId(), 0);
                                commentCount++;
                                if(postsList.size() == commentCount){
                                    loadPostLikeStatus();
                                }else{
                                    loadAllCommentCount();
                                }
                            }
                            mFirebaseDatabase.getReference().removeEventListener(this);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            commentCountList.put(postsList.get(commentCount).getPostId(), 0);
                            commentCount++;
                            if(postsList.size() == commentCount){
                                loadPostLikeStatus();
                            }else{
                                loadAllCommentCount();
                            }
                        }
                    });
        }
    }

    public void loadPostLikeStatus(){
        if(likeCount < postsList.size()){
            likeRef.child(userId).child(homeList.get(likeCount).getPostId()).
                    addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                Like likePost = dataSnapshot.getValue(Like.class);
                                likeStatusList.put(postsList.get(likeCount).getPostId(), likePost);
                                likeCount++;
                                if(postsList.size() == likeCount){
//                                    printAllPostAndCommentCount();
                                    rpostsListAdapter.notifyDataSetChanged();
                                    swipeRefreshLayout.setRefreshing(false);
                                }else{
                                    loadPostLikeStatus();
                                }
                            } else{
                                Like likePost = new Like("NA" , "NA");
                                likeStatusList.put(postsList.get(likeCount).getPostId(), likePost);
                                likeCount++;
                                if(postsList.size() == likeCount){
//                                    printAllPostAndCommentCount();
                                    rpostsListAdapter.notifyDataSetChanged();
                                    swipeRefreshLayout.setRefreshing(false);
                                }else{
                                    loadPostLikeStatus();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Like likePost = new Like("NA", "NA");
                            likeStatusList.put(postsList.get(likeCount).getPostId(), likePost);
                            likeCount++;
                            if(postsList.size() == likeCount){
//                                printAllPostAndCommentCount();
                                rpostsListAdapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                            }else{
                                loadPostLikeStatus();
                            }
                        }
                    });
        }
    }

    public void printAllPostAndCommentCount(){
        for(int i = 0 ; i < homeList.size(); i++){
            String ls = "";
            Like likeStatus = likeStatusList.get(postsList.get(i).getPostId());
            if(likeStatus.getImageId().equals("NA")){
                ls = "Not Liked";
            }else{
                ls = "Liked";
            }

            Log.e("Post "," User Id : "+postsList.get(i).getUserId()+" | post Id : "+ postsList.get(i).getPostId()+
                    " | com count "+ commentCountList.get(postsList.get(i).getPostId())+" | Like Status : "+ ls +
                    " | user Name "+ userList.get(postsList.get(i).getPostId()).getName());

            String postlikecode = postImageList.get(postsList.get(i).getPostId());
            String[] split = postlikecode.split(Pattern.quote(Constant.STRING_POST_IMAGE_DIFFERENTIATOR));
            for(int j = 0 ; j < split.length; j++) {
                String[] likeSplit = split[j].split(Pattern.quote(Constant.STRING_POST_IMAGE_LIKE_DIFFERENTIATOR));
                Log.e("Post Image "," "+ likeSplit[0]+" likes = "+likeSplit[1]+" Url = "+likeSplit[2]);

            }
        }
        rpostsListAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {

        homeRef.removeEventListener(homeRefValueEventListener);
        super.finish();
    }

    public void initializeScreen(){
        imageViewAddPost = (ImageView) findViewById(R.id.imageView_add_post);
        imageViewUserProfile = (ImageView) findViewById(R.id.imageView_user_profile);
        imageViewHome = (ImageView) findViewById(R.id.imageView_Home);
        imageViewSearch = (ImageView) findViewById(R.id.imageView_Search);
        imageViewFriends = (ImageView) findViewById(R.id.imageView_Friends);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

//        postsListView = (ListView) findViewById(R.id.listView_posts_list);
//        postsListAdapter = new PostsListAdapter(getApplicationContext(), R.layout.item_post_one, postsList,
//                userList,
//                commentCountList,
//                likeStatusList,
//                postImageList);
//        postsListView.setAdapter(postsListAdapter);
//
        postsListView = (RecyclerView) findViewById(R.id.listView_posts_list);
        android.support.v7.widget.LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        postsListView.setHasFixedSize(true);
        rpostsListAdapter = new PostsListRecyclerAdapter(getApplicationContext(),
                postsList,
                userList,
                commentCountList,
                likeStatusList,
                postImageList,
                userId,
                postPositionList);
        postsListView.setLayoutManager(mLayoutManager);
        postsListView.setItemAnimator(new DefaultItemAnimator());
        postsListView.setAdapter(rpostsListAdapter);

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

        imageViewUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homeActivity.this, userProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        imageViewFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homeActivity.this, userFriendsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

    @Override
    public void onRefresh() {
        loadAllHomePost();
    }
}
