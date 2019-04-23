package cn.cch.healthy.model;

import java.util.Date;

public class Userinfo {
    private Integer userId;

    private String userOpenid;

    private String userName;

    private String userSex;

    private Integer userAge;

    private Double userHeight;

    private Double userWeight;

    private Date userBirthday;

    private String userCcupation;

    private String userType;

    private String userNumber;

    private String userFaceToken;

    private Date userRecognize;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserOpenid() {
        return userOpenid;
    }

    public void setUserOpenid(String userOpenid) {
        this.userOpenid = userOpenid == null ? null : userOpenid.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex == null ? null : userSex.trim();
    }

    public Integer getUserAge() {
        return userAge;
    }

    public void setUserAge(Integer userAge) {
        this.userAge = userAge;
    }

    public Double getUserHeight() {
        return userHeight;
    }

    public void setUserHeight(Double userHeight) {
        this.userHeight = userHeight;
    }

    public Double getUserWeight() {
        return userWeight;
    }

    public void setUserWeight(Double userWeight) {
        this.userWeight = userWeight;
    }

    public Date getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(Date userBirthday) {
        this.userBirthday = userBirthday;
    }

    public String getUserCcupation() {
        return userCcupation;
    }

    public void setUserCcupation(String userCcupation) {
        this.userCcupation = userCcupation == null ? null : userCcupation.trim();
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType == null ? null : userType.trim();
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber == null ? null : userNumber.trim();
    }

    public String getUserFaceToken() {
        return userFaceToken;
    }

    public void setUserFaceToken(String userFaceToken) {
        this.userFaceToken = userFaceToken == null ? null : userFaceToken.trim();
    }

    public Date getUserRecognize() {
        return userRecognize;
    }

    public void setUserRecognize(Date userRecognize) {
        this.userRecognize = userRecognize;
    }

    @Override
    public String toString() {
        return "Userinfo{" +
                "userId=" + userId +
                ", userOpenid='" + userOpenid + '\'' +
                ", userName='" + userName + '\'' +
                ", userSex='" + userSex + '\'' +
                ", userAge=" + userAge +
                ", userHeight=" + userHeight +
                ", userWeight=" + userWeight +
                ", userBirthday=" + userBirthday +
                ", userCcupation='" + userCcupation + '\'' +
                ", userType='" + userType + '\'' +
                ", userNumber='" + userNumber + '\'' +
                ", userFaceToken='" + userFaceToken + '\'' +
                ", userRecognize=" + userRecognize +
                '}';
    }
}