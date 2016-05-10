package wang.chillax.betterplay.bmob;

import android.os.Parcel;
import android.os.Parcelable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by MAC on 16/3/4.
 *
 * 商家列表
 *
 */
public class GroupFriend extends BmobObject implements Parcelable{

    private int id;
    private String name;
    private double price;
    private String note;
    private BmobFile logo;
    private String logoUrl;//也是为了缓存时用的,因为我们无法缓存一个BmobFile,所以这里新加一个字符

    public static final  Creator<GroupFriend> CREATOR=new Creator() {
        @Override
        public GroupFriend createFromParcel(Parcel source) {
            return new GroupFriend(source);
        }

        @Override
        public GroupFriend[] newArray(int size) {
            return new GroupFriend[0];
        }
    };


    public GroupFriend(int id, String name, double price, String note,String logoUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.note = note;
        this.logoUrl = logoUrl;
    }

    public GroupFriend(Parcel source){
        id=source.readInt();
        name=source.readString();
        price=source.readDouble();
        note=source.readString();
        logo= (BmobFile) source.readSerializable();
        logoUrl=source.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BmobFile getLogo() {
        return logo;
    }

    public void setLogo(BmobFile logo) {
        this.logo = logo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeString(note);
        dest.writeSerializable(logo);
        dest.writeString(logoUrl);
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    @Override
    public String toString() {
        return "GroupFriend{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", note='" + note + '\'' +
                ", logo=" + logo +
                ", logoUrl='" + logoUrl + '\'' +
                '}';
    }
}
