package skiteapps.gkadda;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    GridView gridView;
    private AdView mAdView;
    InterstitialAd mInterstitialAd;
    AdRequest adRequest2;
    private static final String messageUrl = "https://sheets.googleapis.com/v4/spreadsheets/1gymAaNCyzFRtTDyd2ztLYWO_s5DXRxQR4UQmLNukiFo/values/SHARE!A:E?key=AIzaSyAHRy3U0-UXRMxVJ09ubzT66KCmPeKkC98";

    String[] values = {

            "Practice Gk",
            "Quiz Test",
            "Current GK",
            "Short Questions",
            "Notes",
            "Points to Remember",
            "More Apps",
            "Rate Us",
            "Share",


    };

    int[] images = {
            R.drawable.ic_assignment_black_24dp,
            R.drawable.quiztest,
            R.drawable.currentaffair,
            R.drawable.shortques,
            R.drawable.notes,
            R.drawable.pointstoremember,
            R.drawable.moreapps,
            R.drawable.likeus,
            R.drawable.share,

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Welcome");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        gridView = (GridView) findViewById(R.id.griview);
        GridAdapter gridAdapter = new GridAdapter(MainActivity.this, values, images);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        Intent intent = new Intent(MainActivity.this, PracticeGkActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        Intent intent1 = new Intent(MainActivity.this, ShortQA.class);
                        startActivity(intent1);
                        break;
                    case 1:
                        Intent intent2 = new Intent(MainActivity.this, QuizTestActivity.class);
                        startActivity(intent2);
                        break;
                    case 4:
                        Intent intent3 = new Intent(MainActivity.this, NotesActivity.class);
                        startActivity(intent3);
                        break;
                    case 2:
                        Intent intent4 = new Intent(MainActivity.this, CurrentAffairsActivity.class);
                        startActivity(intent4);
                        break;
                    case 5:
                        Intent intent5 = new Intent(MainActivity.this, PointsToRememberActivity.class);
                        startActivity(intent5);
                        break;
                    case 6:
                        String devName = "SKITE APPS";
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://store/apps/developer?id=" + devName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/developer?id=" + devName)));
                        }
                        break;
                    case 7:
                        final String appPackageName = getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                        break;
                    case 8:
                        new MessageAsyncTask().execute(messageUrl);
                        break;
                }
            }
        });

        mInterstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial1));

        adRequest2 = new AdRequest.Builder().build();

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest2);

    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    private class MessageAsyncTask extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = strings[0];
            URL url;
            HttpURLConnection conn = null;
            InputStream in = null;
            try {
                url = new URL(stringUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");

                conn.connect();
                in = conn.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String jsonResponse) {
            super.onPostExecute(jsonResponse);
            if (jsonResponse != null) {
                String message = null;
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    JSONArray jsonArray = jsonObject.getJSONArray("values");
                    for (int i = 1; i < jsonArray.length(); i++) {
                        JSONArray jsonArray1 = jsonArray.getJSONArray(i);
                        message = jsonArray1.getString(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = message + "\nhttps://play.google.com/store/apps/details?id=skiteapps.gkadda";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "GK ADDA");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (mInterstitialAd.isLoaded()) {
            showInterstitial();
        }
        super.onDestroy();
    }
}
