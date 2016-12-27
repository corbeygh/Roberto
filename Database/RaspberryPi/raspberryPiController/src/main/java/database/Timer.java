package database;

/**
 * Created by Calvin on 27/12/2016.
 */
public class Timer {

    private long timeStarted;
    private long timeFinished;
    private long timerLength;

    public Timer(int seconds){
        long timerLength = seconds*1000;
    }

    public void start(){
        timeStarted = System.currentTimeMillis();
        timeFinished = System.currentTimeMillis() +timerLength;
    }

    public boolean hasFinished(){
        if(System.currentTimeMillis() > timeFinished)
            return true;

        return false;
    }
}
