package wang.chillax.betterplay.bmob;

import cn.bmob.v3.BmobObject;

/**
 * Created by MAC on 15/12/30.
 */
public class CrashLog extends BmobObject{
    private String username;
    private String appversion;
    private String deviceversion;
    private String hardware;
    private String errorlog;

    public CrashLog(String username, String appversion, String deviceversion, String hardware, String errorlog) {
        super();
        this.username = username;
        this.appversion = appversion;
        this.deviceversion = deviceversion;
        this.hardware = hardware;
        this.errorlog = errorlog;
    }

    @Override
    public String toString() {
        return "CrashLog{" +
                "username='" + username + '\'' +
                ", appversion='" + appversion + '\'' +
                ", deviceversion='" + deviceversion + '\'' +
                ", hardware='" + hardware + '\'' +
                ", errorlog='" + errorlog + '\'' +
                '}';
    }

    public CrashLog copy(){
        CrashLog log=new CrashLog(username,appversion,deviceversion,hardware,errorlog);
        return log;
    }
}
