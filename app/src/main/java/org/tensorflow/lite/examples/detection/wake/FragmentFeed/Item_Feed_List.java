package org.tensorflow.lite.examples.detection.wake.FragmentFeed;

import com.google.gson.annotations.SerializedName;

public class Item_Feed_List
{
    @SerializedName("Wake_Evidence_No")
    private String Wake_Evidence_No;

    @SerializedName("Wake_Evidence_JoinProject_index")
    private String Wake_Evidence_JoinProject_index;

    @SerializedName("Wake_Info_No")
    private String Wake_Info_No;

    @SerializedName("Wake_Info_Title")
    private String Wake_Info_Title;

    @SerializedName("Wake_Info_ThumbImages")
    private String Wake_Info_ThumbImages;

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("photo")
    private String photo;

    @SerializedName("Wake_Evidence_File_Type")
    private String Wake_Evidence_File_Type;

    @SerializedName("Wake_Evidence_File")
    private String Wake_Evidence_File;

    @SerializedName("Wake_Evidence_getExpType")
    private String Wake_Evidence_getExpType;

    @SerializedName("Wake_Evidence_Date")
    private String Wake_Evidence_Date;

    @SerializedName("Disposition_of_false_reports")
    private String Disposition_of_false_reports;

    @SerializedName("Wake_Info_Subscript")
    private String Wake_Info_Subscript;

    public Item_Feed_List(String wake_Evidence_No, String wake_Evidence_JoinProject_index, String wake_Info_No, String wake_Info_Title, String wake_Info_ThumbImages, String id, String name, String photo, String wake_Evidence_File_Type, String wake_Evidence_File, String wake_Evidence_getExpType, String wake_Evidence_Date, String disposition_of_false_reports, String wake_Info_Subscript)
    {
        Wake_Evidence_No = wake_Evidence_No;
        Wake_Evidence_JoinProject_index = wake_Evidence_JoinProject_index;
        Wake_Info_No = wake_Info_No;
        Wake_Info_Title = wake_Info_Title;
        Wake_Info_ThumbImages = wake_Info_ThumbImages;
        this.id = id;
        this.name = name;
        this.photo = photo;
        Wake_Evidence_File_Type = wake_Evidence_File_Type;
        Wake_Evidence_File = wake_Evidence_File;
        Wake_Evidence_getExpType = wake_Evidence_getExpType;
        Wake_Evidence_Date = wake_Evidence_Date;
        Disposition_of_false_reports = disposition_of_false_reports;
        Wake_Info_Subscript = wake_Info_Subscript;
    }

    public String getWake_Evidence_No()
    {
        return Wake_Evidence_No;
    }

    public void setWake_Evidence_No(String wake_Evidence_No)
    {
        Wake_Evidence_No = wake_Evidence_No;
    }

    public String getWake_Evidence_JoinProject_index()
    {
        return Wake_Evidence_JoinProject_index;
    }

    public void setWake_Evidence_JoinProject_index(String wake_Evidence_JoinProject_index)
    {
        Wake_Evidence_JoinProject_index = wake_Evidence_JoinProject_index;
    }

    public String getWake_Info_No()
    {
        return Wake_Info_No;
    }

    public void setWake_Info_No(String wake_Info_No)
    {
        Wake_Info_No = wake_Info_No;
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

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPhoto()
    {
        return photo;
    }

    public void setPhoto(String photo)
    {
        this.photo = photo;
    }

    public String getWake_Evidence_File_Type()
    {
        return Wake_Evidence_File_Type;
    }

    public void setWake_Evidence_File_Type(String wake_Evidence_File_Type)
    {
        Wake_Evidence_File_Type = wake_Evidence_File_Type;
    }

    public String getWake_Evidence_File()
    {
        return Wake_Evidence_File;
    }

    public void setWake_Evidence_File(String wake_Evidence_File)
    {
        Wake_Evidence_File = wake_Evidence_File;
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

    public String getDisposition_of_false_reports()
    {
        return Disposition_of_false_reports;
    }

    public void setDisposition_of_false_reports(String disposition_of_false_reports)
    {
        Disposition_of_false_reports = disposition_of_false_reports;
    }

    public String getWake_Info_Subscript()
    {
        return Wake_Info_Subscript;
    }

    public void setWake_Info_Subscript(String wake_Info_Subscript)
    {
        Wake_Info_Subscript = wake_Info_Subscript;
    }
}
