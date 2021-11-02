
package com.sam.canpoint.ecard;

interface IResportRecordCallback {
    List<ConsumptionLocalRecordsRequest> getLocalReocrd();
    void updateHistory(ConsumptionLocalRecordsRequest request);
}