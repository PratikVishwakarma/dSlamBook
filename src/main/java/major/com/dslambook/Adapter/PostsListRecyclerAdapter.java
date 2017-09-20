package major.com.dslambook.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import major.com.dslambook.Pojo.FullSinglePost;
import major.com.dslambook.R;
import major.com.dslambook.Utility.Constant;
import major.com.dslambook.Utility.Utility;

/**
 * Created by prati on 11-Apr-17.
 */

public class PostsListRecyclerAdapter extends RecyclerView.Adapter<PostsListRecyclerAdapter.MyViewHolder> {

    private List<FullSinglePost> fullSinglePostsList;
    private Utility utility;

    private int mNumberOfItems;
    private String userEmailId, userIdByEmail;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference likeRef;

    StorageReference filePathRef;

    private StorageReference mStorageRef;
    private Context mContext;
    TextView postUsername, postContent,  postTotalLike, postDate;
    ImageView postImage, postUserProfilePic, postLiked, postNotLiked;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View listItemView) {
            super(listItemView);

            postUsername = (TextView) listItemView.findViewById(R.id.post_item_user_username);
            TextView postDate = (TextView) listItemView.findViewById(R.id.post_item_post_date_and_time);
            postContent = (TextView) listItemView.findViewById(R.id.post_item_post_content);
            postLiked = (ImageView) listItemView.findViewById(R.id.post_item_post_liked);
            postNotLiked = (ImageView) listItemView.findViewById(R.id.post_item_post_not_liked);
            postTotalLike = (TextView) listItemView.findViewById(R.id.post_item_post_total_likes);
            final ImageView postImage= (ImageView) listItemView.findViewById(R.id.post_item_post_image_one_one);
            postUserProfilePic = (ImageView) listItemView.findViewById(R.id.post_item_user_profile_pic);
        }
    }

    public PostsListRecyclerAdapter(int numberOfItems){
        this.mNumberOfItems = numberOfItems;
    }
    public PostsListRecyclerAdapter(List<FullSinglePost> fullSinglePostsList, Context mContext) {
        this.fullSinglePostsList = fullSinglePostsList;
        this.mContext = mContext;
    }

    @Override
    public PostsListRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post_one, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostsListRecyclerAdapter.MyViewHolder holder, int position) {
        final FullSinglePost post = fullSinglePostsList.get(position);

        utility = new Utility();

        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userEmailId = sharedpreferences.getString(Constant.SHARED_PREFRENCE_USER_EMAIL_ID, null);
        userIdByEmail = utility.emailToId(userEmailId);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        likeRef = mFirebaseDatabase.getReference(Constant.FIREBASE_LOCATION_LIKE);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        String[] idFromPostId = utility.getIdFromPostId(post.getPostId());

        Log.e("Post list ","called without any issue  "+post.getContent()+" img : "+post.getImage()+" id : "+idFromPostId[0]+" username : "+post.getUserName());
        filePathRef = mStorageRef.child(Constant.FIREBASE_LOCATION_STORAGE_POSTIMAGE)
                .child(idFromPostId[0]).child(post.getImage());


        postContent.setText(post.getContent());
        postUsername.setText(post.getUserName());
        postDate.setText(post.getDate());
        postTotalLike.setText(post.getLike()+ "");
        Picasso.with(mContext)
                .load(post.getUserProfilePic())
                .into(postUserProfilePic);
        if(post.isLikeStatus()){
            postLiked.setVisibility(View.VISIBLE);
        } else{
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
                    Picasso.with(mContext)
                            .load(uri)
                            .into(postImage);
                }
            });
        }
    }
    public void onLikePost(String postId){
        Date d = new Date();
        String time = new SimpleDateFormat("HH:mm:ss").format(d);
        String date = new SimpleDateFormat("dd-MM-yyyy").format(d);
//        Like like = new Like(postId, time, date);
//
//        likeRef.child(userIdByEmail).child(postId).setValue(like).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(mContext, "onLike pressed",Toast.LENGTH_SHORT).show();
//                postNotLiked.setVisibility(View.GONE);
//                postLiked.setVisibility(View.VISIBLE);
//            }
//        });
    }

    public void onUnLikePost(String postId){
        likeRef.child(userIdByEmail).child(postId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext, "onUnLike pressed",Toast.LENGTH_SHORT).show();
                postLiked.setVisibility(View.GONE);
                postNotLiked.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
