package major.com.dslambook.Pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by prati on 23-Mar-17.
 */

public class User implements Parcelable {

    public String userName, email, dob, gender, imageUrl, dateAndTime;
    public int isProvideBasicInformation, friend;;

    public User() {
    }

    public User(String userName, String email, String dob, String gender, String imageUrl, String dateAndTime,
                int isProvideBasicInformation, int friend) {
        this.userName = userName;
        this.email = email;
        this.dob = dob;
        this.gender = gender;
        this.imageUrl = imageUrl;
        this.dateAndTime = dateAndTime;
        this.isProvideBasicInformation = isProvideBasicInformation;
        this.friend = friend;
    }

    protected User(Parcel in) {
        userName = in.readString();
        email = in.readString();
        dob = in.readString();
        gender = in.readString();
        imageUrl = in.readString();
        dateAndTime = in.readString();
        isProvideBasicInformation = in.readInt();
        friend = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public int getIsProvideBasicInformation() {
        return isProvideBasicInformation;
    }

    public int getFriend() {
        return friend;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(email);
        dest.writeString(dob);
        dest.writeString(gender);
        dest.writeString(imageUrl);
        dest.writeString(dateAndTime);
        dest.writeInt(isProvideBasicInformation);
        dest.writeInt(friend);
    }
}
