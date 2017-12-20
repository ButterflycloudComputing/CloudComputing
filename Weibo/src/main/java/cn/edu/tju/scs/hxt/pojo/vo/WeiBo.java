package cn.edu.tju.scs.hxt.pojo.vo;

import cn.edu.tju.scs.hxt.CustomDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * Created by haoxiaotian on 2017/12/20 1:50.
 */
public class WeiBo {

    private String id;
    private String userId;
    private String text;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;
    private String repostsCount;
    private String attitudesCount;
    private String commentsCount;
    private String fromId;
    private String fromUser;
    private String fromText;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date fromCreateTime;
    private String fromRepostsCount;
    private String fromAttitudesCount;
    private String fromCommentsCount;

    public WeiBo() {
    }

    public WeiBo(String id, String userId, String text, String createTime, String repostsCount, String attitudesCount, String commentsCount, String fromId, String fromUser, String fromText, String fromCreateTime, String fromRepostsCount, String fromAttitudesCount, String fromCommentsCount) {
        this.id = id;
        this.userId = userId;
        this.text = text;
        this.createTime = new Date(createTime);
        this.repostsCount = repostsCount;
        this.attitudesCount = attitudesCount;
        this.commentsCount = commentsCount;
        this.fromId = fromId;
        this.fromUser = fromUser;
        this.fromText = fromText;
        if(fromCreateTime != null){
            this.fromCreateTime = new Date(fromCreateTime);
        }

        this.fromRepostsCount = fromRepostsCount;
        this.fromAttitudesCount = fromAttitudesCount;
        this.fromCommentsCount = fromCommentsCount;
    }

    public WeiBo(String [] array){
        this(array[0],array[1],array[2],array[3],array[4],array[5],array[6],array[7],array[8],array[9],array[10],array[11],array[12],array[13]);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRepostsCount() {
        return repostsCount;
    }

    public void setRepostsCount(String repostsCount) {
        this.repostsCount = repostsCount;
    }

    public String getAttitudesCount() {
        return attitudesCount;
    }

    public void setAttitudesCount(String attitudesCount) {
        this.attitudesCount = attitudesCount;
    }

    public String getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(String commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getFromText() {
        return fromText;
    }

    public void setFromText(String fromText) {
        this.fromText = fromText;
    }

    public Date getFromCreateTime() {
        return fromCreateTime;
    }

    public void setFromCreateTime(Date fromCreateTime) {
        this.fromCreateTime = fromCreateTime;
    }

    public String getFromRepostsCount() {
        return fromRepostsCount;
    }

    public void setFromRepostsCount(String fromRepostsCount) {
        this.fromRepostsCount = fromRepostsCount;
    }

    public String getFromAttitudesCount() {
        return fromAttitudesCount;
    }

    public void setFromAttitudesCount(String fromAttitudesCount) {
        this.fromAttitudesCount = fromAttitudesCount;
    }

    public String getFromCommentsCount() {
        return fromCommentsCount;
    }

    public void setFromCommentsCount(String fromCommentsCount) {
        this.fromCommentsCount = fromCommentsCount;
    }
}
