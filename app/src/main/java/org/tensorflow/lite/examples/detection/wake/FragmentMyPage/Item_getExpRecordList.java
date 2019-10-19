package org.tensorflow.lite.examples.detection.wake.FragmentMyPage;

import com.google.gson.annotations.SerializedName;

public class Item_getExpRecordList
{
    @SerializedName("Wake_Evidence_getExp")
    private String Wake_Evidence_getExp;

    @SerializedName("Wake_Evidence_getExpType")
    private String Wake_Evidence_getExpType;

    @SerializedName("Wake_Evidence_Date")
    private String Wake_Evidence_Date;

    @SerializedName("Wake_Evidence_JoinProject_index")
    private String Wake_Evidence_JoinProject_index;

    @SerializedName("Wake_Info_Title")
    private String Wake_Info_Title;

    @SerializedName("Wake_Info_ThumbImages")
    private String Wake_Info_ThumbImages;

    public Item_getExpRecordList(String wake_Evidence_getExp, String wake_Evidence_getExpType, String wake_Evidence_Date, String wake_Evidence_JoinProject_index, String wake_Info_Title, String wake_Info_ThumbImages)
    {
        Wake_Evidence_getExp = wake_Evidence_getExp;
        Wake_Evidence_getExpType = wake_Evidence_getExpType;
        Wake_Evidence_Date = wake_Evidence_Date;
        Wake_Evidence_JoinProject_index = wake_Evidence_JoinProject_index;
        Wake_Info_Title = wake_Info_Title;
        Wake_Info_ThumbImages = wake_Info_ThumbImages;
    }

    public String getWake_Evidence_getExp()
    {
        return Wake_Evidence_getExp;
    }

    public void setWake_Evidence_getExp(String wake_Evidence_getExp)
    {
        Wake_Evidence_getExp = wake_Evidence_getExp;
    }

    public String getWake_Evidence_getExpType()
    {
        return Wake_Evidence_getExpType;
    }

    public void setWake_Evidence_getExpType(String wake_Evidence_getExpType)
    {
        Wake_Evidence_getExpType = wake_Evidence_getExpType;
    }

    public String getWake_Evidence_Date()
    {
        return Wake_Evidence_Date;
    }

    public void setWake_Evidence_Date(String wake_Evidence_Date)
    {
        Wake_Evidence_Date = wake_Evidence_Date;
    }

    public String getWake_Evidence_JoinProject_index()
    {
        return Wake_Evidence_JoinProject_index;
    }

    public void setWake_Evidence_JoinProject_index(String wake_Evidence_JoinProject_index)
    {
        Wake_Evidence_JoinProject_index = wake_Evidence_JoinProject_index;
    }

    public String getWake_Info_Title()
    {
        return Wake_Info_Title;
    }

    public void setWake_Info_Title(String wake_Info_Title)
    {
        Wake_Info_Title = wake_Info_Title;
    }

    public String getWake_Info_ThumbImages()
    {
        return Wake_Info_ThumbImages;
    }

    public void setWake_Info_ThumbImages(String wake_Info_ThumbImages)
    {
        Wake_Info_ThumbImages = wake_Info_ThumbImages;
    }
}
