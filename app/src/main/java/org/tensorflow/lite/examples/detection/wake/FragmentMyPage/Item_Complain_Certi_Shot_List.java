package org.tensorflow.lite.examples.detection.wake.FragmentMyPage;

import com.google.gson.annotations.SerializedName;

public class Item_Complain_Certi_Shot_List
{
    @SerializedName("Wake_Evidence_File")
    private String Wake_Evidence_File;

    @SerializedName("Wake_Evidence_Date")
    private String Wake_Evidence_Date;


    public Item_Complain_Certi_Shot_List(String wake_Evidence_File, String wake_Evidence_Date)
    {
        Wake_Evidence_File = wake_Evidence_File;
        Wake_Evidence_Date = wake_Evidence_Date;
    }

    public String getWake_Evidence_File()
    {
        return Wake_Evidence_File;
    }

    public void setWake_Evidence_File(String wake_Evidence_File)
    {
        Wake_Evidence_File = wake_Evidence_File;
    }

    public String getWake_Evidence_Date()
    {
        return Wake_Evidence_Date;
    }

    public void setWake_Evidence_Date(String wake_Evidence_Date)
    {
        Wake_Evidence_Date = wake_Evidence_Date;
    }
}
