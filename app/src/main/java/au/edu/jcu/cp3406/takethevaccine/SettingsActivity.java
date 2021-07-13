package au.edu.jcu.cp3406.takethevaccine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;


public class SettingsActivity extends AppCompatActivity {
    private TextView userInfo;
    private TweetAdapter adapter;
    private Button authenticate;
    private final Twitter twitter = TwitterFactory.getSingleton();
    private User user;
    private int highScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        userInfo = findViewById(R.id.user_info);
        ListView tweetList = findViewById(R.id.tweets);
        authenticate = findViewById(R.id.authorise);
        List<Tweet> tweets = new ArrayList<>();
        adapter = new TweetAdapter(this, tweets);
        tweetList.setAdapter(adapter);
        SharedPreferences sharedPreferences = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE); // Access High Score
        highScore = sharedPreferences.getInt("HIGH_SCORE", 0);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Background.run(() -> {
            final boolean status;
            final String text;
            if (isAuthorised()) {

                try {
                    twitter.updateStatus("Come and take the vaccine !!! My current high score is: " + highScore + " !!!" + " #takethevaccine");
                } catch (TwitterException ignored) {

                }

                text = user.getScreenName();
                status = false;
            } else {
                text = "unknown";
                userInfo.setText(getString(R.string.unknown));
                status = true;
            }

            runOnUiThread(() -> {
                userInfo.setText(text);
                authenticate.setEnabled(status);
                adapter.notifyDataSetChanged();
            });
        });
    }

    public void authorise(View view) {
        Intent intent = new Intent(this, Authenticate.class);
        startActivity(intent);
    }

    private boolean isAuthorised() {
        try {
            user = twitter.verifyCredentials();
            Log.i("SettingsActivity", "verified");
            return true;
        } catch (Exception e) {
            Log.i("SettingsActivity", "not verified");
            return false;
        }
    }

}
