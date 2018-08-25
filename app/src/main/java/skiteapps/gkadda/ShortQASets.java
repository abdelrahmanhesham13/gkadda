package skiteapps.gkadda;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.Scanner;

public class ShortQASets extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView textView;
    private Intent intent;
    private String selectedTopicId;
    private ArrayList<Quiz> quizzes;
    private RecyclerView recyclerView;
    private QuizAdapter quizAdapter;
    private AdView mAdView;
    InterstitialAd mInterstitialAd;
    AdRequest adRequest2;
    private static final String quizUrl = "https://sheets.googleapis.com/v4/spreadsheets/1gymAaNCyzFRtTDyd2ztLYWO_s5DXRxQR4UQmLNukiFo/values/SHORT%20Q&A%20SETS!A:D?key=AIzaSyAHRy3U0-UXRMxVJ09ubzT66KCmPeKkC98";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_qasets);
        setTitle("Topic Sets");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        intent = getIntent();
        textView = (TextView) findViewById(R.id.quiz_no_internet);
        progressBar = (ProgressBar) findViewById(R.id.quiz_progress_bar);
        quizzes = new ArrayList<>();
        selectedTopicId = intent.getStringExtra("selectedTopicId");
        recyclerView = (RecyclerView) findViewById(R.id.quiz_recycler_view);
        quizAdapter = new QuizAdapter(quizzes, new QuizAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {
                Intent intent = new Intent(ShortQASets.this,ShortQAQuestions.class);
                intent.putExtra("selectedQuizId",quizzes.get(position).getQuizId());
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(quizAdapter);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new QuizAsyncTask().execute(quizUrl);
        } else {
            textView.setText("No internet connection");
            textView.setVisibility(View.VISIBLE);
        }
        mInterstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial1));

        adRequest2 = new AdRequest.Builder()
                .build();

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest2);

    }
    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    private class QuizAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            textView.setVisibility(View.INVISIBLE);
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
            progressBar.setVisibility(View.INVISIBLE);
            if (jsonResponse != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    JSONArray jsonArray = jsonObject.getJSONArray("values");
                    for (int i = 1; i < jsonArray.length(); i++) {
                        JSONArray jsonArray1 = jsonArray.getJSONArray(i);
                        String topicName = jsonArray1.getString(1);
                        String topicId = jsonArray1.getString(0);
                        String quizName = jsonArray1.getString(2);
                        String quizId = jsonArray1.getString(3);
                        if (topicId.equals(selectedTopicId)) {
                            quizzes.add(new Quiz(topicId,topicName,quizName,quizId));
                        }
                    }
                    for (int i=5;i<quizzes.size();i+=5){
                        quizzes.add(i,new Quiz("Ad","Ad", "Ad", "Ad"));
                    }
                    quizAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                textView.setText("Error");
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
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (mInterstitialAd.isLoaded()){
            showInterstitial();
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }
}
