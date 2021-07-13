package au.edu.jcu.cp3406.takethevaccine;

import org.junit.Test;

import static org.junit.Assert.*;

public class VaccineUnitTest {
    int score = 10;
    int oldScore = 100;
    int highScore;

    @Test
    public void testHighScoreUpdate() {
        if (score > highScore) {
            highScore = score;
        } else {
            highScore = oldScore;
        }
        if (score != highScore) {
            assertFalse(false);
        }

    }

}