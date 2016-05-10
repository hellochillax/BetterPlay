package wang.chillax.betterplay.bmob;

import android.os.Parcel;
import android.os.Parcelable;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by MAC on 15/12/1.
 */
public class User extends BmobUser implements Parcelable{

    BmobFile head;//头像下载路径
    String nickname;    //昵称
    String school;  //学校全称
    String sex;     //性别
    String phone;   //手机号
    String stunum;   //学号
    private String code;//用户推荐码
    private double score;//原来是用"用户积分",现在直接改成"返现"
    private int level;//用户等级(0~99普通用户,100~199代理用户,888管理员)

    public BmobFile getHead() {
        return head;
    }

    public void setHead(BmobFile head) {
        this.head = head;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStunum() {
        return stunum;
    }

    public void setStunum(String stunum) {
        this.stunum = stunum;
    }

    public static final Creator<User> CREATOR=new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User(Parcel source){
        setUsername(source.readString());
        head=(BmobFile) source.readSerializable();
        nickname=source.readString();
        school=source.readString();
        code=source.readString();
        score=source.readInt();
        level=source.readInt();
        sex=source.readString();
        phone=source.readString();
        stunum=source.readString();
    }

    public User(){

    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getUsername());
        dest.writeSerializable(head);
        dest.writeString(nickname);
        dest.writeString(school);
        dest.writeString(code);
        dest.writeDouble(score);
        dest.writeInt(level);
        dest.writeString(sex);
        dest.writeString(phone);
        dest.writeString(stunum);
    }
}
