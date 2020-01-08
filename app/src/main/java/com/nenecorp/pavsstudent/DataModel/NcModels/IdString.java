package com.nenecorp.pavsstudent.DataModel.NcModels;

public class IdString {
    private String stringId;
    private String stringContent;

    public IdString(String stringId, String stringContent) {
        this.stringId = stringId;
        this.stringContent = stringContent;
    }

    public String getStringId() {
        return stringId;
    }

    public void setStringId(String stringId) {
        this.stringId = stringId;
    }

    public String getStringContent() {
        return stringContent;
    }

    public void setStringContent(String stringContent) {
        this.stringContent = stringContent;
    }
}
