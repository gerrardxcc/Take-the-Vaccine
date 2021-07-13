package au.edu.jcu.cp3406.takethevaccine;

public class Background {
    static void run(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
