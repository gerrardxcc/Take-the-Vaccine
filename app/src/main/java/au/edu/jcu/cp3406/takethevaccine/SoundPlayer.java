package au.edu.jcu.cp3406.takethevaccine;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class SoundPlayer {

    private static SoundPool soundPool;
    private static int infect;
    private static int inject;
    private static int wear;

    public SoundPlayer(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // SoundPool is deprecated in API LEVEL 30 (R)
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(4)
                    .build();
        } else {
            soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        }
        infect = soundPool.load(context, R.raw.infect, 1);
        inject = soundPool.load(context, R.raw.inject, 1);
        wear = soundPool.load(context, R.raw.wear, 1);


    }

    public void playInjectSound() {
        soundPool.play(inject, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playInfectSound() {
        soundPool.play(infect, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playWearSound() {
        soundPool.play(wear, 1.0f, 1.0f, 1, 0, 1.0f);
    }


}
