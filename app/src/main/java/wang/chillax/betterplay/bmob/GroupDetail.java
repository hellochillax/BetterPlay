package wang.chillax.betterplay.bmob;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by MAC on 16/3/22.
 * 商家详情
 */
public class GroupDetail extends BmobObject {

    private BmobFile image;//商家头像
    private String title;//团购标题
    private double price;//价格
    private double back;//返现
    private int buys;//购买人数
    private String group_address;//商家介绍链接
    private String rule;//使用规则
    private String group_time;//团购时间
    private String legal_time;//合法使用期
    private int group_id;//对应的惟一的GroupFriend的id



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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getBuys() {
        return buys;
    }

    public void setBuys(int buys) {
        this.buys = buys;
    }

    public String getGroup_address() {
        return group_address;
    }

    public void setGroup_address(String group_address) {
        this.group_address = group_address;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public double getBack() {
        return back;
    }

    public void setBack(double back) {
        this.back = back;
    }

    public String getGroup_time() {
        return group_time;
    }

    public void setGroup_time(String group_time) {
        this.group_time = group_time;
    }

    public String getLegal_time() {
        return legal_time;
    }

    public void setLegal_time(String legal_time) {
        this.legal_time = legal_time;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    @Override
    public String toString() {
        return "GroupDetail{" +
                "image=" + image +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", buys=" + buys +
                ", group_address='" + group_address + '\'' +
                ", rule='" + rule + '\'' +
                ", group_time='" + group_time + '\'' +
                ", legal_time='" + legal_time + '\'' +
                ", group_id=" + group_id +
                '}';
    }
}
