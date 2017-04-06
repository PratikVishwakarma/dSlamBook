package major.com.dslambook;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import major.com.dslambook.Pojo.FullSinglePost;
import major.com.dslambook.Pojo.Post;
import major.com.dslambook.Pojo.User;
import major.com.dslambook.Utility.Constant;
import major.com.dslambook.Utility.Utility;

import static major.com.dslambook.UI.addPostActivity.uri;

/**
 * Created by prati on 14-Nov-16.
 */

public class PostsListAdapter extends ArrayAdapter<FullSinglePost> {

    private View listItemView;
    private List<FullSinglePost> fullSinglePosts;
    private Utility utility;
    public static final String MyPREFERENCES = "MyPrefs" ;
    StorageReference filePathRef;
    private User user;

    private StorageReference mStorageRef;

    TextView postUsername, postContent;

    public PostsListAdapter(Context context, int resource, List<FullSinglePost> fullSinglePosts) {
        super(context, resource, fullSinglePosts);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final FullSinglePost post = getItem(position);

        utility = new Utility();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        listItemView = convertView;
        listItemView = LayoutInflater.from(getContext()).inflate(R.layout.home_post_list_item, parent, false);
        String[] idFromPostId = utility.getIdFromPostId(post.getPostId());

        Log.e("Post list ","called without any issue  "+post.getContent()+" img : "+post.getImage()+" id : "+idFromPostId[0]+" username : "+post.getUserName());
        filePathRef = mStorageRef.child(Constant.FIREBASE_LOCATION_STORAGE_POSTIMAGE)
                .child(idFromPostId[0]).child(post.getImage());

        postUsername = (TextView) listItemView.findViewById(R.id.textView_post_item_username);
        TextView postDate = (TextView) listItemView.findViewById(R.id.textView_post_item_post_date);
        postContent = (TextView) listItemView.findViewById(R.id.textView_post_item_post_content);
        final ImageView postImage= (ImageView) listItemView.findViewById(R.id.imageView_post_item_post_image);
        ImageView postUserProfilePic = (ImageView) listItemView.findViewById(R.id.imageView_post_item_user_profile_pic);

        postContent.setText(post.getContent());
        postUsername.setText(post.getUserName());
        postDate.setText(post.getDate());
        Picasso.with(getContext())
                .load(post.getUserProfilePic())
                .into(postUserProfilePic);

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
}
