package wang.chillax.betterplay.bmob;

import android.os.Parcel;
import android.os.Parcelable;

import cn.bmob.v3.BmobObject;

/**
 * Created by MAC on 15/12/16.
 * 订单类
 */
public class Order extends BmobObject implements Parcelable{

    private String order_id;//订单号
    private String username;//账户
    private int group_id;//团购id
    private String code;//推荐码
    private int count;//数量
    private double price;//总价
    private String title;
    private int status;

    public Order(Parcel source) {
        setObjectId(source.readString());
        order_id=source.readString();
        username=source.readString();
        group_id=source.readInt();
        code=source.readString();
        count=source.readInt();
        price=source.readDouble();
        title=source.readString();
        status=source.readInt();
    }

    public Order(String order_id, String username, int group_id, String code, int count, double price,String title,int status) {
        this.order_id = order_id;
        this.username = username;
        this.group_id = group_id;
        this.code = code;
        this.count = count;
        this.price = price;
        this.title=title;
        this.status=status;
    }

    public static final Creator<Order> CREATOR=new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            return new Order(source);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getObjectId());
        dest.writeString(order_id);
        dest.writeString(username);
        dest.writeInt(group_id);
        dest.writeString(code);
        dest.writeInt(count);
        dest.writeDouble(price);
        dest.writeString(title);
        dest.writeInt(status);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public static Creator<Order> getCREATOR() {
        return CREATOR;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
