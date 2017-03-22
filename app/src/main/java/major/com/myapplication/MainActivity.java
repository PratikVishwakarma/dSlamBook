package major.com.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RC_SIGN_IN = 1;


    //UI elements
    TextView username, dateOfBirth;
    ImageView profile_pic;
    Button button_next;
    public FirebaseUser user;
    Spinner spinner_gender;

    private Calendar calendar;
    private int year, month, day, sYear;
    private String sGender = "notSlectedYet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_next = (Button) findViewById(R.id.button_next);
        username =(TextView) findViewById(R.id.textView_username);
        dateOfBirth =(TextView) findViewById(R.id.textView_selected_dob);
        profile_pic = (ImageView) findViewById(R.id.imageView_profile_pic);
        spinner_gender = (Spinner) findViewById(R.id.spinner_gender);

        // Spinner click listener
        spinner_gender.setOnItemSelectedListener(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
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



        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    //Signed IN
                    String name = user.getDisplayName();

                    username.setText(name);
                    Picasso.with(getApplicationContext()).load(user.getPhotoUrl()).into(profile_pic);
                    Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
                    button_next.setVisibility(View.VISIBLE);
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
        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),sYear+"", Toast.LENGTH_SHORT).show();
                if (sYear <= 2010){
                    Toast.makeText(getApplicationContext(),"All okay..", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(getApplicationContext(),"Invalid Date of birth", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
}
