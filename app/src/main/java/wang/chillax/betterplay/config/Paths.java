package wang.chillax.betterplay.config;

import android.os.Environment;

/**
 * Created by MAC on 15/12/1.
 */
public class Paths {
    private static String basePath= Environment.getExternalStorageDirectory()+"/BetterPlay";

    //图片缓存
    public static String imagesPath= basePath+"/images/";
    public static String iconLocal=imagesPath+"icon.png";//头像的大图
    public static String iconLocal2=imagesPath+"icon2.png";//头像剪贴之后的小图

    //数据库缓存
    public static String dbPath=basePath+"/dbs";


    //文档缓存
    public static String downloadPath=basePath+"/docs";
}
