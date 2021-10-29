package com.sam.canpoint.ecard.api.bean;

public class EventBean {

    public static class RestartSmile {
        public String tag;
    }

    public static class ChangeLocalRecord {
        public boolean result;

        public ChangeLocalRecord(boolean result) {
            this.result = result;
        }
    }

    public static class CloseConfirmOrderActivity {
        public String type;
    }
}
