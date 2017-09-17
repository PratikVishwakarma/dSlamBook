package major.com.dslambook.Pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by prati on 23-Mar-17.
 */

public class User implements Parcelable {

    public String name, userName, email, dob, gender, image, dateAndTime, aboutMe;
    public int isProvideBasicInformation;

    public User() {
    }

    public User(String name, String userName, String email, String dob, String gender, String image, String dateAndTime,
                int isProvideBasicInformation, String aboutMe) {
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.dob = dob;
        this.gender = gender;
        this.image = image;
        this.dateAndTime = dateAndTime;
        this.isProvideBasicInformation = isProvideBasicInformation;
        this.aboutMe = aboutMe;
    }

    protected User(Parcel in) {
        name = in.readString();
        userName = in.readString();
        email = in.readString();
        dob = in.readString();
        gender = in.readString();
        image = in.readString();
        dateAndTime = in.readString();
        aboutMe = in.readString();
        isProvideBasicInformation = in.readInt();
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

    public String getName() {
        return name;
    }

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

    public String getImage() {
        return image;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public int getIsProvideBasicInformation() {
        return isProvideBasicInformation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(userName);
        dest.writeString(email);
        dest.writeString(dob);
        dest.writeString(gender);
        dest.writeString(image);
        dest.writeString(dateAndTime);
        dest.writeString(aboutMe);
        dest.writeInt(isProvideBasicInformation);
    }
}
