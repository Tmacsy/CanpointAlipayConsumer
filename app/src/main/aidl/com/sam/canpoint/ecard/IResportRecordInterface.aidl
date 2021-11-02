
package com.sam.canpoint.ecard.service;

import com.sam.canpoint.ecard.IResportRecordCallback;

interface IResportRecordInterface {
    fun registerCallback(IResportRecordCallback callback);
    fun unRegisterCallback();
    fun startReport();
    fun stopReport();
    fun notifyReport();
}