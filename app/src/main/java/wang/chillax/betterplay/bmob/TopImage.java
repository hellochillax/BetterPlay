package wang.chillax.betterplay.bmob;

import android.os.Parcel;
import android.os.Parcelable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by MAC on 16/3/17.
 */
public class TopImage extends BmobObject implements Parcelable{


    private BmobFile image;
    private String title;
    private String address;
    private String imageUrl;//这里是缓存的图片URL,如果该字符串不为空,则应使用该字符串.

    public TopImage(String imageUrl, String title, String address) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.address = address;
    }
    public TopImage(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getObjectId());
        dest.writeSerializable(image);
        dest.writeString(title);
        dest.writeString(address);
        dest.writeString(imageUrl);
    }
    public static final Creator<TopImage> CREATOR=new Creator<TopImage>() {
        @Override
        public TopImage createFromParcel(Parcel source) {
            TopImage item=new TopImage();
            item.setObjectId(source.readString());
            item.setImage((BmobFile) source.readSerializable());
            item.setTitle(source.readString());
            item.setAddress(source.readString());
            item.setImageUrl(source.readString());
            return item;
        }

        @Override
        public TopImage[] newArray(int size) {
            return new TopImage[0];
        }
    };

    public BmobFile getImage() {
        return image;
    }

    public void setImage(BmobFile image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static Creator<TopImage> getCREATOR() {
        return CREATOR;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "TopImage{" +
                "image=" + image +
                ", title='" + title + '\'' +
                ", address='" + address + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
