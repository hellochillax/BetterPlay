package wang.chillax.betterplay.bmob;

import android.os.Parcel;
import android.os.Parcelable;

import java.security.acl.Group;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    private int priority;//优先级,优先级越高排列越靠前
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


    public GroupFriend(int id, String name, double price, String note,String logoUrl,int priority) {
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
        priority=source.readInt();
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeString(note);
        dest.writeSerializable(logo);
        dest.writeString(logoUrl);
        dest.writeInt(priority);
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
                ", priority=" + priority +
                ", logoUrl='" + logoUrl + '\'' +
                '}';
    }
    public static void sortByPriority(List<GroupFriend> gfs){
        if(gfs==null||gfs.size()<=1)return;
        Collections.sort(gfs, new Comparator<GroupFriend>() {
            @Override
            public int compare(GroupFriend lhs, GroupFriend rhs) {
                return lhs.priority-rhs.priority;
            }
        });
    }
}
