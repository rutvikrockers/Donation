package com.rock.qikso.pojo;

import java.util.List;

/**
 * Created by rockers on 24/3/17.
 */

public class ResponseForgotPassword {
    public List<Object> data;
    public boolean success;
    public String message;
    public int error_code;

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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
}
