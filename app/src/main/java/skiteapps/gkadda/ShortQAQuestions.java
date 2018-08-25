package skiteapps.gkadda;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class ShortQAQuestions extends AppCompatActivity {

    private boolean checked;
    private int currentQuestion;
    private MenuItem menuItem;
    private View parentView;
    private TextView question;
    private TextView answer;
    private Button showAnswer;
    private CardView answerParent;
    private ProgressBar progressBar;
    private TextView textView;
    private Intent intent;
    private String selectedQuizId;
    private QuestionsAdapter questionsAdapter;
    private RecyclerView recyclerView;
    private AdView mAdView;
    private ArrayList<QuestionShortQA> questions;
    InterstitialAd mInterstitialAd;
    AdRequest adRequest2;
    private static final String questionsUrl = "https://sheets.googleapis.com/v4/spreadsheets/1gymAaNCyzFRtTDyd2ztLYWO_s5DXRxQR4UQmLNukiFo/values/SHORT%20Q&A!A1:H?key=AIzaSyAHRy3U0-UXRMxVJ09ubzT66KCmPeKkC98";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_qaquestions);
        setTitle("Questions");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        intent = getIntent();
        textView = (TextView) findViewById(R.id.no_internet);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        parentView = findViewById(R.id.question_layout);
        question = (TextView) findViewById(R.id.question);
        answer = (TextView) findViewById(R.id.answer);
        showAnswer = (Button) findViewById(R.id.show_answer);
        answerParent = (CardView) findViewById(R.id.answer_parent);
        questions = new ArrayList<>();
        selectedQuizId = intent.getStringExtra("selectedQuizId");
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        questionsAdapter = new QuestionsAdapter(questions, new QuestionsAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {
                checked = true;
                currentQuestion = position;
                menuItem.setVisible(true);
                if (currentQuestion >= questions.size()-1){
                    menuItem.setTitle("End");
                }
                showAnswer.setVisibility(View.VISIBLE);
                question.setText(questions.get(position).getQuestion());
                answer.setText(questions.get(position).getAnswer());
                recyclerView.setVisibility(View.INVISIBLE);
                parentView.setVisibility(View.VISIBLE);
            }
        });
        showAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerParent.setVisibility(View.VISIBLE);
                showAnswer.setVisibility(View.INVISIBLE);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(questionsAdapter);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new QuestionAsyncTask().execute(questionsUrl);
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

    private class QuestionAsyncTask extends AsyncTask<String, Void, String> {

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
                        String quizName = jsonArray1.getString(2);
                        String quizId = jsonArray1.getString(3);
                        String questionId = jsonArray1.getString(4);
                        String questionNumber = jsonArray1.getString(5);
                        String question = jsonArray1.getString(6);
                        String answer = jsonArray1.getString(7);
                        if (quizId.equals(selectedQuizId)) {
                            questions.add(new QuestionShortQA(topicId, topicName, quizName, quizId, questionId, questionNumber, question, answer));
                        }
                    }
                    questionsAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                textView.setText("Error");
            }
        }
    }

    @Override
    public void onBackPressed() {
        textView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        if (checked) {
            parentView.setVisibility(View.INVISIBLE);
            menuItem.setVisible(true);
            answerParent.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
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
                    parentView.setVisibility(View.INVISIBLE);
                    answerParent.setVisibility(View.INVISIBLE);
                    menuItem.setVisible(false);
                    recyclerView.setVisibility(View.VISIBLE);
                    checked = false;
                } else {
                    return (super.onOptionsItemSelected(item));
                }
            case R.id.next:
                if (currentQuestion < questions.size() - 1){
                    answerParent.setVisibility(View.INVISIBLE);
                    showAnswer.setVisibility(View.VISIBLE);
                    currentQuestion++;
                    question.setText(questions.get(currentQuestion).getQuestion());
                    answer.setText(questions.get(currentQuestion).getAnswer());
                    if (currentQuestion >= questions.size()-1){
                        item.setTitle("End");
                    }
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
        menuItem = menu.getItem(0);
        menuItem.setVisible(false);
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
