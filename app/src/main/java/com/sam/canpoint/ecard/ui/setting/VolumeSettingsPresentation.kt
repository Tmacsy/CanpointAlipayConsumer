package com.sam.canpoint.ecard.ui.setting

import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.PresentationVolumeSettingsBinding
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.tyx.base.mvvm.ktx.createVM
import java.util.*

class VolumeSettingsPresentation(private val mContext: Context, display: Display) : BasePresentation(mContext, display) {
    private lateinit var mBinding: PresentationVolumeSettingsBinding
    private lateinit var mViewModel: VolumeSettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.presentation_volume_settings, null, false)
        setContentView(mBinding.root)
        mViewModel = (mContext as VolumeSettingsActivity).createVM(VolumeSettingsViewModel::class.java)
        observer()
    }

    private fun observer() {
        mViewModel.currentVolume.observe(mContext as LifecycleOwner, Observer {
            mBinding.volumeSeekBar.progress = it
            mBinding.volumeSeekBarText.text = String.format(Locale.getDefault(), "%d%%", it * 100 / (mViewModel.maxVolume.value ?: 0))
        })
    }

    override fun showNetworkImg(text: String, value: Int) {
        mBinding.tvNetStatus.text = text
        mBinding.ivNetStatus.setImageResource(value)
    }
}