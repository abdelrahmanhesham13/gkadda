package skiteapps.gkadda;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

public class PointsToRememberPointActivity extends AppCompatActivity {

    private int currentQuestion;
    private View parentView;
    private TextView question;
    private TextView question2;
    private TextView question3;
    private TextView question4;
    private TextView question5;
    private ProgressBar progressBar;
    private TextView textView;
    private Intent intent;
    private String selectedQuizId;
    private AdView mAdView;
    private ArrayList<QuestionShortQA> questions;
    InterstitialAd mInterstitialAd;
    AdRequest adRequest2;
    private static final String pointsUrl = "https://sheets.googleapis.com/v4/spreadsheets/1gymAaNCyzFRtTDyd2ztLYWO_s5DXRxQR4UQmLNukiFo/values/POINTS%20TO%20REMEMBER!A:E?key=AIzaSyAHRy3U0-UXRMxVJ09ubzT66KCmPeKkC98";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_to_remember_point);
        setTitle("Points");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        intent = getIntent();
        currentQuestion = 0;
        textView = (TextView) findViewById(R.id.no_internet);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        parentView = findViewById(R.id.question_layout);
        question = (TextView) findViewById(R.id.question1);
        question2 = (TextView) findViewById(R.id.question2);
        question3 = (TextView) findViewById(R.id.question3);
        question4 = (TextView) findViewById(R.id.question4);
        question5 = (TextView) findViewById(R.id.question5);
        questions = new ArrayList<>();
        selectedQuizId = intent.getStringExtra("selectedTopicId");
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new PointAsyncTask().execute(pointsUrl);
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

    private class PointAsyncTask extends AsyncTask<String, Void, String> {

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
                        String topicId = jsonArray1.getString(0);
                        String topicName = jsonArray1.getString(1);
                        String pointName = jsonArray1.getString(3);
                        String pointId = jsonArray1.getString(2);
                        String point = jsonArray1.getString(4);
                        if (topicId.equals(selectedQuizId)) {
                            questions.add(new QuestionShortQA(topicId, topicName, pointName, pointId, "Empty", "Empty", point, "Empty"));
                        }
                    }
                    question.setText(questions.get(0).getQuestion());
                    question2.setText(questions.get(1).getQuestion());
                    question3.setText(questions.get(2).getQuestion());
                    question4.setText(questions.get(3).getQuestion());
                    question5.setText(questions.get(4).getQuestion());
                    parentView.setVisibility(View.VISIBLE);
                    currentQuestion+=5;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                textView.setText("Error");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next:
                if (currentQuestion <= questions.size() - 1){
                    if (currentQuestion <= questions.size() - 1){
                    question.setText(questions.get(currentQuestion).getQuestion());}
                    currentQuestion++;
                    if (currentQuestion <= questions.size() - 1){
                    question2.setText(questions.get(currentQuestion).getQuestion());}
                    currentQuestion++;
                    if (currentQuestion <= questions.size() - 1){
                    question3.setText(questions.get(currentQuestion).getQuestion());}
                    currentQuestion++;
                    if (currentQuestion <= questions.size() - 1){
                    question4.setText(questions.get(currentQuestion).getQuestion());}
                    currentQuestion++;
                    if (currentQuestion <= questions.size() - 1){
                    question5.setText(questions.get(currentQuestion).getQuestion());}
                    currentQuestion++;
                    invalidateOptionsMenu();
                } else {
                    finish();
                }
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        invalidateOptionsMenu();
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (currentQuestion > questions.size() - 5 && questions.size() != 0) {
            menu.getItem(0).setTitle("End");
        }
        return true;
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
