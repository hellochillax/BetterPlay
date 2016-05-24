package wang.chillax.betterplay.model;

import java.util.List;

/**
 * Created by chenmo on 2016/5/22.
 */
public class School {
    private boolean success;
    private int error_code;
    private String message;
    private List<SchoolList> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<SchoolList> getData() {
        return data;
    }

    public void setData(List<SchoolList> data) {
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

    public static class SchoolList {
        private String school_id;
        private String school_name;
        private String school_pro_id;
        private String school_schooltype_id;

        public String getSchool_id() {
            return school_id;
        }

        public void setSchool_id(String school_id) {
            this.school_id = school_id;
        }

        public String getSchool_schooltype_id() {
            return school_schooltype_id;
        }

        public void setSchool_schooltype_id(String school_schooltype_id) {
            this.school_schooltype_id = school_schooltype_id;
        }

        public String getSchool_pro_id() {
            return school_pro_id;
        }

        public void setSchool_pro_id(String school_pro_id) {
            this.school_pro_id = school_pro_id;
        }

        public String getSchool_name() {
            return school_name;
        }

        public void setSchool_name(String school_name) {
            this.school_name = school_name;
        }
    }
}