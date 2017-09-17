package major.com.dslambook;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import major.com.dslambook.Pojo.User;
import major.com.dslambook.UI.homeActivity;
import major.com.dslambook.Utility.Constant;
import major.com.dslambook.Utility.ImageConverter;
import major.com.dslambook.Utility.Utility;

public class SignUpActivity extends AppCompatActivity{

    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RC_SIGN_IN = 1;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference usersRef;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageRef;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    private String userId = null, errorMsgUserName = "";
    private Bitmap userPicBitmap = null;
    //UI elements
    TextView username, name, bio;
    ImageView profile_pic;
    Button button_next;

    ProgressDialog progress;
    public FirebaseUser user;

    private boolean usernameAvailablestatus = false;

    private Utility utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = mFirebaseDatabase.getReference(Constant.FIREBASE_REFERENCE_USERS);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageRef = mFirebaseStorage.getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        utility = new Utility();

        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.setCancelable(false);

        String loginStatus = getLoginStatus();
        if(loginStatus.equals(Constant.LOGIN_STATUS_LOGIN)){
            userId = getUserId();
            if (userId.equals("") || userId == null){
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setProviders(
                                        AuthUI.GOOGLE_PROVIDER)
                                .build(),
                        RC_SIGN_IN);
            } else{
//              already logged in
                goToHomeActivity();
            }
        } else{
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(
                                    AuthUI.GOOGLE_PROVIDER)
                            .build(),
                    RC_SIGN_IN);
        }

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    printToast("dis name : "+user.getDisplayName().toString());
                    progress.setMessage("Checking user status");
                    progress.show();
                    Query query = usersRef.orderByChild("email").equalTo(user.getEmail().toString());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                Log.e("SIN user ", "count is "+ dataSnapshot.getChildrenCount());
                                for (DataSnapshot allUser: dataSnapshot.getChildren()){
                                    User value = allUser.getValue(User.class);
                                    Log.e("SIN user ", "user is "+ value.getUserName());
                                    Log.e("SIN user ", "email is "+ value.getEmail());
                                    printToast(value.getUserName().toString());
                                    setUserId(value.getUserName(), value.getEmail());
                                    goToHomeActivity();
                                }
                            } else{
                                initializeScreen();
                                name.setText(user.getDisplayName().toString());
                                Picasso.with(getApplicationContext())
                                        .load(user.getPhotoUrl())
                                        .into(profile_pic, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                userPicBitmap = ((BitmapDrawable) profile_pic.getDrawable()).getBitmap();
                                                Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(userPicBitmap, 180);
                                                profile_pic.setImageBitmap(circularBitmap);
                                                progress.cancel();
                                            }
                                            @Override
                                            public void onError() {}
                                        });
                                button_next.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
//                    user is null
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



    public void initializeScreen(){
        button_next = (Button) findViewById(R.id.button_next);
        username =(TextView) findViewById(R.id.signup_editText_username);
        name =(TextView) findViewById(R.id.signup_editText_name);
        bio =(TextView) findViewById(R.id.signup_editText_bio);
        profile_pic = (ImageView) findViewById(R.id.imageView_profile_pic);

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUserProcess(username.getText().toString().toLowerCase());
            }
        });
    }

    public void createUserProcess(final String userName){
        if(name.getText().toString().trim().length() == 0 || userName.trim().length() == 0){

        } else{
            if(userNameValidation(userName.toLowerCase())){
                usersRef.child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            printToast("Username Not available");
                        }else{
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            userPicBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            final byte[] uImagedata = baos.toByteArray();

                            UploadTask uploadTask = mStorageRef.child(Constant.FIREBASE_IMAGE_REFERENCE_USER).
                                    child(userName).
                                    child(userName + ".jpg").
                                    putBytes(uImagedata);

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Date d = new Date();
                                    String dateAndTime = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(d);
                                    User user1 = new User(name.getText().toString(),
                                            userName,
                                            user.getEmail(),
                                            "NA",
                                            "NA",
                                            userName+".jpg",
                                            dateAndTime,
                                            Constant.IS_PROVIDE_BASCI_INFORMATION,
                                            bio.getText().toString());
                                    usersRef.child(userName).setValue(user1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            setUserId(userName, user.getEmail().toString());
                                            goToHomeActivity();
                                            Toast.makeText(getApplicationContext(),"Inserted", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            } else{
                printToast(errorMsgUserName);
            }
        }
    }

    public boolean userNameValidation(String userName){
        if(userName.trim().length() != 0){
            if(userName.trim().length() > 6 && userName.trim().length() < 40){
                if(userName.contains(" ") || userName.contains("@") || userName.contains("#") ||
                        userName.contains("%") || userName.contains(".") || userName.contains("$") ||
                        userName.contains("[") || userName.contains("]") || userName.contains("/") ||
                        userName.contains("*") || userName.contains("^") || userName.contains("+")){
                    // un supported character
                    usernameAvailablestatus = false;
                    errorMsgUserName = "space , @, #, %, ., $, [, ], /, *, ^, + not allow in username";
                } else {
                    for (int i = 0; i < userName.length(); i++){
                        int chk = (int)userName.charAt(i);
                        if(chk != 127){
                            if(chk > 31){
                                usernameAvailablestatus = true;
                            } else{
                                errorMsgUserName = "null or undefined character not allow in username";
                                usernameAvailablestatus = false;
                            }
                        }else{
                            // un supported character
                            errorMsgUserName = "null or undefined character not allow in username";
                            usernameAvailablestatus = false;
                        }
                    }
                }
            } else{
                usernameAvailablestatus = false;
                //  too short/long
                errorMsgUserName = "username should be greater than 6 or less than 40 character";
            }
        } else{
//            not blank
            errorMsgUserName = "incorrect username";
            usernameAvailablestatus = false;
        }
        return usernameAvailablestatus;
    }

    public String getLoginStatus(){
        return sharedpreferences.getString(Constant.SHARED_PREFRENCE_LOGIN_STATUS, Constant.LOGIN_STATUS_LOGOUT);
    }
    public String getUserId(){
        return sharedpreferences.getString(Constant.SHARED_PREFRENCE_USER_ID, "");
    }
    public boolean getUserIdCreationStatus(){
        return sharedpreferences.getBoolean(Constant.SHARED_PREFRENCE_SIGNUP_COMPLETE, false);
    }
    public void printToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void setUserId(String userId, String userEmailId){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constant.SHARED_PREFRENCE_USER_EMAIL_ID, userEmailId);
        editor.putString(Constant.SHARED_PREFRENCE_USER_ID, userId);
        editor.putBoolean(Constant.SHARED_PREFRENCE_SIGNUP_COMPLETE, true);
        editor.putInt(Constant.SHARED_PREFRENCE_USER_BASIC_INFORMATION_STATUS, Constant.IS_NOT_PROVIDE_BASCI_INFORMATION);
        editor.putString(Constant.SHARED_PREFRENCE_LOGIN_STATUS, Constant.LOGIN_STATUS_LOGIN);
        editor.commit();
    }
    public void goToHomeActivity(){
        Intent intent = new Intent(getApplicationContext(), homeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
}
