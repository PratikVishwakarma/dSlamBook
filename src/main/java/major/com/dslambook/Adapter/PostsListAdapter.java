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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import major.com.dslambook.Pojo.Like;
import major.com.dslambook.Pojo.Post;
import major.com.dslambook.Pojo.User;
import major.com.dslambook.R;
import major.com.dslambook.Utility.Constant;
import major.com.dslambook.Utility.ImageConverter;
import major.com.dslambook.Utility.Utility;

/**
 * Created by prati on 14-Nov-16.
 */

public class PostsListAdapter extends ArrayAdapter<Post> {
    private View listItemView;

    private Map<String, User> userList = new HashMap<>();
    private Map<String, String> postImageList = new HashMap<>();
    private Map<String, Integer> commentCountList = new HashMap<>();
    private Map<String, Like> likeStatusList = new HashMap<>();
    private Utility utility;

    private Post currentPost;
    private User currentUser;
    private Like currentLike;
    private Integer currentComment;
    private String currentPostImage;
    private String userId;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference likeRef;

    private StorageReference mStorageRef;

    TextView postUsername, postContent, postTotalLike, totalComment;

    ImageView postUserProfilePic, postLiked, postNotLiked, comment;
    private ImageView postImageOneOne;

    public PostsListAdapter(Context context, int resource, ArrayList<Post> postList,
                            Map<String, User> userList,
                            Map<String, Integer> commentCountList,
                            Map<String, Like> likeStatusList,
                            Map<String, String> postImageList) {
        super(context, resource, postList);
        this.userList = userList;
        this.postImageList = postImageList;
        this.commentCountList = commentCountList;
        this.likeStatusList= likeStatusList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        sharedpreferences = getContext().getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        currentPost = getItem(position);

        currentUser = userList.get(currentPost.getPostId());
        currentComment = commentCountList.get(currentPost.getPostId());
        currentLike = likeStatusList.get(currentPost.getPostId());
        currentPostImage = postImageList.get(currentPost.getPostId());

        utility = new Utility();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        likeRef = mFirebaseDatabase.getReference(Constant.FIREBASE_LOCATION_LIKE);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        listItemView = convertView;

        switch (currentPost.totalImages){
            case 1:
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_post_one, parent, false);
                break;
            case 2:
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_post_two, parent, false);
                break;
            case 4:
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_post_four, parent, false);
                break;
        }

//        Log.e("Post list ","called without any issue  "+post.getContent()+" img : "+post.getImage()+" id : "+idFromPostId[0]+" username : "+post.getUserName());
//        filePathRef = mStorageRef.child(Constant.FIREBASE_LOCATION_STORAGE_POSTIMAGE)
//                .child(idFromPostId[0]).child(post.getImage());

        Log.e("Post list"," name = "+currentUser.getImage());
        postUsername = (TextView) listItemView.findViewById(R.id.post_item_user_username);
        TextView postDate = (TextView) listItemView.findViewById(R.id.post_item_post_date_and_time);
        postContent = (TextView) listItemView.findViewById(R.id.post_item_post_content);
        postLiked = (ImageView) listItemView.findViewById(R.id.post_item_post_liked);
        postNotLiked = (ImageView) listItemView.findViewById(R.id.post_item_post_not_liked);
        postTotalLike = (TextView) listItemView.findViewById(R.id.post_item_post_total_likes);
        postUserProfilePic = (ImageView) listItemView.findViewById(R.id.post_item_user_profile_pic);

        postContent.setText(currentPost.getContent());
        postUsername.setText(currentUser.getUserId());
        postDate.setText(currentPost.getDate());
        if (currentLike.getPostId().equals(currentPost.getPostId())) {
            postLiked.setVisibility(View.VISIBLE);
            postNotLiked.setVisibility(View.GONE);
        } else{
            postNotLiked.setVisibility(View.VISIBLE);
            postLiked.setVisibility(View.GONE);
        }
        postTotalLike.setText(currentPost.getLike()+ "");

        mStorageRef.child(Constant.FIREBASE_IMAGE_REFERENCE_USER)
                .child(currentPost.getUserId())
                .child(currentUser.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getContext())
                        .load(uri).placeholder(R.drawable.placeholder_picture)
                        .error(R.drawable.placeholder_picture)
                        .into(postUserProfilePic);
                setPostImage(currentPostImage);
            }
        });

//        if(post.isLikeStatus()){
//            postNotLiked.setVisibility(View.GONE);
//            postLiked.setVisibility(View.VISIBLE);
//        } else{
//            postLiked.setVisibility(View.GONE);
//            postNotLiked.setVisibility(View.VISIBLE);
//        }

//        postNotLiked.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onLikePost(post.getPostId());
//            }
//        });

//        postLiked.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onUnLikePost(post.getPostId());
//
//            }
//        });
//        checkPostLikeStatus(post.getPostId());

//        if(!post.getImage().equals("no_Image")){
//            filePathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//                    Picasso.with(getContext())
//                            .load(uri)
//                            .into(postImage);
//                }
//            });
//        }

        return listItemView;
    }

    public void setPostImage(final String postImageCode){
        String[] split = postImageCode.split(Pattern.quote(Constant.STRING_POST_IMAGE_DIFFERENTIATOR));
        switch (currentPost.getTotalImages()){
            case 1:
                String[] likeSplit1_1 = split[0].split(Pattern.quote(Constant.STRING_POST_IMAGE_LIKE_DIFFERENTIATOR));
                postImageOneOne = (ImageView) listItemView.findViewById(R.id.post_item_post_image_one_one);
                final TextView postImageOneOneLike = (TextView) listItemView.findViewById(R.id.post_item_post_like_one_one);
                mStorageRef.child(Constant.FIREBASE_IMAGE_REFERENCE_POST).
                        child(currentPost.getUserId()).
                        child(currentPost.getPostId()).
                        child(likeSplit1_1[0]).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getContext())
                            .load(uri).placeholder(R.drawable.placeholder_picture)
                            .error(R.drawable.placeholder_picture)
                            .into(postImageOneOne);
                    }
                });
            break;
            case 2:
                String[] likeSplit2_1 = split[0].split(Pattern.quote(Constant.STRING_POST_IMAGE_LIKE_DIFFERENTIATOR));
                String[] likeSplit2_2 = split[1].split(Pattern.quote(Constant.STRING_POST_IMAGE_LIKE_DIFFERENTIATOR));

                final ImageView postImageTwoOne = (ImageView) listItemView.findViewById(R.id.post_item_post_image_two_one);
                final ImageView postImageTwoTwo= (ImageView) listItemView.findViewById(R.id.post_item_post_image_two_two);

                final TextView postImageTwoTwoLike = (TextView) listItemView.findViewById(R.id.post_item_post_like_two_one);
                final TextView postImageTwoOneLike = (TextView) listItemView.findViewById(R.id.post_item_post_like_two_two);

                postImageTwoOneLike.setText(likeSplit2_1[1].toString());
                postImageTwoTwoLike.setText(likeSplit2_2[1].toString());
                mStorageRef.child(Constant.FIREBASE_IMAGE_REFERENCE_POST).
                        child(currentPost.getUserId()).
                        child(currentPost.getPostId()).
                        child(likeSplit2_1[0]).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getContext())
                                .load(uri).placeholder(R.drawable.placeholder_picture)
                                .error(R.drawable.placeholder_picture)
                                .into(postImageTwoOne);
                    }
                });
                mStorageRef.child(Constant.FIREBASE_IMAGE_REFERENCE_POST).
                        child(currentPost.getUserId()).
                        child(currentPost.getPostId()).
                        child(likeSplit2_2[0]).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getContext())
                                .load(uri).placeholder(R.drawable.placeholder_picture)
                                .error(R.drawable.placeholder_picture)
                                .into(postImageTwoTwo);
                    }
                });
                break;
            case 4:
                String[] likeSplit4_1 = split[0].split(Pattern.quote(Constant.STRING_POST_IMAGE_LIKE_DIFFERENTIATOR));
                String[] likeSplit4_2 = split[1].split(Pattern.quote(Constant.STRING_POST_IMAGE_LIKE_DIFFERENTIATOR));
                String[] likeSplit4_3 = split[2].split(Pattern.quote(Constant.STRING_POST_IMAGE_LIKE_DIFFERENTIATOR));
                String[] likeSplit4_4 = split[3].split(Pattern.quote(Constant.STRING_POST_IMAGE_LIKE_DIFFERENTIATOR));
                final ImageView postImageFourOne = (ImageView) listItemView.findViewById(R.id.post_item_post_image_four_one);
                final ImageView postImageFourTwo= (ImageView) listItemView.findViewById(R.id.post_item_post_image_four_two);
                final ImageView postImageFourThree = (ImageView) listItemView.findViewById(R.id.post_item_post_image_four_three);
                final ImageView postImageFourFour = (ImageView) listItemView.findViewById(R.id.post_item_post_image_four_four);

                final TextView postImageFourOneLike = (TextView) listItemView.findViewById(R.id.post_item_post_like_four_one);
                final TextView postImageFourTwoLike = (TextView) listItemView.findViewById(R.id.post_item_post_like_four_two);
                final TextView postImageFourThreeLike = (TextView) listItemView.findViewById(R.id.post_item_post_like_four_three);
                final TextView postImageFourFourLike = (TextView) listItemView.findViewById(R.id.post_item_post_like_four_four);

                postImageFourOneLike.setText(likeSplit4_1[1].toString());
                postImageFourTwoLike.setText(likeSplit4_2[1].toString());
                postImageFourThreeLike.setText(likeSplit4_3[1].toString());
                postImageFourFourLike.setText(likeSplit4_4[1].toString());
                mStorageRef.child(Constant.FIREBASE_IMAGE_REFERENCE_POST).
                        child(currentPost.getUserId()).
                        child(currentPost.getPostId()).
                        child(likeSplit4_1[0]).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getContext())
                                .load(uri).placeholder(R.drawable.placeholder_picture)
                                .error(R.drawable.placeholder_picture)
                                .into(postImageFourOne);
                    }
                });
                mStorageRef.child(Constant.FIREBASE_IMAGE_REFERENCE_POST).
                        child(currentPost.getUserId()).
                        child(currentPost.getPostId()).
                        child(likeSplit4_2[0]).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getContext())
                                .load(uri).placeholder(R.drawable.placeholder_picture)
                                .error(R.drawable.placeholder_picture)
                                .into(postImageFourTwo);
                    }
                });
                mStorageRef.child(Constant.FIREBASE_IMAGE_REFERENCE_POST).
                        child(currentPost.getUserId()).
                        child(currentPost.getPostId()).
                        child(likeSplit4_3[0]).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getContext())
                                .load(uri).placeholder(R.drawable.placeholder_picture)
                                .error(R.drawable.placeholder_picture)
                                .into(postImageFourThree);
                    }
                });
                mStorageRef.child(Constant.FIREBASE_IMAGE_REFERENCE_POST).
                        child(currentPost.getUserId()).
                        child(currentPost.getPostId()).
                        child(likeSplit4_4[0]).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getContext())
                                .load(uri).placeholder(R.drawable.placeholder_picture)
                                .error(R.drawable.placeholder_picture)
                                .into(postImageFourFour);
                    }
                });
                break;

        }

    }


    public void onLikePost(String postId){
        Date d = new Date();
        String time = new SimpleDateFormat("HH:mm:ss").format(d);
        String date = new SimpleDateFormat("dd-MM-yyyy").format(d);
//        Like like = new Like(postId, time, date);

//        likeRef.child(userIdByEmail).child(postId).setValue(like).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(getContext(), "onLike pressed",Toast.LENGTH_SHORT).show();
////                fullSinglePosts.set(itemPostion,post);
//                postNotLiked.setVisibility(View.GONE);
//                postLiked.setVisibility(View.VISIBLE);
//                notifyDataSetChanged();
//            }
//        });
    }

//    public void onUnLikePost(String postId){
//        likeRef.child(userIdByEmail).child(postId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(getContext(), "onUnLike pressed",Toast.LENGTH_SHORT).show();
//                postLiked.setVisibility(View.GONE);
//                postNotLiked.setVisibility(View.VISIBLE);
//                notifyDataSetChanged();
//            }
//        });
//    }
}
