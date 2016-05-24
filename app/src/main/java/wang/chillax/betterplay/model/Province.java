package wang.chillax.betterplay.model;

import java.util.List;

/**
 * Created by chenmo on 2016/5/21.
 */
public class Province {
    private boolean success;
    private int error_code;
    private String message;
    private List<ProvinceList> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<ProvinceList> getData() {
        return data;
    }

    public void setData(List<ProvinceList> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public static class ProvinceList {
        private String province_id;
        private String province_name;

        public String getProvince_id() {
            return province_id;
        }

        public void setProvince_id(String province_id) {
            this.province_id = province_id;
        }

        public String getProvince_name() {
            return province_name;
        }

        public void setProvince_name(String province_name) {
            this.province_name = province_name;
        }
    }
}
