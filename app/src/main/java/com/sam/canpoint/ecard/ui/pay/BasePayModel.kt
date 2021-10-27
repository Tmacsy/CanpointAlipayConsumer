package com.sam.canpoint.ecard.ui.pay

import com.alipay.iot.sdk.APIManager
import com.alipay.zoloz.smile2pay.ZolozConstants
import com.alipay.zoloz.smile2pay.verify.FaceVerifyResult
import com.alipay.zoloz.smile2pay.verify.Smile2PayResponse
import com.sam.canpoint.ecard.api.bean.AliPayResult
import com.sam.canpoint.ecard.api.bean.GetAccountInfoListResponse
import com.sam.canpoint.ecard.api.bean.GetConsumeRuleResponse
import com.sam.canpoint.ecard.api.request.ConsumptionLocalRecordsRequest
import com.sam.canpoint.ecard.api.request.FacePayEntity
import com.sam.canpoint.ecard.ui.model.AliPayBaseModel
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.canpoint.ecard.utils.PayDigestUtil
import com.sam.canpoint.ecard.utils.SnowflakeIdWorker
import com.sam.canpoint.ecard.utils.Utils
import com.sam.db.SamDBManager
import com.sam.db.info.WhereInfo
import com.sam.utils.device.DeviceUtils
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

open class BasePayModel : AliPayBaseModel() {

    open fun getSignMap(response: Smile2PayResponse, price: String, isOnLine: String): Map<String, Any> {
        val map = HashMap<String, Any>()
        val faceVerifyResult = response.extInfo[ZolozConstants.KEY_FACE_VERIFY_RESULT] as FaceVerifyResult
        return map.apply {
            put("consumeAmount", BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP))
            put("faceToken", response.faceToken)
            put("scene", "1")
            put("schoolCode", CanPointSp.schoolCode)
            put("uid", faceVerifyResult.uid)
            put("isOnline", isOnLine)
            put("consumeType", CanPointSp.FACE_CONSUMPTION)
            put("sn", "")
            put("timeStamp", (System.currentTimeMillis() / 1000L).toString())
            put("uuidFlag", SnowflakeIdWorker(Utils.nextInt(0, 1023)).nextId().toString())
            put("terminalParams", APIManager.getInstance().paymentAPI.signWithFaceToken(response.faceToken))
        }
    }

    open fun FacePayEntity.initEntity(map: Map<String, Any>, uuid: String? = null): FacePayEntity {
        consumeAmount = map["consumeAmount"] as BigDecimal?
        faceToken = map["faceToken"] as String?
        scene = map["scene"] as String?
        schoolCode = map["schoolCode"] as String?
        uid = map["uid"] as String?
        isOnline = map["isOnline"] as String?
        consumeType = map["consumeType"] as String?
        sn = map["sn"] as String?
        timeStamp = map["timeStamp"] as String?
        uuidFlag = if (!uuid.isNullOrEmpty()) uuid else map["uuidFlag"] as String?
        terminalParams = map["terminalParams"] as String?
        sign = PayDigestUtil.getSign(map, DeviceUtils.getAndroidID())
        return this
    }

    /**
     * 保存数据到本地
     */
    open fun saveLocalRecord(entity: FacePayEntity, userName: String?, arriveAmount: BigDecimal?, isSynchronized: Boolean, businessChannel: String? = "", orderId: String? = "") {
        val onlineRecordsRequest = ConsumptionLocalRecordsRequest()
        SamDBManager.getInstance().dao(ConsumptionLocalRecordsRequest::class.java).add(onlineRecordsRequest.apply {
            this.arriveAmount = arriveAmount
            this.terminalParams = entity.terminalParams
            timeStamp = entity.timeStamp
            this.consumeAmount = entity.consumeAmount
            this.userName = userName
            consumeType = entity.consumeType
            zfbUid = entity.uid
            isOnline = entity.isOnline
            faceToken = entity.faceToken
            scene = entity.scene
            schoolCode = entity.schoolCode
            this.isSynchronized = isSynchronized
            this.businessChannel = businessChannel
            this.orderId = orderId
            uuidFlag = entity.uuidFlag
        })
    }

    /**
     * 离线模式 消费规则判断
     */
    open fun initOffLineResult(userInfo: GetAccountInfoListResponse?, payPrice: String?, sumOfflineAmount: BigDecimal): AliPayResult? {
        userInfo?.let { user ->
            //获取全局消费规则
            val dataEntities = SamDBManager.getInstance().dao(GetConsumeRuleResponse::class.java).queryAll()
            if (dataEntities.isNullOrEmpty()) return initResultPageBean(null, null, null, GetAccountInfoListResponse(), "消费规则异常!", -5)
            val dataEntity = dataEntities[0]
            val consumeAmount = BigDecimal(payPrice)
            //全局消费规则 当天最大消费金额
            val everydayConsume = dataEntity.everydayConsume
            //当前用户 当天最大消费金额
            val userEverydayConsume = user.everydayConsume
            //全局消费规则 单次最大消费金额
            val singleConsume = dataEntity.singleConsume
            //当前用户 单次最大消费金额
            val userSingleConsume = user.singleConsume
            //当前身份的折扣金额
            val discountAmount = when (user.preferType) {
                //消费类型（0：无优惠，1：折扣，2：减免）
                "1" -> {
                    val discount = if (user.discount == null) BigDecimal("0.00") else user.discount
                    consumeAmount.subtract(consumeAmount.multiply(discount).setScale(2, RoundingMode.UP))
                }
                "2" -> {
                    if (user.remit == null) BigDecimal("0.00") else user.remit
                }
                else -> {
                    BigDecimal("0.00").setScale(2, RoundingMode.UP)
                }
            }
            var arriveAmount = consumeAmount.subtract(discountAmount).setScale(2, RoundingMode.UP)
            //当前身份的实付金额
            if (arriveAmount.toDouble() < 0.01) {
                arriveAmount = BigDecimal("0.01")
            }
            //查询当前身份的离线消费记录
            val currentUserOfflineRecords = SamDBManager.getInstance().dao(ConsumptionLocalRecordsRequest::class.java)
                    .query(WhereInfo.get().equal(ConsumptionLocalRecordsRequest._isSynchronized, false)
                            .equal(ConsumptionLocalRecordsRequest._zfbUid, user.zfbUid))
            //当前身份的离线消费记录超过设置的可脱机欠款消费次数，则不让消费
            if (null != currentUserOfflineRecords && currentUserOfflineRecords.size > CanPointSp.debtCount - 1) {
                //每人只能脱机消费所设置的限定次数！
                return initResultPageBean(discountAmount, arriveAmount, consumeAmount, user, "超出当前设备消费次数！", -5)
            }
            //已欠款金额
            if (userInfo.debtingAmount.toDouble() > 0.0) {
                //有欠款用户一次都不能消费！
                return initResultPageBean(discountAmount, arriveAmount, consumeAmount, user, "您有一笔未缴纳的欠款，请先缴纳！", -4)
            }
//            if (sumOfflineAmount.add(arriveAmount) > userInfo.debtAmount) {
//                //超过账户可欠费金额！
//                return initResultPageBean(discountAmount, arriveAmount, consumeAmount, user, "超过账户可欠费金额！", -4)
//            }
            //全局消费规则最大单次
            if (arriveAmount > singleConsume) {
                //超过单次消费限额！
                return initResultPageBean(discountAmount, arriveAmount, consumeAmount, user, "本次消费金额已超额，限额" + singleConsume + "元！", -1)
            }
            //用户单次
            if (arriveAmount > userSingleConsume) {
                //超过单次消费限额！
                return initResultPageBean(discountAmount, arriveAmount, consumeAmount, user, "本次消费金额已超额，限额" + userSingleConsume + "元！", -1)
            }
            //查询当前身份的消费记录
            val offlineRecords = SamDBManager.getInstance().dao(ConsumptionLocalRecordsRequest::class.java)
                    .query(WhereInfo.get().equal(ConsumptionLocalRecordsRequest._zfbUid, user.zfbUid))
            var todayMaxMoney = BigDecimal(0) //当前身份当天 消费金额总计
            //计算当前身份当天消费是否超过限额
            if (!offlineRecords.isNullOrEmpty()) {
                for (offlineRecord in offlineRecords) {
                    val date = Date(offlineRecord.timeStamp.toLong())
                    val today = isToday(date)
                    if (today) {
                        val actualAmount = offlineRecord.arriveAmount
                        todayMaxMoney = todayMaxMoney.add(actualAmount)
                    }
                }
                if (todayMaxMoney.add(arriveAmount) > everydayConsume) {
                    //大于全局消费规则
                    return initResultPageBean(discountAmount, arriveAmount, consumeAmount, user, "今日消费金额已超过设置限额，限额" + everydayConsume + "元！", -3)
                }
                if (todayMaxMoney.add(arriveAmount) > userEverydayConsume) {
                    //大于用户当天最大消费金额
                    return initResultPageBean(discountAmount, arriveAmount, consumeAmount, user, "今日消费金额已超过设置限额，限额" + userEverydayConsume + "元！", -3)
                }
            }
            return initResultPageBean(discountAmount, arriveAmount, consumeAmount, user, "支付成功", Smile2PayResponse.CODE_SUCCESS)
        }
        return initResultPageBean(null, null, null, GetAccountInfoListResponse(), "用户数据异常!", -6)
    }

    /**
     * @param discountAmount 折扣的金额
     * @param arriveAmount 实付金额
     * @param consumeAmount 订单金额
     */
    open fun initResultPageBean(discountAmount: BigDecimal?, arriveAmount: BigDecimal?, consumeAmount: BigDecimal?, user: GetAccountInfoListResponse, infoString: String, resultCode: Int): AliPayResult {
        if (resultCode != Smile2PayResponse.CODE_SUCCESS) {
            Utils.postCatchException(infoString + "的异常，userName=" +
                    user.userName + ",idCard=" + user.idCard + ",设备SN=" + DeviceUtils.getAndroidID())
        }
        return AliPayResult(user.userName, discountAmount, arriveAmount, consumeAmount, user.mobile, 0, infoString, resultCode)
    }

    private fun isToday(inputJudgeDate: Date): Boolean {
        var flag = false
        //获取当前系统时间
        val longDate = System.currentTimeMillis()
        val nowDate = Date(longDate)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val format = dateFormat.format(nowDate)
        val subDate = format.substring(0, 10)
        //定义每天的24h时间范围
        val beginTime = "$subDate 00:00:00"
        val endTime = "$subDate 23:59:59"
        var paseBeginTime: Date? = null
        var paseEndTime: Date? = null
        try {
            paseBeginTime = dateFormat.parse(beginTime)
            paseEndTime = dateFormat.parse(endTime)
        } catch (e: Exception) {
        }
        if (inputJudgeDate.after(paseBeginTime) && inputJudgeDate.before(paseEndTime)) {
            flag = true
        }
        return flag
    }
}