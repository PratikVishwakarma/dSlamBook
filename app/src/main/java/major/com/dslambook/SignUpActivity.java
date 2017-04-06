package major.com.dslambook;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import major.com.dslambook.Pojo.User;
import major.com.dslambook.UI.homeActivity;
import major.com.dslambook.Utility.Constant;
import major.com.dslambook.Utility.Utility;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RC_SIGN_IN = 1;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference usersRef;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    //UI elements
    TextView username, dateOfBirth;
    ImageView profile_pic;
    Button button_next;
    public FirebaseUser user;
    Spinner spinner_gender;

    private Calendar calendar;
    private int year, month, day, sYear;
    private String sGender = "notSlectedYet", idByEmail;
    private Utility utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = mFirebaseDatabase.getReference(Constant.FIREBASE_LOCATION_USERS);

        mFirebaseAuth = FirebaseAuth.getInstance();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        utility = new Utility();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    Toast.makeText(getApplicationContext(), user.getDisplayName(), Toast.LENGTH_SHORT).show();
                    idByEmail = utility.emailToId(user.getEmail());
                    usersRef.child(idByEmail).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                User currentUser = dataSnapshot.getValue(User.class);
                                if(currentUser.getIsProvideBasicInformation() == Constant.IS_PROVIDE_BASCI_INFORMATION){
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString(Constant.SHARED_PREFRENCE_USER_EMAIL_ID, user.getEmail());
                                    editor.putString(Constant.SHARED_PREFRENCE_USER_USERNAME, user.getDisplayName());
                                    editor.putInt(Constant.SHARED_PREFRENCE_USER_BASIC_INFORMATION_STATUS, 1);
                                    editor.commit();
                                    Intent intent = new Intent(SignUpActivity.this, homeActivity.class);
                                    startActivity(intent);
                                }
                            } else{
                                //Signed IN
                                initializeScreen();
                                String name = user.getDisplayName();
                                username.setText(name);
                                Picasso.with(getApplicationContext()).load(user.getPhotoUrl()).into(profile_pic);
                                Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
                                button_next.setVisibility(View.VISIBLE);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
//                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER
                                    )
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
        initializeScreen();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
//        Toast.makeText(getApplicationContext(), "ca", Toast.LENGTH_SHORT).show();
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
//                     arg1 = year;
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private void showDate(int year, int month, int day) {
        sYear = year;
        dateOfBirth.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        sGender = parent.getItemAtPosition(position).toString();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void initializeScreen(){
        button_next = (Button) findViewById(R.id.button_next);
        username =(TextView) findViewById(R.id.textView_username);
        dateOfBirth =(TextView) findViewById(R.id.textView_selected_dob);
        profile_pic = (ImageView) findViewById(R.id.imageView_profile_pic);
        spinner_gender = (Spinner) findViewById(R.id.spinner_gender);

        // Spinner click listener
        spinner_gender.setOnItemSelectedListener(this);

        button_next.setVisibility(View.INVISIBLE);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);

        // Spinner Drop down elements
        List<String> gender = new ArrayList<String>();
        gender.add("Female");
        gender.add("Male");

        // Creating adapter for spinner
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gender);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_gender.setAdapter(dataAdapter);
        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),sYear+"", Toast.LENGTH_SHORT).show();
                if (sYear <= 2010){
                    Date d = new Date();
                    String dateAndTime = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(d);
                    Toast.makeText(getApplicationContext(),"All okay..", Toast.LENGTH_SHORT).show();
                    User user1 = new User(user.getDisplayName(), user.getEmail(),dateOfBirth.getText().toString(),
                            sGender, user.getPhotoUrl().toString(), dateAndTime, Constant.IS_PROVIDE_BASCI_INFORMATION, 0);

                    usersRef.child(idByEmail).setValue(user1).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"Inserted", Toast.LENGTH_SHORT).show();
                        }
                    });
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(Constant.SHARED_PREFRENCE_USER_EMAIL_ID, user.getEmail());
                    editor.putInt(Constant.SHARED_PREFRENCE_USER_BASIC_INFORMATION_STATUS, 1);
                    editor.commit();
                    Intent intent = new Intent(SignUpActivity.this, homeActivity.class);
                    startActivity(intent);
                } else{
                    Toast.makeText(getApplicationContext(),"Invalid Date of birth", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
