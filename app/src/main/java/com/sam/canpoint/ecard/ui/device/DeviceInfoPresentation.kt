package com.sam.canpoint.ecard.ui.device

import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.PresentationDeviceInfoBinding
import com.sam.canpoint.ecard.keyboard.KeyBoardEvent
import com.sam.canpoint.ecard.keyboard.KeyBoardType
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.canpoint.ecard.utils.presentationNetWorkImg
import com.sam.canpoint.ecard.utils.presenterNetWorkStr
import com.sam.utils.device.DeviceUtils
import com.sam.utils.network.NetworkUtils
import com.tyx.base.BaseNetWorkActivity
import com.tyx.base.mvvm.ktx.createVM

class DeviceInfoPresentation(private val mContext: Context, display: Display) : BasePresentation(mContext, display) {

    private lateinit var mBinding: PresentationDeviceInfoBinding
    private lateinit var mViewModel: DeviceViewModel
    private var currentPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.presentation_device_info, null, false)
        setContentView(mBinding.root)
        mViewModel = (mContext as DeviceActivity).createVM(DeviceViewModel::class.java)
        initView()
        observer()
    }

    private fun initView() {
        val netWorkState = mViewModel.netWorkState.value ?: BaseNetWorkActivity.NetworkStateDef.WIFI
        showNetworkImg(netWorkState.presenterNetWorkStr(), netWorkState.presentationNetWorkImg())
        mBinding.tvBindSchool.text = CanPointSp.schoolName
        mBinding.tvIp.text = NetworkUtils.getIPAddress(true)
        mBinding.tvMac.text = DeviceUtils.getMacAddress()
    }

    private fun observer() {
        mViewModel.run {
            deviceName.observe(mContext as LifecycleOwner, Observer {
                mBinding.tvDeviceName.text = it
            })
            deviceSn.observe(mContext as LifecycleOwner, Observer {
                mBinding.tvSn.text = it
            })
            bindStore.observe(mContext, Observer {
                mBinding.tvBindStore.text = it
            })
            faceVersion.observe(mContext, Observer {
                mBinding.tvFaceVersionNumber.text = it
            })
            systemVersion.observe(mContext, Observer {
                mBinding.tvSystemVersionNumber.text = it
            })
            serviceAddress.observe(mContext, Observer {
                mBinding.tvServerAddress.text = it
            })
        }
    }

    override fun interceptKeyEvent(event: KeyBoardEvent): Boolean {
        if (event.type == KeyBoardType.DOWN) {
            if (currentPage == 4) return true
            currentPage += 1
            selectCurrentPage(currentPage)
        } else if (event.type == KeyBoardType.UP) {
            if (currentPage == 1) return true
            currentPage -= 1
            selectCurrentPage(currentPage)
        }
        return true
    }

    private fun selectCurrentPage(page: Int) {
        mBinding.llPageOne.visibility = View.GONE
        mBinding.llPageTwo.visibility = View.GONE
        mBinding.llPageThree.visibility = View.GONE
        mBinding.llPageFour.visibility = View.GONE
        when (page) {
            1 -> mBinding.llPageOne.visibility = View.VISIBLE
            2 -> mBinding.llPageTwo.visibility = View.VISIBLE
            3 -> mBinding.llPageThree.visibility = View.VISIBLE
            4 -> mBinding.llPageFour.visibility = View.VISIBLE
        }
    }

    override fun showNetworkImg(text: String, value: Int) {
        mBinding.tvNetStatus.text = text
        mBinding.ivNetStatus.setImageResource(value)
    }
}