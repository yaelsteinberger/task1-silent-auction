import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import server.AuctionTimer;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




public class JustTesing {
    ObjectMapper mapper = new ObjectMapper();


    @Test
    public void timer1() throws Exception {
        ExecutorService threadsTimer = Executors.newFixedThreadPool(1);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.err.println("=> 2");
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, 5000L);

        Thread.sleep(10000);
    }


    @Test
    public void timer2() throws Exception {
        Timer timer = new Timer("MyTimer");
        timer.scheduleAtFixedRate(new AuctionTimer(30L), 30, 1000);
//        timer.schedule(new AuctionTimer(30L), 500, 1000);

        Thread.sleep(10000);
    }

}
