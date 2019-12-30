package server.timer;

public class TimerRunnable implements Runnable {

    private final AuctionTimer timer;

    public TimerRunnable(AuctionTimer timer) {
        this.timer = timer;
    }

    @Override
    public void run() {
        boolean isRun = true;
        while(isRun){
            isRun = timer.isTimeUp();
        }
    }
}
