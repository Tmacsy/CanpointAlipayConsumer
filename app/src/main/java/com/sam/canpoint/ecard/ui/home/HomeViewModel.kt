package com.sam.canpoint.ecard.ui.home

import com.tyx.base.mvvm.BaseViewModel

class HomeViewModel : BaseViewModel<HomeModel>(){
    override fun createModel(): HomeModel = HomeModel()
}