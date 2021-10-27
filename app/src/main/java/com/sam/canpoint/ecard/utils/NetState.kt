package com.sam.canpoint.ecard.utils

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.sam.canpoint.ecard.R
import com.tyx.base.BaseNetWorkActivity

@BindingAdapter(value = ["netWorkState"], requireAll = false)
fun netState(img: ImageView, state: Int) {
    when (state) {
        BaseNetWorkActivity.NetworkStateDef.WIFI, BaseNetWorkActivity.NetworkStateDef.MOBILE -> {
            img.setImageResource(R.drawable.big_screen_home_ic_wifi)
        }
        BaseNetWorkActivity.NetworkStateDef.ETHERNET -> img.setImageResource(R.drawable.big_screen_home_ic_wire_online)
        BaseNetWorkActivity.NetworkStateDef.NO_NET_WORK -> img.setImageResource(R.drawable.big_screen_home_ic_nowifi)
    }
}


fun Int.presenterNetWorkStr(): String {
    return when (this) {
        BaseNetWorkActivity.NetworkStateDef.NO_NET_WORK -> "离线"
        else -> "在线"
    }
}


fun Int.presentationNetWorkImg(): Int {
    return return when (this) {
        BaseNetWorkActivity.NetworkStateDef.WIFI, BaseNetWorkActivity.NetworkStateDef.MOBILE -> R.drawable.small_screen_wifi
        BaseNetWorkActivity.NetworkStateDef.ETHERNET -> R.drawable.small_screen_home_ic_wire_online
        BaseNetWorkActivity.NetworkStateDef.NO_NET_WORK -> R.drawable.small_screen_home_ic_nowifi
        else -> R.drawable.small_screen_wifi
    }
}