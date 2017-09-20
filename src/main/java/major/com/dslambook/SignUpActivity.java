package major.com.dslambook;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    ImageView profile_pic, edit_profile_pic;
    Button button_next;

    ProgressDialog progress;
    public FirebaseUser user;

    private boolean usernameAvailablestatus = false;

    public static final int GALLERY_INTENT_1 = 100, READ_EXTERNAL_STORAGE_PERMISSION_CONSTANT = 4, WRITE_EXTERNAL_STORAGE_PERMISSION_CONSTANT = 5;
    public static Uri uri;
    File file;
    Intent CamIntent, GalIntent, CropIntent ;


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
                                    Log.e("SIN user ", "user is "+ value.getUserId());
                                    Log.e("SIN user ", "email is "+ value.getEmail());
                                    printToast(value.getUserId().toString());
                                    setUserId(value.getUserId(), value.getEmail());
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
        edit_profile_pic = (ImageView) findViewById(R.id.imageView_edit_profile_pic);
        profile_pic = (ImageView) findViewById(R.id.imageView_profile_pic);

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUserProcess(username.getText().toString().toLowerCase());
            }
        });
        edit_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
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
                        userName.contains("(") || userName.contains(")") || userName.contains(",") ||
                        userName.contains("<") || userName.contains(">") || userName.contains("?") ||
                        userName.contains("*") || userName.contains("^") || userName.contains("+")){
                    // un supported character
                    usernameAvailablestatus = false;
                    errorMsgUserName = "space , @, #, %, ., $, [, ], (, ), /, *, ^, +, <, >, ? or , not allow in username";
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


    public void GetImageFromGallery(){
        GalIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), 200);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK) {
            ImageCropFunction();
        } else if (requestCode == 200) {
            if (data != null) {
                uri = data.getData();
                ImageCropFunction();
            }
        } else if (requestCode == 100) {
            if (data != null) {
                Log.e("profilepic debug ", "called 1 ");
                Uri data1 = data.getData();
                String s = compressImage(data1.toString());
                String filePath = getRealPathFromURI(s);
                Log.e("profile file path ", "Path = "+filePath);

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap dbitmap = BitmapFactory.decodeFile(filePath,bmOptions);
                profile_pic.setImageBitmap(dbitmap);
                userPicBitmap = ((BitmapDrawable)profile_pic.getDrawable()).getBitmap();
            }
        }
    }

    public void ImageCropFunction() {
        // Image Crop Code
        try {
            CropIntent = new Intent("com.android.camera.action.CROP");
            CropIntent.setDataAndType(uri, "image/*");

            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", 600);
            CropIntent.putExtra("outputY", 600);
            CropIntent.putExtra("aspectX", 1);
            CropIntent.putExtra("aspectY", 1);
            CropIntent.putExtra("scaleUpIfNeeded", false); //true to change size
            CropIntent.putExtra("return-data", true);
            startActivityForResult(CropIntent, 100);
        } catch (ActivityNotFoundException e) {}
    }

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public void checkPermission(){
        if (ActivityCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                READ_EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            } else{
                ActivityCompat.requestPermissions(SignUpActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(SignUpActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                } else{
                    ActivityCompat.requestPermissions(SignUpActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                }
            } else{
                GetImageFromGallery();
            }
        }
    }
}
