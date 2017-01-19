package cn.com.zjs.cloud.coolcode;

/**
 * The base response
 * Created by Administrator on 2017/1/16.
 */
public class BaseResponse {
    // Default success code
    public static final int SUCCESS_CODE = 0;
    public static final int FAIL_DEFAULT_CODE = 1;

    public BaseResponse(){
        super();
    }

    public BaseResponse(int code, String message){
        this.code = code;
        this.message = message;
    }

    private int code;

    private String message;

    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
