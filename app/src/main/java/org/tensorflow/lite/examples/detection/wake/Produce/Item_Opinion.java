package org.tensorflow.lite.examples.detection.wake.Produce;

import com.google.gson.annotations.SerializedName;

public class Item_Opinion
{

    @SerializedName("Wake_SO_No")
    private String Wake_SO_No;

    @SerializedName("Wake_SO_AS_No")
    private String Wake_SO_AS_No;

    @SerializedName("Wake_SO_Opinion")
    private String Wake_SO_Opinion;

    public Item_Opinion(String wake_SO_No, String wake_SO_AS_No, String wake_SO_Opinion)
    {
        Wake_SO_No = wake_SO_No;
        Wake_SO_AS_No = wake_SO_AS_No;
        Wake_SO_Opinion = wake_SO_Opinion;
    }

    public String getWake_SO_No()
    {
        return Wake_SO_No;
    }

    public void setWake_SO_No(String wake_SO_No)
    {
        Wake_SO_No = wake_SO_No;
    }

    public String getWake_SO_AS_No()
    {
        return Wake_SO_AS_No;
    }

    public void setWake_SO_AS_No(String wake_SO_AS_No)
    {
        Wake_SO_AS_No = wake_SO_AS_No;
    }

    public String getWake_SO_Opinion()
    {
        return Wake_SO_Opinion;
    }

    public void setWake_SO_Opinion(String wake_SO_Opinion)
    {
        Wake_SO_Opinion = wake_SO_Opinion;
    }
}
