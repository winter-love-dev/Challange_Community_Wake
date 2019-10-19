package org.tensorflow.lite.examples.detection.wake;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient
{
//    public static final String BASE_URL = "http://34.73.32.3/"; // gcp
    public static final String BASE_URL = "http://115.68.231.84/"; // iwinv


    private static Retrofit retrofit;

    public static Retrofit getApiClient()
    {
        if (retrofit == null)
        {
            retrofit = new Retrofit.Builder().
                    baseUrl(BASE_URL).
                    addConverterFactory(GsonConverterFactory.create()).
                    build();
        }
        return retrofit;
    }
}
