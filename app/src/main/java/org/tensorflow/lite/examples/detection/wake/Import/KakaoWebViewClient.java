package org.tensorflow.lite.examples.detection.wake.Import;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URISyntaxException;

public class KakaoWebViewClient extends WebViewClient
{
    private String TAG = "KakaoWebViewClient: ";

    private Activity activity;

    public KakaoWebViewClient(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {

        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:"))
        {
            Intent intent = null;

            try
            {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME); //IntentURI처리
                Log.e(TAG, "shouldOverrideUrlLoading: try url: " + url);

                Uri uri = Uri.parse(intent.getDataString());
                Log.e(TAG, "shouldOverrideUrlLoading: uri: " + uri );

                activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));

                return true;
            } catch (URISyntaxException ex)
            {
                Log.e(TAG, "shouldOverrideUrlLoading: URISyntaxException ex: " + ex );
                return false;
            } catch (ActivityNotFoundException e)
            {
                Log.e(TAG, "shouldOverrideUrlLoading: ActivityNotFoundException e: " + e );
                if (intent == null) return false;

                String packageName = intent.getPackage(); //packageName should be com.kakao.talk
                Log.e(TAG, "shouldOverrideUrlLoading: packageName: " + packageName );
                if (packageName != null)
                {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                    Log.e(TAG, "shouldOverrideUrlLoading: market://details?id=" + packageName);
                    return true;
                }
                return false;
            }
        }

        return false;
    }
}
