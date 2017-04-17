package major.com.dslambook.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import major.com.dslambook.Pojo.User;
import major.com.dslambook.R;
import major.com.dslambook.Utility.ImageConverter;
import major.com.dslambook.Utility.Utility;

/**
 * Created by prati on 14-Nov-16.
 */

public class UsersFriendsListAdapter extends ArrayAdapter<User> {

    private View listItemView;
    private List<User> userList;
    private Utility utility;
    public static final String MyPREFERENCES = "MyPrefs" ;
    StorageReference filePathRef;
    private User user;

    private StorageReference mStorageRef;

    TextView userUsername;
    ImageView postUserProfilePic;
    public UsersFriendsListAdapter(Context context, int resource, List<User> userList) {
        super(context, resource, userList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final User user = getItem(position);

        utility = new Utility();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        listItemView = convertView;
        listItemView = LayoutInflater.from(getContext()).inflate(R.layout.users_friends_list_item, parent, false);
        String id = utility.emailToId(user.getEmail());

//        Log.e("Post list ","called without any issue  "+user.getContent()+" img : "+post.getImage()+" id : "+idFromPostId[0]+" username : "+post.getUserName());
//        filePathRef = mStorageRef.child(Constant.FIREBASE_LOCATION_STORAGE_POSTIMAGE).child(idFromPostId[0]).child(post.getImage());

        userUsername = (TextView) listItemView.findViewById(R.id.users_friends_item_textView_username);
        postUserProfilePic = (ImageView) listItemView.findViewById(R.id.users_friends_item_imageView_user_profile_pic);

        userUsername.setText(user.getUserName());
        Picasso.with(getContext())
                .load(user.getImageUrl())
                .into(postUserProfilePic, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) postUserProfilePic.getDrawable()).getBitmap();
                        Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 180);
                        postUserProfilePic.setImageBitmap(circularBitmap);
                    }
                    @Override
                    public void onError() {}
                });
        return listItemView;
    }
}
