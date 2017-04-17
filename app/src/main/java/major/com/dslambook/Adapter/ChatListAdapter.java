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

import major.com.dslambook.Pojo.Chat;
import major.com.dslambook.R;
import major.com.dslambook.Utility.Constant;
import major.com.dslambook.Utility.Utility;

/**
 * Created by prati on 14-Nov-16.
 */

public class ChatListAdapter extends ArrayAdapter<Chat> {

    private View listItemView;
    private List<Chat> chatList;
    private Utility utility;
    public static final String MyPREFERENCES = "MyPrefs" ;
    private Chat chat;

    TextView message_content, message_time;
    public ChatListAdapter(Context context, int resource, List<Chat> chatList) {
        super(context, resource, chatList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Chat chat= getItem(position);

        utility = new Utility();
        if(chat.getMessageType().equals(Constant.CHAT_MESSAGE_TYPE_SENT)){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.single_chat_message_sent_item, parent, false);
        } else{
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.single_chat_message_recieve_item, parent, false);
        }

        message_content = (TextView) listItemView.findViewById(R.id.single_chat_item_message_content);
        message_time= (TextView) listItemView.findViewById(R.id.single_chat_item_message_time);

        message_content.setText(chat.getContent());
        message_time.setText(utility.convertTimeForChat(chat.getTime()));
        return listItemView;
    }
}
