package com.sam.canpoint.ecard.utils

import android.widget.ImageView
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