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

import major.com.dslambook.Adapter.UsersListAdapter;
import major.com.dslambook.Pojo.User;
import major.com.dslambook.R;
import major.com.dslambook.Utility.Constant;
import major.com.dslambook.Utility.Utility;

public class userFriendsActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference userRef;

    private ArrayList<User> usersList = new ArrayList<>();
    private UsersListAdapter usersListAdapter;

    private String idByEmail, userEmailid;
    private Utility utility;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    private ImageView imageViewUserProfile, imageViewAddPost, imageViewHome, imageViewSearch, imageViewFriends;
    private ListView listViewUsersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_friends);

        utility = new Utility();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        userRef = mFirebaseDatabase.getReference(Constant.FIREBASE_LOCATION_USERS);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userEmailid = sharedpreferences.getString(Constant.SHARED_PREFRENCE_USER_EMAIL_ID, null);
        idByEmail = utility.emailToId(userEmailid);
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

        listViewUsersList = (ListView) findViewById(R.id.search_user_listView_users);
        usersListAdapter = new UsersListAdapter(getApplicationContext(), R.layout.users_list_item, usersList);
        listViewUsersList.setAdapter(usersListAdapter);
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

    public void searchUsersFriends(final String username){
        Log.e("Search User ","search User By Name called.");
        userRef.orderByValue().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersList.clear();
                if(dataSnapshot.exists()){
                    Log.e("Search User ","search User data exist "+dataSnapshot.getChildrenCount());
                    for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                        User value = childDataSnapshot.getValue(User.class);
                        Log.e("Search User ","User name "+value.getUserName());
                        String s1 = value.getUserName();
                        if(Pattern.compile(Pattern.quote(username), Pattern.CASE_INSENSITIVE).matcher(s1).find()){
                            Log.e("Search User ","search User By Name called." + value.getUserName());
                            usersList.add(value);
                        }
                    }
                }
                usersListAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
