package org.tensorflow.lite.examples.detection.wake.Produce;

import com.google.gson.annotations.SerializedName;

public class Item_Suggestion
{

    @SerializedName("Wake_No")
    private String Wake_AS_No;

    @SerializedName("Wake_Suggestion")
    private String Wake_AS_Suggestion;

    @SerializedName("Like_Count")
    private String Suggestion_Like;

    @SerializedName("Like_Include_Me")
    private String Like_InClude_Me;

    public Item_Suggestion(String wake_AS_No, String wake_AS_Suggestion, String suggestion_Like, String like_InClude_Me)
    {
        Wake_AS_No = wake_AS_No;
        Wake_AS_Suggestion = wake_AS_Suggestion;
        Suggestion_Like = suggestion_Like;
        Like_InClude_Me = like_InClude_Me;
    }

    public String getWake_AS_No()
    {
        return Wake_AS_No;
    }

    public void setWake_AS_No(String wake_AS_No)
    {
        Wake_AS_No = wake_AS_No;
    }

    public String getWake_AS_Suggestion()
    {
        return Wake_AS_Suggestion;
    }

    public void setWake_AS_Suggestion(String wake_AS_Suggestion)
    {
        Wake_AS_Suggestion = wake_AS_Suggestion;
    }

    public String getSuggestion_Like()
    {
        return Suggestion_Like;
    }

    public void setSuggestion_Like(String suggestion_Like)
    {
        Suggestion_Like = suggestion_Like;
    }

    public String getLike_InClude_Me()
    {
        return Like_InClude_Me;
    }

    public void setLike_InClude_Me(String like_InClude_Me)
    {
        Like_InClude_Me = like_InClude_Me;
    }
}
