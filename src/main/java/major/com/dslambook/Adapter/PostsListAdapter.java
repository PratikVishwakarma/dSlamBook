package major.com.dslambook.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import major.com.dslambook.Pojo.FullSinglePost;
import major.com.dslambook.Pojo.Like;
import major.com.dslambook.Pojo.User;
import major.com.dslambook.R;
import major.com.dslambook.Utility.Constant;
import major.com.dslambook.Utility.ImageConverter;
import major.com.dslambook.Utility.Utility;

import static major.com.dslambook.UI.addPostActivity.uri;

/**
 * Created by prati on 14-Nov-16.
 */

public class PostsListAdapter extends ArrayAdapter<FullSinglePost> {

    private View listItemView;
    private List<FullSinglePost> fullSinglePosts;
    private Utility utility;

    private String userEmailId, userIdByEmail;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference likeRef;

    StorageReference filePathRef;

    private FullSinglePost post;

    private StorageReference mStorageRef;
    private int itemPostion = 0;

    TextView postUsername, postContent, postLiked, postNotLiked, postTotalLike;

    ImageView postUserProfilePic;

    public PostsListAdapter(Context context, int resource, List<FullSinglePost> fullSinglePosts) {
        super(context, resource, fullSinglePosts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        post = getItem(position);
        itemPostion = position;

        utility = new Utility();

        sharedpreferences = getContext().getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userEmailId = sharedpreferences.getString(Constant.SHARED_PREFRENCE_USER_EMAIL_ID, null);
        userIdByEmail = utility.emailToId(userEmailId);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        likeRef = mFirebaseDatabase.getReference(Constant.FIREBASE_LOCATION_LIKE);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        listItemView = convertView;
//        LayoutInflater liInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        listItemView = liInflater.inflate(R.layout.home_post_list_item, parent, false);

        listItemView = LayoutInflater.from(getContext()).inflate(R.layout.home_post_list_item, parent, false);

        String[] idFromPostId = utility.getIdFromPostId(post.getPostId());

        Log.e("Post list ","called without any issue  "+post.getContent()+" img : "+post.getImage()+" id : "+idFromPostId[0]+" username : "+post.getUserName());
        filePathRef = mStorageRef.child(Constant.FIREBASE_LOCATION_STORAGE_POSTIMAGE)
                .child(idFromPostId[0]).child(post.getImage());

        postUsername = (TextView) listItemView.findViewById(R.id.textView_post_item_username);
        TextView postDate = (TextView) listItemView.findViewById(R.id.textView_post_item_post_date);
        postContent = (TextView) listItemView.findViewById(R.id.textView_post_item_post_content);
        postLiked = (TextView) listItemView.findViewById(R.id.textView_post_item_post_liked);
        postNotLiked = (TextView) listItemView.findViewById(R.id.textView_post_item_post_notliked);
        postTotalLike = (TextView) listItemView.findViewById(R.id.textView_post_item_post_total_likes);
        final ImageView postImage= (ImageView) listItemView.findViewById(R.id.imageView_post_item_post_image);
        postUserProfilePic = (ImageView) listItemView.findViewById(R.id.imageView_post_item_user_profile_pic);

        postContent.setText(post.getContent());
        postUsername.setText(post.getUserName());
        postDate.setText(post.getDate());
        postTotalLike.setText(post.getLike()+ "");

        Picasso.with(getContext())
                .load(post.getUserProfilePic())
                .into(postUserProfilePic, new Callback() {
                    @Override
                    public void onSuccess() {
//                        Bitmap bitmap = ((BitmapDrawable) postUserProfilePic.getDrawable()).getBitmap();
//                        Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 180);
//                        postUserProfilePic.setImageBitmap(circularBitmap);
                    }

                    @Override
                    public void onError() {

                    }
                });


        if(post.isLikeStatus()){
            postNotLiked.setVisibility(View.GONE);
            postLiked.setVisibility(View.VISIBLE);
        } else{
            postLiked.setVisibility(View.GONE);
            postNotLiked.setVisibility(View.VISIBLE);
        }

        postNotLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLikePost(post.getPostId());
            }
        });

        postLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUnLikePost(post.getPostId());

            }
        });
//        checkPostLikeStatus(post.getPostId());

        if(!post.getImage().equals("no_Image")){
            filePathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(getContext())
                            .load(uri)
                            .into(postImage);
                }
            });
        }

        return listItemView;
    }

    public void onLikePost(String postId){
        Date d = new Date();
        String time = new SimpleDateFormat("HH:mm:ss").format(d);
        String date = new SimpleDateFormat("dd-MM-yyyy").format(d);
        Like like = new Like(postId, time, date);

        likeRef.child(userIdByEmail).child(postId).setValue(like).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "onLike pressed",Toast.LENGTH_SHORT).show();
//                fullSinglePosts.set(itemPostion,post);
                postNotLiked.setVisibility(View.GONE);
                postLiked.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
            }
        });
    }

    public void onUnLikePost(String postId){
        likeRef.child(userIdByEmail).child(postId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "onUnLike pressed",Toast.LENGTH_SHORT).show();
                postLiked.setVisibility(View.GONE);
                postNotLiked.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
            }
        });
    }
}
