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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.ArrayList;
import java.util.Calendar;

import major.com.dslambook.Pojo.Friend;
import major.com.dslambook.Pojo.Home;
import major.com.dslambook.Pojo.Post;
import major.com.dslambook.Pojo.PostImage;
import major.com.dslambook.Pojo.User;
import major.com.dslambook.R;
import major.com.dslambook.SignUpActivity;
import major.com.dslambook.Utility.Constant;
import major.com.dslambook.Utility.Utility;

import static java.security.AccessController.getContext;

public class addPostActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mFirebaseAuth;
    public FirebaseUser user;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseRef, homeRef, friendRef, userRef;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference storageReference;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    ImageView showImageView;
    EditText editText_post_content;
    private String bitmapCode, frameCode = "1";

    private Button button_frame_one, button_frame_two, button_frame_four;
    private LinearLayout linearLayout_frame_one, linearLayout_frame_two, linearLayout_frame_four;
    private int totalNoOfImages = 0;

    ProgressDialog progress;

    private String userId = "", postId = null, content_post = null, currentDate, currentTime;
    private long lCurrentTime;
    public static final int GALLERY_INTENT_1 = 1;
    public static Uri uri;
    File file;

    Bitmap bitmap_1_1 = null, bitmap_2_1 = null, bitmap_2_2 = null, bitmap_4_1 = null, bitmap_4_2 = null, bitmap_4_3 = null, bitmap_4_4 = null;
    Bitmap[] allBitmap = new Bitmap[]{};
    ArrayList<String> allImageName = new ArrayList<String>();
    ArrayList<String> allImageUrl = new ArrayList<String>();
    private int noOfImage = 0, noOfImageAdded = 0;
    private Utility utility;
    Intent CamIntent, GalIntent, CropIntent ;
    DateFormat datef, timef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        
        utility = new Utility();

        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.setCancelable(false);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        mDatabaseRef = mFirebaseDatabase.getReference();
        homeRef = mFirebaseDatabase.getReference().child(Constant.FIREBASE_LOCATION_HOME);
        friendRef = mFirebaseDatabase.getReference(Constant.FIREBASE_LOCATION_FRIEND);
        userRef = mFirebaseDatabase.getReference(Constant.FIREBASE_REFERENCE_USERS);
        storageReference = mFirebaseStorage.getReference();

        String loginStatus = getLoginStatus();
        if(loginStatus.equals(Constant.LOGIN_STATUS_LOGIN)) {
            userId = getUserId();
            if (userId.equals("") || userId == null){
                goToSignupActivity();
            }
        } else{
            goToSignupActivity();
        }
            initializeScreen();
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
        final Query query = userRef.child(userId);
        long time= System.currentTimeMillis();
        postId = userId + Constant.STRING_POSTID_DIFFERENTIATOR + time;
        Log.e("Post id created ", postId);
        uploadPost(postId);
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
                loadImageToBitmap(dbitmap);
            }
        }
    }

    public void ImageCropFunction() {
        // Image Crop Code
        try {
            CropIntent = new Intent("com.android.camera.action.CROP");
            CropIntent.setDataAndType(uri, "image/*");
            CropIntent.putExtra("crop", "true");
            if(bitmapCode.equals("2_1") || bitmapCode.equals("2_2")){
                CropIntent.putExtra("aspectX", 9);
                CropIntent.putExtra("aspectY", 16);
            } else{
                CropIntent.putExtra("outputX", 800);
                CropIntent.putExtra("outputY", 800);
                CropIntent.putExtra("aspectX", 1);
                CropIntent.putExtra("aspectY", 1);
            }
            CropIntent.putExtra("scaleUpIfNeeded", false); //true to change size
            CropIntent.putExtra("return-data", true);
            startActivityForResult(CropIntent, 1);
        } catch (ActivityNotFoundException e) {}
    }

    public void uploadPost(String newPostId){
        if(frameCode.equals("1")){
            allBitmap = new Bitmap[]{bitmap_1_1};
        } else if(frameCode.equals("2")){
            allBitmap = new Bitmap[]{bitmap_2_1, bitmap_2_2};
        } else if(frameCode.equals("4")){
            allBitmap = new Bitmap[]{bitmap_4_1, bitmap_4_2, bitmap_4_3, bitmap_4_4};
        }
        totalNoOfImages = allBitmap.length;
        Log.e("No of images ", "Total len : "+totalNoOfImages);
        uploadAllPostImage(newPostId);
    }

    public void uploadAllPostImage(final String postId){
        progress.setMessage("Uploading...");
        progress.show();
        lCurrentTime = System.currentTimeMillis();
        if(noOfImage < totalNoOfImages){
            StorageReference photoRef = storageReference.child(Constant.FIREBASE_IMAGE_REFERENCE_POST).
                    child(userId).
                    child(postId).
                    child(String.valueOf(lCurrentTime) + "_img.jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            allBitmap[noOfImage].compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] data = baos.toByteArray();
            UploadTask uploadTask = photoRef.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    allImageName.add(String.valueOf(lCurrentTime) + "_img.jpg");
                    allImageUrl.add(String.valueOf(downloadUrl));
                    Log.e("Images Name ", " "+ allImageName.get(noOfImage));
                    noOfImage++;
                    if(totalNoOfImages == noOfImage){
                        Log.e("Images upload ", "Total len : "+ allImageName.size());
                        Log.e("Images upload ", "Total url : "+ allImageUrl.size());
                        noOfImage = 0;
                        addPostEntry(postId);
                        progress.setProgress(75);
                    } else{
                        uploadAllPostImage(postId);
                    }
                }
            });
        }
    }

    public void addPostEntry(final String postId){
        DatabaseReference newPostRef = mDatabaseRef.child(Constant.FIREBASE_REFERENCE_POST).
                child(userId).
                child(postId);
        datef = new SimpleDateFormat(Constant.FORMATE_ADD_POST_DATE);
        timef = new SimpleDateFormat(Constant.FORMATE_ADD_POST_TIME);
        currentDate = datef.format(Calendar.getInstance().getTime());
        currentTime = timef.format(Calendar.getInstance().getTime());
        Post newPost = new Post(userId, postId, "", currentTime, currentDate, 0, totalNoOfImages);

        newPostRef.setValue(newPost).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
//                  add the images entry
                addImageEntryInPost(postId);
            }
        });
    }

    public void addImageEntryInPost(final String postId){
        if(noOfImageAdded < allImageName.size()){
            String imageId = allImageName.get(noOfImageAdded).toString().replace("_img.jpg","");
            DatabaseReference newImageEntry = mDatabaseRef.child(Constant.FIREBASE_REFERENCE_POST).
                    child(userId).
                    child(postId).
                    child("image").
                    child(imageId);
            PostImage newPostImage = new PostImage(allImageName.get(noOfImageAdded),allImageUrl.get(noOfImageAdded), 0);
            newImageEntry.setValue(newPostImage).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
//                     add the images entry
                    noOfImageAdded++;
                    if(allImageName.size() == noOfImageAdded){
                        Log.e("Images entry ", " done");
                        noOfImageAdded = 0;
                        allImageName.clear();
                        addToUsersHomeEntry(postId);
                    } else{
//                        add images entry into post
                        addImageEntryInPost(postId);
                    }
                }
            });
        }else{
            progress.cancel();
            Log.e("Upd cond fail ", " "+ noOfImage+"");
        }
    }

    public void addToUsersHomeEntry(final String postId){
        DatabaseReference newPostRef = mDatabaseRef.child(Constant.FIREBASE_REFERENCE_HOME).
                child(userId).
                child(postId);
        Home newHomePost = new Home(postId, userId);

        newPostRef.setValue(newHomePost).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
//                add into home
                progress.cancel();
                goToHomeActivity();
            }
        });
    }


    public void initializeScreen(){

        button_frame_one = (Button) findViewById(R.id.addPost_button_one_pic_frame);
        button_frame_two = (Button) findViewById(R.id.addPost_button_two_pic_frame);
        button_frame_four = (Button) findViewById(R.id.addPost_button_four_pic_frame);

        linearLayout_frame_one = (LinearLayout) findViewById(R.id.addPost_linear_layout_frame_one);
        linearLayout_frame_two = (LinearLayout) findViewById(R.id.addPost_linear_layout_frame_two);
        linearLayout_frame_four = (LinearLayout) findViewById(R.id.addPost_linear_layout_frame_four);


//        editText_post_content = (EditText) findViewById(R.id.editText_post_content);
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
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/dSlambook");
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.addPost_button_one_pic_frame:
                setFramCode("1");
                linearLayout_frame_one.setVisibility(View.VISIBLE);
                linearLayout_frame_two.setVisibility(View.GONE);
                linearLayout_frame_four.setVisibility(View.GONE);
                break;
            case R.id.addPost_button_two_pic_frame:
                setFramCode("2");
                linearLayout_frame_one.setVisibility(View.GONE);
                linearLayout_frame_two.setVisibility(View.VISIBLE);
                linearLayout_frame_four.setVisibility(View.GONE);
                break;
            case R.id.addPost_button_four_pic_frame:
                setFramCode("4");
                linearLayout_frame_one.setVisibility(View.GONE);
                linearLayout_frame_two.setVisibility(View.GONE);
                linearLayout_frame_four.setVisibility(View.VISIBLE);
                break;
            case R.id.addPost_imageview_one_one:
                setImageViewAndBitmap(id, "1_1");
                GetImageFromGallery();
                break;
            case R.id.addPost_imageview_two_one:
                setImageViewAndBitmap(id, "2_1");
                GetImageFromGallery();
                break;
            case R.id.addPost_imageview_two_two:
                setImageViewAndBitmap(id, "2_2");
                GetImageFromGallery();
                break;
            case R.id.addPost_imageview_four_one:
                setImageViewAndBitmap(id, "4_1");
                GetImageFromGallery();
                break;
            case R.id.addPost_imageview_four_two:
                setImageViewAndBitmap(id, "4_2");
                GetImageFromGallery();
                break;
            case R.id.addPost_imageview_four_three:
                setImageViewAndBitmap(id, "4_3");
                GetImageFromGallery();
                break;
            case R.id.addPost_imageview_four_four:
                setImageViewAndBitmap(id, "4_4");
                GetImageFromGallery();
                break;
            case R.id.addPost_button_post:
                if(frameCode.equals("1")){
                    if(bitmap_1_1 == null){
                        printToast("Select image to upload");
                    }else{
                        createPostId();
                    }
                } else if(frameCode.equals("2")){
                    if(bitmap_2_1 == null || bitmap_2_2 == null){
                        printToast("Need all 2 images to upload");
                    }else{
                        createPostId();
                    }
                } else if(frameCode.equals("4")){
                    if(bitmap_4_1 == null || bitmap_4_2 == null || bitmap_4_3 == null || bitmap_4_4 == null){
                        printToast("Need all 4 images to upload");
                    }else{
                        createPostId();
                    }
                }
                break;
        }
    }

    public void setImageViewAndBitmap(int imageViewId, String frameCode){
        showImageView = (ImageView) findViewById(imageViewId);
        bitmapCode = frameCode;
    }

    public void setFramCode(String Code){
        frameCode = Code;
    }
    public void loadImageToBitmap(Bitmap bitmap){
        showImageView.setImageBitmap(bitmap);
        switch (bitmapCode){
            case "1_1":
                bitmap_1_1 = ((BitmapDrawable)showImageView.getDrawable()).getBitmap();
                break;
            case "2_1":
                bitmap_2_1 = ((BitmapDrawable)showImageView.getDrawable()).getBitmap();
                break;
            case "2_2":
                bitmap_2_2 = ((BitmapDrawable)showImageView.getDrawable()).getBitmap();
                break;
            case "4_1":
                bitmap_4_1 = ((BitmapDrawable)showImageView.getDrawable()).getBitmap();
                break;
            case "4_2":
                bitmap_4_2 = ((BitmapDrawable)showImageView.getDrawable()).getBitmap();
                break;
            case "4_3":
                bitmap_4_3 = ((BitmapDrawable)showImageView.getDrawable()).getBitmap();
                break;
            case "4_4":
                bitmap_4_4 = ((BitmapDrawable)showImageView.getDrawable()).getBitmap();
                break;
            default:
                break;
        }
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
    public void goToHomeActivity(){
        Intent intent = new Intent(getApplicationContext(), homeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
}

