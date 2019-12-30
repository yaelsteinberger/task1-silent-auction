package server.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class AuctionTimer {
    private final static Logger logger = LoggerFactory.getLogger(AuctionTimer.class);

    private final Long startTimeSeconds;
    private final Long totalTimeSeconds;

    public AuctionTimer(Long totalTime, TimeUnit timeUnit){
        startTimeSeconds = System.currentTimeMillis()/1000;

        Long time = 0L;
        switch(timeUnit.name()){
            case "HOURS":{
                time = totalTime*3600;
                break;
            }
            case "MINUTES":{
                time = totalTime*60;
                break;
            }
            default:{
                time = totalTime;
            }
        }

        totalTimeSeconds = time;
    }

    public Float getTimeLeft(TimeUnit timeUnit) {
        Long currentTime = System.currentTimeMillis()/1000;
        Long timeLeftSeconds = totalTimeSeconds - (currentTime - startTimeSeconds);

//        logger.debug("Time Left: {}", timeLeftSeconds);

        switch(timeUnit.name()){
            case "HOURS":{
                return timeLeftSeconds/3600F;
            }
            case "MINUTES":{
                return timeLeftSeconds/60F;
            }
            default:{
                return timeLeftSeconds/1F;
            }
        }
    }

    public boolean isTimeUp(){
        return (getTimeLeft(TimeUnit.SECONDS) <= 0);
    }
}
