package major.com.dslambook.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Pattern;

import major.com.dslambook.Adapter.UsersListAdapter;
import major.com.dslambook.Pojo.Post;
import major.com.dslambook.Pojo.User;
import major.com.dslambook.R;
import major.com.dslambook.Utility.Constant;
import major.com.dslambook.Utility.Utility;
import major.com.dslambook.otherProfileActivity;

import static java.security.AccessController.getContext;

public class searchUserActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference userRef;

    private ArrayList<User> usersList = new ArrayList<>();
    private UsersListAdapter usersListAdapter;

    private String idByEmail, userEmailid;
    private Utility utility;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    private SearchView searchViewUser;
    private ListView listViewUsersList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        utility = new Utility();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        userRef = mFirebaseDatabase.getReference(Constant.FIREBASE_LOCATION_USERS);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userEmailid = sharedpreferences.getString(Constant.SHARED_PREFRENCE_USER_EMAIL_ID, null);
        idByEmail = utility.emailToId(userEmailid);

        initializeScreen();
    }

    public void initializeScreen(){

        searchViewUser = (SearchView) findViewById(R.id.search_user_searchView_user);

        searchViewUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("Search User ",query);
                searchUserByName(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
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
                    intent.putExtra(Constant.INTENT_KEY_STRING_USERLIST_TO_PROFILE, usersList.get(position));
                    startActivity(intent);
                }
            }
        });
    }

    public void searchUserByName(final String username){
        Log.e("Search User ","search User By Name called.");
        userRef.orderByValue().startAt(username).addValueEventListener(new ValueEventListener() {
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
