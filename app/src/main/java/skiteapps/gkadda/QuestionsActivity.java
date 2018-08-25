package skiteapps.gkadda;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

public class QuestionsActivity extends AppCompatActivity {

    int currentQuestion;
    boolean openIt = false;
    private View wrongAnswerLayout;
    private View rightAnswerLayout;
    private View explainationLayout;
    private View explaintLayoutTwo;
    private TextView numberNow;
    private TextView question;
    private TextView rightAnswer;
    private TextView explainRight;
    private TextView explain;
    private Button checkAnswer;
    private RadioButton optionA;
    private RadioButton optionB;
    private RadioButton optionC;
    private RadioButton optionD;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private View parentView;
    private TextView textView;
    private ProgressBar progressBar;
    private Intent intent;
    private String selectedQuizId;
    private AdView mAdView;
    private ArrayList<Question> questions;
    InterstitialAd mInterstitialAd;
    AdRequest adRequest2;
    private static final String questionUrl = "https://sheets.googleapis.com/v4/spreadsheets/1gymAaNCyzFRtTDyd2ztLYWO_s5DXRxQR4UQmLNukiFo/values/MCQ!A:N?key=AIzaSyAHRy3U0-UXRMxVJ09ubzT66KCmPeKkC98";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        setTitle("Questions");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        currentQuestion = 0;
        ActionBar actionBar = getSupportActionBar();
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.action_bar, null);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Questions");
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setCustomView(v);
        textView = (TextView) findViewById(R.id.question_no_internet);
        rightAnswer = (TextView) findViewById(R.id.right_answer);
        checkAnswer = (Button) findViewById(R.id.check_answer);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        wrongAnswerLayout = findViewById(R.id.wrong_answer_layout);
        explaintLayoutTwo = findViewById(R.id.explain_layout_two);
        rightAnswer = (TextView) findViewById(R.id.right_answer);
        numberNow = (TextView) findViewById(R.id.number_now);
        explain = (TextView) findViewById(R.id.explain);
        explainationLayout = findViewById(R.id.explaination_layout);
        rightAnswerLayout = findViewById(R.id.right_answer_layout);
        explainRight = (TextView) findViewById(R.id.explain_right);
        checkAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    radioButton = (RadioButton) findViewById(selectedId);
                    if (radioButton.getText().equals(questions.get(currentQuestion).getAnswer())) {
                        parentView.setVisibility(View.INVISIBLE);
                        checkAnswer.setVisibility(View.INVISIBLE);
                        rightAnswerLayout.setVisibility(View.VISIBLE);
                        if (TextUtils.isEmpty(questions.get(currentQuestion).getExplain())) {
                            explainationLayout.setVisibility(View.INVISIBLE);
                        } else {
                            explainationLayout.setVisibility(View.VISIBLE);
                            explainRight.setText("Explaination : " + questions.get(currentQuestion).getExplain());
                        }
                        optionA.setChecked(false);
                        optionB.setChecked(false);
                        optionC.setChecked(false);
                        optionD.setChecked(false);
                        radioGroup.clearCheck();
                    } else {
                        parentView.setVisibility(View.INVISIBLE);
                        checkAnswer.setVisibility(View.INVISIBLE);
                        wrongAnswerLayout.setVisibility(View.VISIBLE);
                        rightAnswer.setText("Right Answer : " + questions.get(currentQuestion).getAnswer());
                        if (TextUtils.isEmpty(questions.get(currentQuestion).getExplain())) {
                            explaintLayoutTwo.setVisibility(View.INVISIBLE);
                        } else {
                            explaintLayoutTwo.setVisibility(View.VISIBLE);
                            explain.setText("Explaination : " + questions.get(currentQuestion).getExplain());
                        }
                        optionA.setChecked(false);
                        optionB.setChecked(false);
                        optionC.setChecked(false);
                        optionD.setChecked(false);
                        radioGroup.clearCheck();
                    }
                    openIt = true;
                    invalidateOptionsMenu();
                } else {
                    Toast.makeText(QuestionsActivity.this, "Choose Answer", Toast.LENGTH_SHORT).show();
                }

            }
        });
        question = (TextView) findViewById(R.id.question);
        optionA = (RadioButton) findViewById(R.id.choice_A);
        optionB = (RadioButton) findViewById(R.id.choice_B);
        optionC = (RadioButton) findViewById(R.id.choice_C);
        optionD = (RadioButton) findViewById(R.id.choice_D);
        progressBar = (ProgressBar) findViewById(R.id.question_progress_bar);
        parentView = findViewById(R.id.question_layout);
        intent = getIntent();
        selectedQuizId = intent.getStringExtra("selectedQuizId");
        questions = new ArrayList<>();
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new QuestionAsyncTask().execute(questionUrl);
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
                parentView.setVisibility(View.VISIBLE);
                checkAnswer.setVisibility(View.VISIBLE);
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
                        String optionA = jsonArray1.getString(7);
                        String optionB = jsonArray1.getString(8);
                        String optionC = jsonArray1.getString(9);
                        String optionD = jsonArray1.getString(10);
                        String answer = jsonArray1.getString(11);
                        String explain = jsonArray1.getString(12);
                        String correctAnswer = jsonArray1.getString(13);
                        if (quizId.equals(selectedQuizId)) {
                            questions.add(new Question(topicId, topicName, quizName, quizId, questionId, questionNumber, question, optionA, optionB
                                    , optionC, optionD, answer, explain, correctAnswer));
                        }
                    }
                    question.setText(questions.get(0).getQuestion());
                    optionA.setText(questions.get(0).getOptionA());
                    optionB.setText(questions.get(0).getOptionB());
                    optionC.setText(questions.get(0).getOptionC());
                    optionD.setText(questions.get(0).getOptionD());
                    numberNow.setText(currentQuestion + 1 + "/" + questions.size());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                textView.setText("Error");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        invalidateOptionsMenu();
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.next) {
            if (currentQuestion < questions.size() - 1) {
                openIt = false;
                invalidateOptionsMenu();
                currentQuestion++;
                numberNow.setText(currentQuestion + 1 + "/" + questions.size());
                wrongAnswerLayout.setVisibility(View.INVISIBLE);
                rightAnswerLayout.setVisibility(View.INVISIBLE);
                parentView.setVisibility(View.INVISIBLE);
                checkAnswer.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                question.setText(questions.get(currentQuestion).getQuestion());
                optionA.setText(questions.get(currentQuestion).getOptionA());
                optionB.setText(questions.get(currentQuestion).getOptionB());
                optionC.setText(questions.get(currentQuestion).getOptionC());
                optionD.setText(questions.get(currentQuestion).getOptionD());
                parentView.setVisibility(View.VISIBLE);
                checkAnswer.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                if (currentQuestion == questions.size() - 1) {
                    invalidateOptionsMenu();
                }
                return true;
            } else {
                finish();
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (openIt) {
            menu.getItem(0).setEnabled(true);
        } else {
            menu.getItem(0).setEnabled(false);
        }
        if (currentQuestion == questions.size() - 1) {
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
