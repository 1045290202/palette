package com.sjk.palette;

public class Option {
    private String optionName;
    private int optionImageID;

    public Option(String optionName, int optionImageID) {
        this.optionName = optionName;
        this.optionImageID = optionImageID;
    }

    public String getOptionName() {
        return optionName;
    }

    public int getOptionImageID() {
        return optionImageID;
    }
}
