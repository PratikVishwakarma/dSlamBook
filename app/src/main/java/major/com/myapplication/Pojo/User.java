package major.com.myapplication.Pojo;

/**
 * Created by prati on 23-Mar-17.
 */

public class User {

    public String userName, email, dob, gender, imageUrl, dateAndTime;
    public int isProvideBasicInformation;

    public User() {
    }

    public User(String userName, String email, String dob, String gender, String imageUrl, String dateAndTime, int isProvideBasicInformation) {
        this.userName = userName;
        this.email = email;
        this.dob = dob;
        this.gender = gender;
        this.imageUrl = imageUrl;
        this.dateAndTime = dateAndTime;
        this.isProvideBasicInformation = isProvideBasicInformation;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public int getIsProvideBasicInformation() {
        return isProvideBasicInformation;
    }
}
