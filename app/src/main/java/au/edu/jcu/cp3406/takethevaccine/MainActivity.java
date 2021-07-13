package au.edu.jcu.cp3406.takethevaccine;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    //Components
    private TextView scoreLabel, startLabel;
    private ImageView human, vaccine, virus, mask;

    //Size
    private int frameHeight;
    private int humanHeight;
    private int screenWidth;

    //Position
    private float humanY;
    private float vaccineX, vaccineY;
    private float virusX, virusY;
    private float maskX, maskY;

    //Speed
    private int humanSpeed, vaccineSpeed, virusSpeed, maskSpeed;

    //Score
    private int score;

    //Timer
    private Timer timer = new Timer();
    private final Handler handler = new Handler();

    //Status
    private boolean action_flg = false;
    private boolean start_flg = false;

    //SoundPlayer
    private SoundPlayer soundPlayer;
    private MediaPlayer mediaPlayer;

    //Vibration
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPlayer = new SoundPlayer(this);

        scoreLabel = findViewById(R.id.scoreLabel);
        startLabel = findViewById(R.id.startLabel);
        human = findViewById(R.id.human);
        vaccine = findViewById(R.id.vaccine);
        virus = findViewById(R.id.virus);
        mask = findViewById(R.id.mask);


        //Background music
        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.virus_music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();


        // Screen size
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenWidth = size.x;
        int screenHeight = size.y;

        //Pixel 4 XL width: 1440 height: 3040
        //Speed human:51, vaccine:24, mask:12, virus:16
        humanSpeed = Math.round(screenHeight / 80.0f); // 3040 / 60 = 50.66....=> 51
        vaccineSpeed = Math.round(screenWidth / 80.0f); // 1440 / 60 = 24
        maskSpeed = Math.round(screenWidth / 66.0f); // 1440 / 36 = 40
        virusSpeed = Math.round(screenWidth / 65.0f); // 1440 / 45 = 32


        //Initial position
        vaccine.setX(-80.0f);
        vaccine.setY(-80.0f);
        mask.setX(-80.0f);
        mask.setY(-80.0f);
        virus.setX(-80.0f);
        virus.setY(-80.0f);

        scoreLabel.setText(getString(R.string.score, score));

        //Vibration
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    }


    public void changePos() {

        hitCheck();

        //Vaccine
        vaccineX -= vaccineSpeed;
        if (vaccineX < 0) {
            vaccineX = screenWidth + 20;
            vaccineY = (float) Math.floor(Math.random() * (frameHeight - vaccine.getHeight()));
        }
        vaccine.setX(vaccineX);
        vaccine.setY(vaccineY);

        //Virus
        virusX -= virusSpeed;
        if (virusX < 0) {
            virusX = screenWidth + 10;
            virusY = (float) Math.floor(Math.random() * (frameHeight - virus.getHeight()));
        }
        virus.setX(virusX);
        virus.setY(virusY);

        //Mask
        maskX -= maskSpeed;
        if (maskX < 0) {
            maskX = screenWidth + 5000;
            maskY = (float) Math.floor(Math.random() * (frameHeight - mask.getHeight()));
        }
        mask.setX(maskX);
        mask.setY(maskY);


        //Human
        if (action_flg) {
            //Touching
            humanY -= humanSpeed;
        } else {
            //Releasing
            humanY += humanSpeed;
        }
        if (humanY < 0) humanY = 0;
        if (humanY > frameHeight - humanHeight) humanY = frameHeight - humanHeight;

        human.setY(humanY);

        scoreLabel.setText(getString(R.string.score, score));
    }

    public void hitCheck() {

        //Vaccine
        float vaccineCenterX = vaccineX + vaccine.getWidth() / 2.0f;
        float vaccineCenterY = vaccineY + vaccine.getWidth() / 2.0f;

        if (0 <= vaccineCenterX && vaccineCenterX <= humanHeight
                && humanY <= vaccineCenterY && vaccineCenterY <= humanY + humanHeight) {
            vaccineX = -100.0f;
            score += 10;
            soundPlayer.playInjectSound();
        }

        //Mask
        float maskCenterX = maskX + mask.getWidth() / 2.0f;
        float maskCenterY = maskY + mask.getWidth() / 2.0f;

        if (0 <= maskCenterX && maskCenterX <= humanHeight
                && humanY <= maskCenterY && maskCenterY <= humanY + humanHeight) {
            maskX = -100.0f;
            score += 5;
            soundPlayer.playWearSound();
        }

        //Virus
        float virusCenterX = virusX + virus.getWidth() / 2.0f;
        float virusCenterY = virusY + virus.getWidth() / 2.0f;

        if (0 <= virusCenterX && virusCenterX <= humanHeight
                && humanY <= virusCenterY && virusCenterY <= humanY + humanHeight) {

            soundPlayer.playInfectSound();

            //Game Over!!
            if (timer != null) {
                timer.cancel();
                timer = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            } else {
                vibrator.cancel();
            }


            //Show ResultActivity
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!start_flg) {
            start_flg = true;

            //FrameHeight
            FrameLayout frameLayout = findViewById(R.id.frame);
            frameHeight = frameLayout.getHeight();

            //Human
            humanY = human.getY();
            humanHeight = human.getHeight();

            startLabel.setVisibility(View.GONE);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(() -> changePos());
                }
            }, 0, 20);

        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                action_flg = true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                action_flg = false;
            }
        }
        return super.onTouchEvent(event);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    @Override
    public void onBackPressed() {


    }
}