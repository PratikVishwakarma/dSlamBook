package major.com.dslambook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.IntegerRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import major.com.dslambook.Pojo.FullSinglePost;
import major.com.dslambook.Pojo.Like;
import major.com.dslambook.Pojo.LikedBy;
import major.com.dslambook.Pojo.Post;
import major.com.dslambook.Pojo.User;
import major.com.dslambook.R;
import major.com.dslambook.Utility.Constant;
import major.com.dslambook.Utility.Utility;

import static java.security.AccessController.getContext;

/**
 * Created by prati on 11-Apr-17.
 */

public class PostsListRecyclerAdapter extends RecyclerView.Adapter<PostsListRecyclerAdapter.MyViewHolder> {

    private List<Post> postList = new ArrayList<Post>();
    private Map<String, User> userList = new HashMap<>();
    private Map<String, String> postImageList = new HashMap<>();
    private Map<String, Integer> commentCountList = new HashMap<>();
    private Map<String, Like> likeStatusList = new HashMap<>();
    private Map<String, Like> tempLikeList = new HashMap<>();
    private Map<String, Integer> postPositionList = new HashMap<>();
    private Utility utility;

    private Post currentPostItemChoose;
    private String userId;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference postRef, likeRef;

    private Context mContext;
    private StorageReference mStorageRef;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        View listItemView;
        TextView postUsername, postContent, postTotalLike, totalComment, postDate;
        TextView postImageOneOneLike, postImageTwoTwoLike, postImageTwoOneLike;
        TextView postImageFourOneLike, postImageFourTwoLike, postImageFourThreeLike, postImageFourFourLike;
        ImageView postImageFourOne, postImageFourTwo, postImageFourThree, postImageFourFour;
        ImageView postImageTwoOne, postImageTwoTwo;

        ImageView postUserProfilePic, postLiked, postNotLiked, comment;
        private ImageView postImageOneOne;

        public MyViewHolder(View mlistItemView, int layoutChoose) {
            super(mlistItemView);
            listItemView = mlistItemView;
            postUsername = (TextView) listItemView.findViewById(R.id.post_item_user_username);
            postDate = (TextView) listItemView.findViewById(R.id.post_item_post_date_and_time);
            postContent = (TextView) listItemView.findViewById(R.id.post_item_post_content);
            postLiked = (ImageView) listItemView.findViewById(R.id.post_item_post_liked);
            postNotLiked = (ImageView) listItemView.findViewById(R.id.post_item_post_not_liked);
            postTotalLike = (TextView) listItemView.findViewById(R.id.post_item_post_total_likes);
            totalComment = (TextView) listItemView.findViewById(R.id.post_item_post_total_comments);
            postUserProfilePic = (ImageView) listItemView.findViewById(R.id.post_item_user_profile_pic);
            switch (layoutChoose){
                case 1:
                    postImageOneOne = (ImageView) listItemView.findViewById(R.id.post_item_post_image_one_one);
                    postImageOneOneLike = (TextView) listItemView.findViewById(R.id.post_item_post_like_one_one);
                    break;
                case 2:

                    break;
                case 4:

                    break;
            }
        }
}

    public PostsListRecyclerAdapter(Context mContext, List<Post> postList,
                                    Map<String, User> userList,
                                    Map<String, Integer> commentCountList,
                                    Map<String, Like> likeStatusList,
                                    Map<String, String> postImageList,
                                    String userId,
                                    Map<String, Integer> postPositionList) {
        this.mContext = mContext;
        this.postList= postList;
        this.userList = userList;
        this.postImageList = postImageList;
        this.commentCountList = commentCountList;
        this.likeStatusList= likeStatusList;
        this.userId = userId;
        this.postPositionList = postPositionList;
        setLikes(likeStatusList);
    }

    @Override
    public int getItemViewType(int position) {
        currentPostItemChoose = postList.get(position);
        int itemChoose = 1;
        switch (currentPostItemChoose.totalImages){
            case 1:
                itemChoose = 1;
                break;
            case 2:
                itemChoose = 2;
            break;
            case 4:
                itemChoose = 4;
            break;
            default:
                itemChoose = 1;
        }
        return itemChoose;
    }

    @Override
    public PostsListRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItemView = null;
        switch (currentPostItemChoose.totalImages){
            case 1:
                listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_one, parent, false);
                break;
            case 2:
                listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_two, parent, false);
                break;
            case 4:
                listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_four, parent, false);
                break;
        }

        sharedpreferences =  mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        return new MyViewHolder(listItemView, currentPostItemChoose.totalImages);
    }

    @Override
    public void onBindViewHolder(final PostsListRecyclerAdapter.MyViewHolder holder, int position) {
        final Post currentPost;
        final User currentUser;
        final Like currentLike;
        final Integer currentComment;
        final String currentPostImage;
        final int itemPosition;

//
        currentPost = postList.get(position);
        itemPosition = position;
        Log.e("Post Ad"," pos of post = "+itemPosition);
//        Log.e("Post Ad"," postList size = "+postList.get(itemPosition).getPostId());
        currentUser = userList.get(currentPost.getPostId());
        currentComment = commentCountList.get(currentPost.getPostId());
        currentLike = tempLikeList.get(currentPost.getPostId());
        currentPostImage = postImageList.get(currentPost.getPostId());

        utility = new Utility();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        postRef = mFirebaseDatabase.getReference(Constant.FIREBASE_REFERENCE_POST);
        likeRef = mFirebaseDatabase.getReference(Constant.FIREBASE_REFERENCE_LIKE);

        mStorageRef = FirebaseStorage.getInstance().getReference();

//        Log.e("Post Ad"," holder  = "+holder);
//        Log.e("Post Ad"," id  = "+ holder.postUsername.getId());
//        Log.e("Post Ad"," userId = "+currentUser.getUserId());
//        Log.e("Post Ad"," pos of post = "+itemPosition);
        holder.postContent.setText(currentPost.getContent());
        holder.postUsername.setText(currentUser.getUserId());
        holder.postDate.setText(currentPost.getDate());
        if (currentLike.getPostId().equals(currentPost.getPostId())) {
            holder.postLiked.setVisibility(View.VISIBLE);
            holder.postNotLiked.setVisibility(View.GONE);
        } else{
            holder.postNotLiked.setVisibility(View.VISIBLE);
            holder.postLiked.setVisibility(View.GONE);
        }
        if(currentPost.getLike() != 0){
            holder.postTotalLike.setText(currentPost.getLike()+ "");
        } else{
            holder.postTotalLike.setText("");
        }
        if(currentComment != 0){
            holder.totalComment.setText(currentComment+ "");
        } else{
            holder.totalComment.setText("");
        }

        holder.postLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Post Ad"," liked = "+currentPost.getPostId());
                holder.postLiked.setVisibility(View.GONE);
                holder.postNotLiked.setVisibility(View.VISIBLE);
            }
        });
        holder.postNotLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Post Ad"," Not liked = "+currentPost.getPostId());
                holder.postNotLiked.setVisibility(View.GONE);
                holder.postLiked.setVisibility(View.VISIBLE);
            }
        });
        mStorageRef.child(Constant.FIREBASE_IMAGE_REFERENCE_USER)
                .child(currentPost.getUserId())
                .child(currentUser.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(mContext)
                        .load(uri).placeholder(R.drawable.placeholder_picture)
                        .error(R.drawable.placeholder_picture)
                        .into(holder.postUserProfilePic);
                setPostImage(currentPostImage, currentPost, holder, itemPosition, currentLike);
            }
        });

    }
    public void setPostImage(final String postImageCode, final Post currentPost,
                             final PostsListRecyclerAdapter.MyViewHolder holder, final int itemPosition,
                             final Like likeStatus){
        final String imageId_1_1, imageId_2_1, imageId_2_2, imageId_4_1, imageId_4_2, imageId_4_3, imageId_4_4;
        final String[] likeSplit1_1, likeSplit2_1, likeSplit2_2, likeSplit4_1, likeSplit4_2, likeSplit4_3, likeSplit4_4, split;
        final int imageId_1_1_like, imageId_2_1_like, imageId_2_2_like, imageId_4_1_like, imageId_4_2_like, imageId_4_3_like, imageId_4_4_like;

        split = postImageCode.split(Pattern.quote(Constant.STRING_POST_IMAGE_DIFFERENTIATOR));
        StorageReference postImageRef = mStorageRef.child(Constant.FIREBASE_IMAGE_REFERENCE_POST).
                child(currentPost.getUserId()).
                child(currentPost.getPostId());

        switch (currentPost.getTotalImages()){
            case 1:
                likeSplit1_1 = split[0].split(Pattern.quote(Constant.STRING_POST_IMAGE_LIKE_DIFFERENTIATOR));
//                holder.postImageOneOne = (ImageView) holder.listItemView.findViewById(R.id.post_item_post_image_one_one);
                imageId_1_1 = likeSplit1_1[0].replace("_img.jpg","");
                imageId_1_1_like = Integer.parseInt(likeSplit1_1[1]);
//                holder.postImageOneOneLike = (TextView) holder.listItemView.findViewById(R.id.post_item_post_like_one_one);

                postImageRef.child(likeSplit1_1[0]).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(mContext)
                                .load(uri).placeholder(R.drawable.placeholder_picture)
                                .error(R.drawable.placeholder_picture)
                                .into(holder.postImageOneOne);
                    }
                });

                if(likeStatus.getImageId().equals(imageId_1_1)){
                    holder.postImageOneOneLike.setText(imageId_1_1_like+" L");
                    holder.postImageOneOneLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("Post Ad","single liked = "+currentPost.getPostId()+" iId : "+imageId_1_1);
                            notLikePost(currentPost.getUserId(),currentPost.getPostId(), imageId_1_1 , imageId_1_1_like, currentPost, itemPosition, holder);
//                            notifyItemChanged(itemPosition);
                        }
                    });
                } else{
                    holder.postImageOneOneLike.setText(imageId_1_1_like+" NL");
                    holder.postImageOneOneLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("Post Ad","single not liked = "+currentPost.getPostId()+" iId : "+imageId_1_1);
                            likePost(currentPost.getUserId(),currentPost.getPostId(), imageId_1_1 , imageId_1_1_like, currentPost, itemPosition, holder);
//                            notifyItemChanged(itemPosition);
                        }
                    });
                }


                break;
            case 2:
                likeSplit2_1 = split[0].split(Pattern.quote(Constant.STRING_POST_IMAGE_LIKE_DIFFERENTIATOR));
                likeSplit2_2 = split[1].split(Pattern.quote(Constant.STRING_POST_IMAGE_LIKE_DIFFERENTIATOR));

                imageId_2_1 = likeSplit2_1[0].replace("_img.jpg","");
                imageId_2_2 = likeSplit2_2[0].replace("_img.jpg","");
                imageId_2_1_like = Integer.parseInt(likeSplit2_1[1]);
                imageId_2_2_like = Integer.parseInt(likeSplit2_2[1]);

                holder.postImageTwoOne = (ImageView) holder.listItemView.findViewById(R.id.post_item_post_image_two_one);
                holder.postImageTwoTwo = (ImageView) holder.listItemView.findViewById(R.id.post_item_post_image_two_two);

                holder.postImageTwoOneLike = (TextView) holder.listItemView.findViewById(R.id.post_item_post_like_two_one);
                holder.postImageTwoTwoLike = (TextView) holder.listItemView.findViewById(R.id.post_item_post_like_two_two);

                postImageRef.child(likeSplit2_1[0]).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(mContext)
                                .load(uri).placeholder(R.drawable.placeholder_picture)
                                .error(R.drawable.placeholder_picture)
                                .into(holder.postImageTwoOne);
                    }
                });
                postImageRef.child(likeSplit2_2[0]).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(mContext)
                                .load(uri).placeholder(R.drawable.placeholder_picture)
                                .error(R.drawable.placeholder_picture)
                                .into(holder.postImageTwoTwo);
                    }
                });

                if(likeStatus.getImageId().equals(imageId_2_1)){
                    holder.postImageTwoOneLike.setText(imageId_2_1_like+ " L");
                    holder.postImageTwoOneLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("Post Ad","single not liked = "+currentPost.getPostId()+" iId : "+imageId_2_1);
                            notLikePost(currentPost.getUserId(),currentPost.getPostId(), imageId_2_1 , imageId_2_1_like, currentPost, itemPosition, holder);
                            notifyItemChanged(itemPosition);
                            holder.postImageTwoOneLike.setClickable(false);
                        }
                    });
                } else{
                    holder.postImageTwoOneLike.setText(imageId_2_1_like+ " NL");
                    holder.postImageTwoOneLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("Post Ad","single  liked = "+currentPost.getPostId()+" iId : "+imageId_2_1);
                            likePost(currentPost.getUserId(),currentPost.getPostId(), imageId_2_1 , imageId_2_1_like, currentPost, itemPosition, holder);
                            notifyItemChanged(itemPosition);
                        }
                    });
                }

                if(likeStatus.getImageId().equals(imageId_2_2)){
                    holder.postImageTwoTwoLike.setText(imageId_2_2_like+ " L");
                    holder.postImageTwoTwoLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("Post Ad","single not liked = "+currentPost.getPostId()+" iId : "+imageId_2_2);
                            notLikePost(currentPost.getUserId(),currentPost.getPostId(), imageId_2_2 , imageId_2_2_like, currentPost, itemPosition, holder);
                            notifyItemChanged(itemPosition);
                        }
                    });
                } else{
                    holder.postImageTwoTwoLike.setText(imageId_2_2_like+ " NL");
                    holder.postImageTwoTwoLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("Post Ad","single liked = "+currentPost.getPostId()+" iId : "+imageId_2_2);
                            likePost(currentPost.getUserId(),currentPost.getPostId(), imageId_2_2 , imageId_2_2_like, currentPost, itemPosition, holder);
                            notifyItemChanged(itemPosition);
                        }
                    });
                }

                break;
            case 4:
                likeSplit4_1 = split[0].split(Pattern.quote(Constant.STRING_POST_IMAGE_LIKE_DIFFERENTIATOR));
                likeSplit4_2 = split[1].split(Pattern.quote(Constant.STRING_POST_IMAGE_LIKE_DIFFERENTIATOR));
                likeSplit4_3 = split[2].split(Pattern.quote(Constant.STRING_POST_IMAGE_LIKE_DIFFERENTIATOR));
                likeSplit4_4 = split[3].split(Pattern.quote(Constant.STRING_POST_IMAGE_LIKE_DIFFERENTIATOR));
                holder.postImageFourOne = (ImageView) holder.listItemView.findViewById(R.id.post_item_post_image_four_one);
                holder.postImageFourTwo= (ImageView) holder.listItemView.findViewById(R.id.post_item_post_image_four_two);
                holder.postImageFourThree = (ImageView) holder.listItemView.findViewById(R.id.post_item_post_image_four_three);
                holder.postImageFourFour = (ImageView) holder.listItemView.findViewById(R.id.post_item_post_image_four_four);

                holder.postImageFourOneLike = (TextView) holder.listItemView.findViewById(R.id.post_item_post_like_four_one);
                holder.postImageFourTwoLike = (TextView) holder.listItemView.findViewById(R.id.post_item_post_like_four_two);
                holder.postImageFourThreeLike = (TextView) holder.listItemView.findViewById(R.id.post_item_post_like_four_three);
                holder.postImageFourFourLike = (TextView) holder.listItemView.findViewById(R.id.post_item_post_like_four_four);

                holder.postImageFourOneLike.setText(likeSplit4_1[1].toString());
                holder.postImageFourTwoLike.setText(likeSplit4_2[1].toString());
                holder.postImageFourThreeLike.setText(likeSplit4_3[1].toString());
                holder.postImageFourFourLike.setText(likeSplit4_4[1].toString());

                postImageRef.child(likeSplit4_1[0]).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(mContext)
                                .load(uri).placeholder(R.drawable.placeholder_picture)
                                .error(R.drawable.placeholder_picture)
                                .into(holder.postImageFourOne);
                    }
                });
                postImageRef.child(likeSplit4_2[0]).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(mContext)
                                .load(uri).placeholder(R.drawable.placeholder_picture)
                                .error(R.drawable.placeholder_picture)
                                .into(holder.postImageFourTwo);
                    }
                });
                postImageRef.child(likeSplit4_3[0]).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(mContext)
                                .load(uri).placeholder(R.drawable.placeholder_picture)
                                .error(R.drawable.placeholder_picture)
                                .into(holder.postImageFourThree);
                    }
                });
                postImageRef.child(likeSplit4_4[0]).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(mContext)
                                .load(uri).placeholder(R.drawable.placeholder_picture)
                                .error(R.drawable.placeholder_picture)
                                .into(holder.postImageFourFour);
                    }
                });
                break;

        }
    }
    public void likePost(final String postUserId, final String postId, final String imageId, final int imageLike, final Post currentPost,
                         final int itemPosition, final PostsListRecyclerAdapter.MyViewHolder holder){
        holder.postLiked.setVisibility(View.VISIBLE);
        holder.postNotLiked.setVisibility(View.GONE);
        final LikedBy likedBy = new LikedBy(postId,imageId);
        likeRef.child(userId).child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Like value = dataSnapshot.getValue(Like.class);
                    Log.e("Post Ad","in Like all ready liked once = "+value.getImageId()+" = "+imageId);
                } else{
                    postRef.child(postUserId).child(postId).child("likedBy").child(userId).setValue(likedBy).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            postRef.child(postUserId).child(postId).child("like").setValue(currentPost.getLike()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    postRef.child(postUserId).child(postId).child("image").child(imageId).child("like").setValue(imageLike+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            final Like like = new Like(postId, imageId);
                                            final Post uPost = new Post(userId, postId, currentPost.getContent(),
                                                    currentPost.getTime(), currentPost.getDate(), currentPost.getLike()+1,
                                                    currentPost.getTotalImages());

                                            likeRef.child(userId).child(postId).setValue(like).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    tempLikeList.remove(postId);
                                                    tempLikeList.put(postId, like);
                                                    postList.set(itemPosition,uPost);
                                                    notifyItemChanged(itemPosition);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    public void notLikePost(final String postUserId, final String postId, final String imageId, final int imageLike, final Post currentPost,
                            final int itemPosition, final PostsListRecyclerAdapter.MyViewHolder holder){
        holder.postLiked.setVisibility(View.GONE);
        holder.postNotLiked.setVisibility(View.VISIBLE);
        postRef.child(postUserId).child(postId).child("likedBy").child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                int like = currentPost.getLike();
                if(like == 0){
                    like = 0;
                } else{
                    like = like - 1;
                }
                postRef.child(postUserId).child(postId).child("like").setValue(like).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        int like = imageLike;
                        if(like == 0){
                            like = 0;
                        } else{
                            like = like - 1;
                        }
                        postRef.child(postUserId).child(postId).child("image").child(imageId).child("like").setValue(like).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                final Like likePost = new Like("NA" , "NA");
                                final Post uPost = new Post(userId, postId, currentPost.getContent(),
                                        currentPost.getTime(), currentPost.getDate(), currentPost.getLike()-1,
                                        currentPost.getTotalImages());
                                likeRef.child(userId).child(postId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        tempLikeList.remove(postId);
                                        tempLikeList.put(postId, likePost);
                                        postList.set(itemPosition,uPost);
                                        notifyItemChanged(itemPosition);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    public void setLikes(Map<String, Like> allPostsLike){
        tempLikeList = allPostsLike;

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}
