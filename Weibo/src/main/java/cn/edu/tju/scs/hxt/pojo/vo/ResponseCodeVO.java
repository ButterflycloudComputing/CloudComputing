package cn.edu.tju.scs.hxt.pojo.vo;


import cn.edu.tju.scs.hxt.enums.BizCode;

/**
 * Created by haoxiaotian on 2016/8/19 23:14.
 */
public class ResponseCodeVO<T> {
    private int state;
    private String message;
    private T data;

    public ResponseCodeVO(BizCode bizCode) {
        this.state = bizCode.getState();
        this.message = bizCode.getMessage();
    }

    public ResponseCodeVO(BizCode bizCode, T data) {
        this.state = bizCode.getState();
        this.message = bizCode.getMessage();

        this.data = data;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
