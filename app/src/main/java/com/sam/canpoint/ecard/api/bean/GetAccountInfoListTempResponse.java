package com.sam.canpoint.ecard.api.bean;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账户列表临时表
 */
@DatabaseTable(tableName = "ali_accountInfo_all_temp")
public class GetAccountInfoListTempResponse {

    public final static String _searchValue = "searchValue";
    public final static String _createBy = "createBy";
    public final static String _createTime = "createTime";
    public final static String _updateBy = "updateBy";
    public final static String _updateTime = "updateTime";
    public final static String _remark = "remark";
    public final static String _id = "id";
    public final static String _userCode = "userCode";
    public final static String _userName = "userName";
    public final static String _userType = "userType";
    public final static String _idCard = "idCard";
    public final static String _mobile = "mobile";
    public final static String _certType = "certType";
    public final static String _schoolIntcode = "schoolIntcode";
    public final static String _schoolStdcode = "schoolStdcode";
    public final static String _schoolCode = "schoolCode";
    public final static String _zfbUid = "zfbUid";
    public final static String _loginUid = "loginUid";
    public final static String _signUpNo = "signUpNo";
    public final static String _faceCollectStatus = "faceCollectStatus";
    public final static String _facePayStatus = "facePayStatus";
    public final static String _faceCollectTime = "faceCollectTime";
    public final static String _facePayTime = "facePayTime";
    public final static String _everydayConsume = "everydayConsume";
    public final static String _singleConsume = "singleConsume";
    public final static String _debtAmount = "debtAmount";
    public final static String _preferType = "preferType";
    public final static String _discount = "discount";
    public final static String _remit = "remit";
    public final static String _balance = "balance";
    public final static String _debtingAmount = "debtingAmount";
    public final static String _openEbank = "openEbank";
    public final static String _classBoards = "classBoards";
    public final static String _workNo = "workNo";
    public final static String _photoUrl = "photoUrl";
    /**
     * searchValue : null
     * createBy :
     * createTime : 2020-12-31 16:01:49
     * updateBy :
     * updateTime : null
     * remark : null
     * params : {}
     * id : 4841017446464817479
     * userCode : U14jy658nk7qsw
     * userName : 张立
     * userType : 2
     * idCard : 429006198910179339
     * mobile : 18702707106
     * certType : 1
     * schoolIntcode : 2088010897014930
     * schoolStdcode : 91420100MA4KWR801N
     * schoolCode : CANPOINTLIVE
     * zfbUid : 2088612191404970
     * loginUid : 2088612191404970
     * signUpNo : null
     * faceCollectStatus : 1
     * facePayStatus : 2
     * faceCollectTime : 2020-12-31 16:01:49
     * facePayTime : null
     * everydayConsume : 300
     * singleConsume : 100
     * debtAmount : 30
     * preferType : 0
     * discount : null
     * remit : null
     * balance: 838.87,
     * debtingAmount: 0,
     * openEbank: null,
     * classBoards : 研发组
     * workNo :
     * photoUrl : http://canpoint-photo.oss-cn-beijing.aliyuncs.com/997/2020/12/10/base/14c34efadfcd4576bbd2337f9b5decd9.jpg
     * parentList : []
     * ebankList : []
     */

    //        @DatabaseField
    private String searchValue;
    @DatabaseField
    private String createBy;
    @DatabaseField
    private String createTime;
    @DatabaseField
    private String updateBy;
    //        @DatabaseField
    private String updateTime;
    //        @DatabaseField
    private String remark;
    @DatabaseField
    private String id;
    @DatabaseField(id = true)
    private String userCode;
    @DatabaseField
    private String userName;
    @DatabaseField
    private String userType;
    @DatabaseField
    private String idCard;
    @DatabaseField
    private String mobile;
    @DatabaseField
    private String certType;
    @DatabaseField
    private String schoolIntcode;
    @DatabaseField
    private String schoolStdcode;
    @DatabaseField
    private String schoolCode;
    @DatabaseField
    private String zfbUid;
    @DatabaseField
    private String loginUid;
    //        @DatabaseField
    private String signUpNo;
    @DatabaseField
    private String faceCollectStatus;
    @DatabaseField
    private String facePayStatus;
    @DatabaseField
    private String faceCollectTime;
    //        @DatabaseField
    private String facePayTime;
    @DatabaseField
    private BigDecimal everydayConsume;
    @DatabaseField
    private BigDecimal singleConsume;
    @DatabaseField
    private BigDecimal debtAmount;
    @DatabaseField
    private String preferType;
    @DatabaseField
    private BigDecimal discount;
    @DatabaseField
    private BigDecimal remit;
    @DatabaseField
    private BigDecimal balance;
    @DatabaseField
    private BigDecimal debtingAmount;
    @DatabaseField
    private String openEbank;
    @DatabaseField
    private String classBoards;
    @DatabaseField
    private String workNo;
    @DatabaseField
    private String photoUrl;
    private List<?> parentList;
    private List<?> ebankList;

    public GetAccountInfoListTempResponse() {

    }

    public GetAccountInfoListTempResponse(GetAccountInfoListResponse accountInfo) {
        this.searchValue = accountInfo.getSearchValue();
        this.createBy = accountInfo.getCreateBy();
        this.createTime = accountInfo.getCreateTime();
        this.updateBy = accountInfo.getUpdateBy();
        this.updateTime = accountInfo.getUpdateTime();
        this.remark = accountInfo.getRemark();
        this.id = accountInfo.getId();
        this.userCode = accountInfo.getUserCode();
        this.userName = accountInfo.getUserName();
        this.userType = accountInfo.getUserType();
        this.idCard = accountInfo.getIdCard();
        this.mobile = accountInfo.getMobile();
        this.certType = accountInfo.getCertType();
        this.schoolIntcode = accountInfo.getSchoolIntcode();
        this.schoolStdcode = accountInfo.getSchoolStdcode();
        this.schoolCode = accountInfo.getSchoolCode();
        this.zfbUid = accountInfo.getZfbUid();
        this.loginUid = accountInfo.getLoginUid();
        this.signUpNo = accountInfo.getSignUpNo();
        this.faceCollectStatus = accountInfo.getFaceCollectStatus();
        this.facePayStatus = accountInfo.getFacePayStatus();
        this.faceCollectTime = accountInfo.getFaceCollectTime();
        this.facePayTime = accountInfo.getFacePayTime();
        this.everydayConsume = accountInfo.getEverydayConsume();
        this.singleConsume = accountInfo.getSingleConsume();
        this.debtAmount = accountInfo.getDebtAmount();
        this.preferType = accountInfo.getPreferType();
        this.discount = accountInfo.getDiscount();
        this.remit = accountInfo.getRemit();
        this.balance = accountInfo.getBalance();
        this.debtingAmount = accountInfo.getDebtingAmount();
        this.openEbank = accountInfo.getOpenEbank();
        this.classBoards = accountInfo.getClassBoards();
        this.workNo = accountInfo.getWorkNo();
        this.photoUrl = accountInfo.getPhotoUrl();
        this.parentList = accountInfo.getParentList();
        this.ebankList = accountInfo.getEbankList();
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType;
    }

    public String getSchoolIntcode() {
        return schoolIntcode;
    }

    public void setSchoolIntcode(String schoolIntcode) {
        this.schoolIntcode = schoolIntcode;
    }

    public String getSchoolStdcode() {
        return schoolStdcode;
    }

    public void setSchoolStdcode(String schoolStdcode) {
        this.schoolStdcode = schoolStdcode;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }

    public String getZfbUid() {
        return zfbUid;
    }

    public void setZfbUid(String zfbUid) {
        this.zfbUid = zfbUid;
    }

    public String getLoginUid() {
        return loginUid;
    }

    public void setLoginUid(String loginUid) {
        this.loginUid = loginUid;
    }

    public String getSignUpNo() {
        return signUpNo;
    }

    public void setSignUpNo(String signUpNo) {
        this.signUpNo = signUpNo;
    }

    public String getFaceCollectStatus() {
        return faceCollectStatus;
    }

    public void setFaceCollectStatus(String faceCollectStatus) {
        this.faceCollectStatus = faceCollectStatus;
    }

    public String getFacePayStatus() {
        return facePayStatus;
    }

    public void setFacePayStatus(String facePayStatus) {
        this.facePayStatus = facePayStatus;
    }

    public String getFaceCollectTime() {
        return faceCollectTime;
    }

    public void setFaceCollectTime(String faceCollectTime) {
        this.faceCollectTime = faceCollectTime;
    }

    public String getFacePayTime() {
        return facePayTime;
    }

    public void setFacePayTime(String facePayTime) {
        this.facePayTime = facePayTime;
    }

    public BigDecimal getEverydayConsume() {
        return everydayConsume;
    }

    public void setEverydayConsume(BigDecimal everydayConsume) {
        this.everydayConsume = everydayConsume;
    }

    public BigDecimal getSingleConsume() {
        return singleConsume;
    }

    public void setSingleConsume(BigDecimal singleConsume) {
        this.singleConsume = singleConsume;
    }

    public BigDecimal getDebtAmount() {
        return debtAmount;
    }

    public void setDebtAmount(BigDecimal debtAmount) {
        this.debtAmount = debtAmount;
    }

    public String getPreferType() {
        return preferType;
    }

    public void setPreferType(String preferType) {
        this.preferType = preferType;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getRemit() {
        return remit;
    }

    public void setRemit(BigDecimal remit) {
        this.remit = remit;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getDebtingAmount() {
        return debtingAmount;
    }

    public void setDebtingAmount(BigDecimal debtingAmount) {
        this.debtingAmount = debtingAmount;
    }

    public String getOpenEbank() {
        return openEbank;
    }

    public void setOpenEbank(String openEbank) {
        this.openEbank = openEbank;
    }

    public String getClassBoards() {
        return classBoards;
    }

    public void setClassBoards(String classBoards) {
        this.classBoards = classBoards;
    }

    public String getWorkNo() {
        return workNo;
    }

    public void setWorkNo(String workNo) {
        this.workNo = workNo;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<?> getParentList() {
        return parentList;
    }

    public void setParentList(List<?> parentList) {
        this.parentList = parentList;
    }

    public List<?> getEbankList() {
        return ebankList;
    }

    public void setEbankList(List<?> ebankList) {
        this.ebankList = ebankList;
    }
}
