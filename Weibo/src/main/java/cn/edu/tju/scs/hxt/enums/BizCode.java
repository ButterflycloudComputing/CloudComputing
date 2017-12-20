package cn.edu.tju.scs.hxt.enums;

/**
 * Created by catold on 15/12/19.
 */
public enum BizCode {
    // 全局错误码
    SUCCESS(200, "操作成功"),
    FAIL(500,"服务器操作失败"),
    NON_EXISTENT(404,"资源不存在"),
    AUTH_FAILED(403,"权限认证失败"),
    AUTHORIZE_FAILED(401,"用户认证失败"),
    BAD_REQUEST(400,"错误请求"),


    // 待议...根据后台逻辑分类
    ;

    private int state;
    private String message;

    BizCode(int state, String message) {
        this.state = state;
        this.message = message;
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
}
