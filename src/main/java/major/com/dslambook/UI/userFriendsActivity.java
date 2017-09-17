package major.com.dslambook.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Pattern;

import major.com.dslambook.Adapter.UsersFriendsListAdapter;
import major.com.dslambook.Adapter.UsersListAdapter;
import major.com.dslambook.Pojo.Friend;
import major.com.dslambook.Pojo.User;
import major.com.dslambook.R;
import major.com.dslambook.Utility.Constant;
import major.com.dslambook.Utility.Utility;

public class userFriendsActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference userRef, friendRef;

    private ArrayList<User> usersList = new ArrayList<>();
    private UsersFriendsListAdapter usersFriendsListAdapter;

    private String userIdByEmail, userEmailid;
    private Utility utility;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    private ImageView imageViewUserProfile, imageViewAddPost, imageViewHome, imageViewSearch, imageViewFriends;
    private ListView listViewUsersList;
    private ArrayList<String> friendsIdList = new ArrayList<>() ;

    private int friendsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_friends);

        utility = new Utility();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        userRef = mFirebaseDatabase.getReference(Constant.FIREBASE_REFERENCE_USERS);
        friendRef = mFirebaseDatabase.getReference(Constant.FIREBASE_LOCATION_FRIEND);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userEmailid = sharedpreferences.getString(Constant.SHARED_PREFRENCE_USER_EMAIL_ID, null);
        userIdByEmail = utility.emailToId(userEmailid);

        initializeScreen();
        fetchAllFriends();
    }

    public void initializeScreen(){


        imageViewAddPost = (ImageView) findViewById(R.id.users_friends_imageView_add_post);
        imageViewUserProfile = (ImageView) findViewById(R.id.users_friends_imageView_profile);
        imageViewHome = (ImageView) findViewById(R.id.users_friends_imageView_Home);
        imageViewSearch = (ImageView) findViewById(R.id.users_friends_imageView_Search);

        imageViewAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userFriendsActivity.this, addPostActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        imageViewHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userFriendsActivity.this, homeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        imageViewUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userFriendsActivity.this, userProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userFriendsActivity.this, searchUserActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        listViewUsersList = (ListView) findViewById(R.id.users_friends_listView_users);
        usersFriendsListAdapter = new UsersFriendsListAdapter(getApplicationContext(), R.layout.users_friends_list_item, usersList);
        listViewUsersList.setAdapter(usersFriendsListAdapter);

        listViewUsersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(usersList.get(position).getEmail().equals(userEmailid)){

                } else {
                    Intent intent = new Intent(getApplicationContext(), otherProfileActivity.class);
                    intent.putExtra(Constant.INTENT_KEY_TO_OTHER_USER_PROFILE, usersList.get(position));
                    startActivity(intent);
                }
            }
        });
    }


    public void fetchAllFriends(){
        friendsIdList.clear();
        usersList.clear();
        friendRef.child(userIdByEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                        Friend value = childDataSnapshot.getValue(Friend.class);
                        friendsIdList.add(value.getOtherUserId());
                    }
                    fetchFriendsDetails();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void fetchFriendsDetails(){
        usersList.clear();
        friendsCount = 0;
        for(String list : friendsIdList){
            friendsCount++;
            Log.e("User's friends "," User id called "+list);
            userRef.child(list).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        User value = dataSnapshot.getValue(User.class);
                        Log.e("Search User ","User name "+value.getUserName());
                        usersList.add(value);
                        if(friendsCount==usersList.size()){
                            Log.e("Users List ","User name "+usersList.size());
                            usersFriendsListAdapter.notifyDataSetChanged();
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }
}
