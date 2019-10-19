package org.tensorflow.lite.examples.detection.wake.FragmentHome;

import com.google.gson.annotations.SerializedName;

public class Item_Project_List
{
    // 진행중인 프로젝트 테이블 인덱스 불러오기
    @SerializedName("Wake_Progress_No")
    String Wake_Progress_No;

    // 프로젝트 시작일
    @SerializedName("Wake_Progress_Start_Date")
    String Wake_Progress_Start_Date;

    // 프로젝트 종료일
    @SerializedName("Wake_Progress_End_Date")
    String Wake_Progress_End_Date;

    // 인증 빈도
    @SerializedName("Wake_Progress_Certi_Day")
    String Wake_Progress_Certi_Day;

    // 진행중인 프로젝트의 정보가 저장된 테이블의 인덱스
    @SerializedName("Wake_Progress_Info_No")
    String Wake_Progress_Info_No;

    // 프로젝트 제목
    @SerializedName("Wake_Info_Title")
    String Wake_Info_Title;

    // 카테고리
    @SerializedName("Wake_Info_Category")
    String Wake_Info_Category;

    // 역대 누적된 금액
    @SerializedName("Wake_Info_Total_Price")
    String Wake_Info_Total_Price;

    // 썸네일 사진 주소
    @SerializedName("Wake_Info_ThumbImages")
    String Wake_Info_ThumbImages;

    public Item_Project_List(String wake_Progress_No, String wake_Progress_Start_Date, String wake_Progress_End_Date, String wake_Progress_Certi_Day, String wake_Progress_Info_No, String wake_Info_Title, String wake_Info_Category, String wake_Info_Total_Price, String wake_Info_ThumbImages)
    {
        Wake_Progress_No = wake_Progress_No;
        Wake_Progress_Start_Date = wake_Progress_Start_Date;
        Wake_Progress_End_Date = wake_Progress_End_Date;
        Wake_Progress_Certi_Day = wake_Progress_Certi_Day;
        Wake_Progress_Info_No = wake_Progress_Info_No;
        Wake_Info_Title = wake_Info_Title;
        Wake_Info_Category = wake_Info_Category;
        Wake_Info_Total_Price = wake_Info_Total_Price;
        Wake_Info_ThumbImages = wake_Info_ThumbImages;
    }

    public String getWake_Progress_No()
    {
        return Wake_Progress_No;
    }

    public void setWake_Progress_No(String wake_Progress_No)
    {
        Wake_Progress_No = wake_Progress_No;
    }

    public String getWake_Progress_Start_Date()
    {
        return Wake_Progress_Start_Date;
    }

    public void setWake_Progress_Start_Date(String wake_Progress_Start_Date)
    {
        Wake_Progress_Start_Date = wake_Progress_Start_Date;
    }

    public String getWake_Progress_End_Date()
    {
        return Wake_Progress_End_Date;
    }

    public void setWake_Progress_End_Date(String wake_Progress_End_Date)
    {
        Wake_Progress_End_Date = wake_Progress_End_Date;
    }

    public String getWake_Progress_Certi_Day()
    {
        return Wake_Progress_Certi_Day;
    }

    public void setWake_Progress_Certi_Day(String wake_Progress_Certi_Day)
    {
        Wake_Progress_Certi_Day = wake_Progress_Certi_Day;
    }

    public String getWake_Progress_Info_No()
    {
        return Wake_Progress_Info_No;
    }

    public void setWake_Progress_Info_No(String wake_Progress_Info_No)
    {
        Wake_Progress_Info_No = wake_Progress_Info_No;
    }

    public String getWake_Info_Title()
    {
        return Wake_Info_Title;
    }

    public void setWake_Info_Title(String wake_Info_Title)
    {
        Wake_Info_Title = wake_Info_Title;
    }

    public String getWake_Info_Category()
    {
        return Wake_Info_Category;
    }

    public void setWake_Info_Category(String wake_Info_Category)
    {
        Wake_Info_Category = wake_Info_Category;
    }

    public String getWake_Info_Total_Price()
    {
        return Wake_Info_Total_Price;
    }

    public void setWake_Info_Total_Price(String wake_Info_Total_Price)
    {
        Wake_Info_Total_Price = wake_Info_Total_Price;
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
