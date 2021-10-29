package com.sam.canpoint.ecard.ui.order

import androidx.lifecycle.MutableLiveData
import com.sam.canpoint.ecard.api.bean.OrderDetailResponse
import com.tyx.base.mvvm.BaseViewModel
import java.text.SimpleDateFormat
import java.util.*

class ConfirmOrderViewModel : BaseViewModel<ConfirmOrderModel>() {

    override fun createModel(): ConfirmOrderModel = ConfirmOrderModel()
    val netWorkState = MutableLiveData<Int>() //网络状态
    val queryOrderResult = MutableLiveData<OrderDetailResponse>()
    val currentDataTime = MutableLiveData<String>()

    init {
        currentDataTime.value = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }
}