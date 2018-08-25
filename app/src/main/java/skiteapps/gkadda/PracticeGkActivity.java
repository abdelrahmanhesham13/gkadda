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

public class PracticeGkActivity extends AppCompatActivity implements RecyclerAdapter.OnItemClick {

    boolean checked = false;
    private String selectedSubjectId;
    private RecyclerView recyclerView;
    private RecyclerAdapter subjectAdapter;
    private TopicsAdapter topicsAdapter;
    private ArrayList<Subject> subjects;
    private ArrayList<Topic> topics;
    private ProgressBar progressBar;
    private TextView textView;
    private AdView mAdView;
    InterstitialAd mInterstitialAd;
    AdRequest adRequest2;
    private static final String subjectUrl = "https://sheets.googleapis.com/v4/spreadsheets/1gymAaNCyzFRtTDyd2ztLYWO_s5DXRxQR4UQmLNukiFo/values/SUBJECT%20NAME%20MCQ!A:B?key=AIzaSyAHRy3U0-UXRMxVJ09ubzT66KCmPeKkC98";
    private static final String topicURL = "https://sheets.googleapis.com/v4/spreadsheets/1gymAaNCyzFRtTDyd2ztLYWO_s5DXRxQR4UQmLNukiFo/values/MCQ%20TOPIC%20NAME!A:E?key=AIzaSyAHRy3U0-UXRMxVJ09ubzT66KCmPeKkC98";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_gk);
        setTitle("GK ADDA");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        textView = (TextView) findViewById(R.id.no_internet);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        subjects = new ArrayList<>();
        topics = new ArrayList<>();
        subjectAdapter = new RecyclerAdapter(subjects, this);
        topicsAdapter = new TopicsAdapter(topics, new TopicsAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {
                Intent intent = new Intent(PracticeGkActivity.this, QuizActivity.class);
                intent.putExtra("selectedTopicId", topics.get(position).getTopicId());
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(subjectAdapter);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new SubjectAsyncTask().execute(subjectUrl);
        } else {
            textView.setText("No internet connection");
            textView.setVisibility(View.VISIBLE);
        }
        mInterstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial2));

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

    @Override
    public void setOnItemClick(int position) {
        Subject selectedSubject = subjects.get(position);
        topics.clear();
        selectedSubjectId = String.valueOf(selectedSubject.getSubjectId());
        new TopicAsyncTask().execute(topicURL);
        checked = true;
    }

    private class SubjectAsyncTask extends AsyncTask<String, Void, String> {

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
                        String subjectName = jsonArray1.getString(1);
                        int subjectId = Integer.parseInt(jsonArray1.getString(0));
                        subjects.add(new Subject(subjectId, subjectName));
                    }
                    for (int i = 5; i < subjects.size(); i += 5) {
                        subjects.add(i,new Subject(1, "ad"));
                    }
                    subjectAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                textView.setText("Error");
            }
        }
    }

    private class TopicAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            recyclerView.setAdapter(topicsAdapter);
            textView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
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
                        String subjectName = jsonArray1.getString(1);
                        String subjectId = jsonArray1.getString(0);
                        String topicId = jsonArray1.getString(2);
                        String topicName = jsonArray1.getString(3);
                        String topicStatue = jsonArray1.getString(4);
                        if (topicStatue.equals("ACTIVE") && subjectId.equals(selectedSubjectId)) {
                            topics.add(new Topic(Integer.parseInt(subjectId), subjectName, topicId, topicName, topicStatue));
                        }
                    }
                    for (int i=5;i<topics.size();i+=5){
                        topics.add(i,new Topic(1, "Ad", "Ad", "Ad", "Ad"));
                    }
                    topicsAdapter.notifyDataSetChanged();
                    if (topics.isEmpty()) {
                        textView.setText("No Topics");
                        textView.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                textView.setText("Error");
                textView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        textView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        if (checked) {
            recyclerView.setAdapter(subjectAdapter);
            checked = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                textView.setVisibility(View.INVISIBLE);
                if (checked) {
                    recyclerView.setAdapter(subjectAdapter);
                    checked = false;
                } else {
                    return (super.onOptionsItemSelected(item));
                }
                return (true);
        }

        return (super.onOptionsItemSelected(item));
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
