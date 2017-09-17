package major.com.dslambook.UI;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import major.com.dslambook.Pojo.Friend;
import major.com.dslambook.Pojo.Home;
import major.com.dslambook.Pojo.Post;
import major.com.dslambook.Pojo.User;
import major.com.dslambook.R;
import major.com.dslambook.SignUpActivity;
import major.com.dslambook.Utility.Constant;
import major.com.dslambook.Utility.Utility;

import static java.security.AccessController.getContext;

public class addPostActivity extends AppCompatActivity {

    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    public FirebaseUser user;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference homeRef, friendRef, userRef;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference storageReference;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    ImageView showImageView;
    ImageButton addImagebtn;
    Button button_done_post;
    EditText editText_post_content;

    public ProgressDialog progressDialog;

    private String idByEmail, postid = null, content_post = null, currentDate, currentTime;
    public static final int GALLERY_INTENT_1 = 1;
    public static Uri uri;
    File file;
    Bitmap bitmap = null;
    private Utility utility;
    Intent CamIntent, GalIntent, CropIntent ;
    public boolean uploadAllImagesStatus = Boolean.FALSE;
    DateFormat datef, timef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String getUserEmailid = sharedpreferences.getString(Constant.SHARED_PREFRENCE_USER_EMAIL_ID, null);

        utility = new Utility();
        uri = null;

        if(getUserEmailid == null){
            Toast.makeText(getApplicationContext(), "User is null", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(addPostActivity.this, SignUpActivity.class);
            startActivity(intent);
        } else{
//            Toast.makeText(getApplicationContext(), "User id is "+getUserEmailid, Toast.LENGTH_SHORT).show();
            idByEmail = utility.emailToId(getUserEmailid);
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        homeRef = mFirebaseDatabase.getReference().child(Constant.FIREBASE_LOCATION_HOME);
        friendRef = mFirebaseDatabase.getReference(Constant.FIREBASE_LOCATION_FRIEND);
        userRef = mFirebaseDatabase.getReference(Constant.FIREBASE_REFERENCE_USERS);
        storageReference = mFirebaseStorage.getReference();
            initializeScreen();

        addImagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetImageFromGallery();
            }
        });

    }

    public void GetImageFromGallery(){
        GalIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), 2);
    }

    @Override
    public void onBackPressed() {
        super.finish();
    }

    public void createPostId(){
        Log.e("User email ", idByEmail);
        final Query query = userRef.child(idByEmail);
        Log.e("Refrence ", query+"");
        userRef.child(idByEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    long time= System.currentTimeMillis();
                    User currentUser = dataSnapshot.getValue(User.class);
                    Log.e("Inside getUser found", currentUser.getEmail()+ Constant.STRING_POSTID_DIFFERENTIATOR+time);
                    postid = idByEmail+Constant.STRING_POSTID_DIFFERENTIATOR+time;
                    upload_post(postid);
                }else{
                    user = null;
                    Log.e("not found", user.getEmail());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK) {
            ImageCropFunction();
        } else if (requestCode == 2) {
            if (data != null) {
                uri = data.getData();
                ImageCropFunction();
            }
        } else if (requestCode == 1) {
            if (data != null) {
                Uri data1 = data.getData();
                String s = compressImage(data1.toString());
                String filePath = getRealPathFromURI(s);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap dbitmap = BitmapFactory.decodeFile(filePath,bmOptions);
                showImageView.setImageBitmap(dbitmap);
                bitmap = ((BitmapDrawable)showImageView.getDrawable()).getBitmap();
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
            startActivityForResult(CropIntent, 1);
        } catch (ActivityNotFoundException e) {}
    }

    public void upload_post(String nPostid) {
        postid = nPostid;
        final Uri auri = uri;
        final Bitmap abitmaps = bitmap;
//        progressDialog.setMessage("Uploading...");
//        progressDialog.show();
        datef = new SimpleDateFormat(Constant.FORMATE_ADD_POST_DATE);
        currentDate = datef.format(Calendar.getInstance().getTime());
        timef = new SimpleDateFormat(Constant.FORMATE_ADD_POST_TIME_WITH_SECONDS);
        currentTime = timef.format(Calendar.getInstance().getTime());
            if (auri != null) {
                final String imageName = currentTime + "_img_.jpg";
                StorageReference photoRef = mFirebaseStorage.getReference().child(Constant.FIREBASE_LOCATION_STORAGE_POSTIMAGE)
                        .child(idByEmail).child(imageName);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                abitmaps.compress(Bitmap.CompressFormat.JPEG, 55, baos);
                final byte[] data = baos.toByteArray();
                UploadTask uploadTask = photoRef.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        imageNameList.add(imageName);
                        uploadAllImagesStatus = Boolean.TRUE;
                        Post newPost = new Post(idByEmail, postid, content_post,imageName, currentTime, currentDate, 0);
                        Task<Void> voidTask = mFirebaseDatabase.getReference().child(Constant.FIREBASE_LOCATION_POST).child(idByEmail)
                                .child(postid).setValue(newPost);
                        if(voidTask.isSuccessful()){
                            Log.e("Total Images upload ", "Task of post successful");
                        }
                        Home home = new Home(postid, idByEmail, Constant.TYPE_POST_TYPE_POST_BY_YOU, currentTime, currentDate);
                        homeRef.child(idByEmail).child(postid).setValue(home).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        friendRef.child(idByEmail).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                                                    Friend friend = childDataSnapshot.getValue(Friend.class);
                                                    if(friend.getRequestType() == 3){
                                                        Home home = new Home(postid, idByEmail, Constant.TYPE_POST_TYPE_POST_BY_FRIEND, currentTime, currentDate);
                                                        homeRef.child(friend.getOtherUserId()).child(postid).setValue(home);
                                                    }
                                                }
                                                Toast.makeText(getApplicationContext(), "Upload complete....", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), homeActivity.class);
                                                intent.putExtra(Constant.INTENT_KEY_STRING_HOME_CALLING,Constant.INTENT_VALUE_STRING_HOME_CALLING_AFTER_POST);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            } else{
                final String imageName = "no_Image";
                uploadAllImagesStatus = Boolean.TRUE;
                Post newPost = new Post(idByEmail, postid, content_post,imageName, currentTime, currentDate, 0);
                Task<Void> voidTask = mFirebaseDatabase.getReference().child(Constant.FIREBASE_LOCATION_POST)
                        .child(idByEmail).child(postid).setValue(newPost);
                if(voidTask.isSuccessful()){
                    Log.e("Total Images upload ", "Task of post successful");
                }
                Home home = new Home(postid, idByEmail, Constant.TYPE_POST_TYPE_POST_BY_YOU, currentTime, currentDate);
                homeRef.child(idByEmail).child(postid).setValue(home).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        friendRef.child(idByEmail).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                                    Friend friend = childDataSnapshot.getValue(Friend.class);
                                    if(friend.getRequestType() == 3){
                                        Home home = new Home(postid, idByEmail, Constant.TYPE_POST_TYPE_POST_BY_FRIEND, currentTime, currentDate);
                                        homeRef.child(friend.getOtherUserId()).child(postid).setValue(home);
                                    }
                                }
                                Toast.makeText(getApplicationContext(), "Upload complete....", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), homeActivity.class);
                                intent.putExtra(Constant.INTENT_KEY_STRING_HOME_CALLING,Constant.INTENT_VALUE_STRING_HOME_CALLING_AFTER_POST);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
//        progressDialog.dismiss();
        }

    public void initializeScreen(){
        addImagebtn = (ImageButton) findViewById(R.id.imageButton_add_image);

        showImageView = (ImageView) findViewById(R.id.imageView_post_image);

        button_done_post = (Button) findViewById(R.id.button_post);

        editText_post_content = (EditText) findViewById(R.id.editText_post_content);

        progressDialog = new ProgressDialog(getApplicationContext());

        button_done_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(content_post == null && uri == null){
                    Toast.makeText(getApplicationContext(), "Add media or text to post", Toast.LENGTH_SHORT).show();
                } else{
//                    Toast.makeText(getApplicationContext(), content_post+" not null post "+uri, Toast.LENGTH_SHORT).show();
                    button_done_post.setVisibility(View.INVISIBLE);
                    content_post = editText_post_content.getText().toString();
                    createPostId();
                }
            }
        });

        editText_post_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() == 0){
                    content_post = null;
//                    Toast.makeText(getApplicationContext(), "c is "+content_post, Toast.LENGTH_SHORT).show();
                }else{
                    content_post = editText_post_content.getText().toString();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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

}

