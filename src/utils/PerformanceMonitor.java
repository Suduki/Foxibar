package utils;

import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * PerformanceMonitor
 * Keeps track of the time spent on each monitored task.
 */
public class PerformanceMonitor {

    //Singleton
    public static PerformanceMonitor instance = new PerformanceMonitor();

    private final int MAX_PERFORMANCE_HISTORY = 5;

    private ConcurrentHashMap<String,TimeKeeper> TimeKeepMap;

    private boolean active;

    private PerformanceMonitor() {
        TimeKeepMap = new ConcurrentHashMap<>();
        active = false;
    }

    /**
     * starts and stops monitoring a given id
     * @param id the id to be monitored
     */
    public void ToggleMonitoring(String id){
        if(!active)
            return;

        if(!TimeKeepMap.containsKey(id)){
            TimeKeepMap.put(id,new TimeKeeper());
        }
        TimeKeepMap.get(id).toggleStopWatch();
    }

    public long getAveragePerformanceOn(String id){
        if(!TimeKeepMap.containsKey(id)){
            System.err.println("Error : Unknown id requested in getAveragePerformanceOn(String id)");
            return 0;
        }
        return TimeKeepMap.get(id).getAverageTimeTable();
    }

    public HashMap<String,Long> getAveragePerformanceOnAll(){
        HashMap<String,Long> hm = new HashMap<>();
        for (Map.Entry<String, TimeKeeper> entry : TimeKeepMap.entrySet())
            hm.put(entry.getKey(),entry.getValue().getAverageTimeTable());
        return hm;
    }

    /**
     *
     * @return
     */
    public HashMap<String,Double> getPercentagePerformanceOnAll(){
        HashMap<String,Long> hm = getAveragePerformanceOnAll();

        long sum = 0;
        for (Long value : hm.values())
            sum += value;

        HashMap<String,Double> percentage = new HashMap<>();
        for (Map.Entry<String, Long> entry : hm.entrySet()){
            percentage.put(entry.getKey(),((double)entry.getValue()/(double)sum)*100d);
        }

        return percentage;
    }

    /**
     * Prints the percentages of all stopwatches to Stdout
     */
    public void printPercentagesOnAll(){
        System.out.println("\n--- Percentage ---");
        HashMap<String,Long> hm = getAveragePerformanceOnAll();
        for(Map.Entry<String,Double> i : PerformanceMonitor.instance.getPercentagePerformanceOnAll().entrySet()){
            System.out.println(String.format("id : %1$-20s value : %2$-5.2f time : %3$-20s", i.getKey(), i.getValue(),hm.get(i.getKey())));
        }
        System.out.println("---            ---");
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * TimeKeeper
     * Keeps track of the time spent on a task and the tasks history.
     */
    public class TimeKeeper{

        private Queue<Long> timeTable;
        private long stopwatch;

        public TimeKeeper() {
            timeTable = new LinkedBlockingQueue<>();
            stopwatch = 0;
        }

        /**
         * Controls when to start and stop the time timings.
         */
        public void toggleStopWatch(){
            if (stopwatch == 0){
                // stopwatch is off
                stopwatch = System.nanoTime();
            } else {
                // Stopwatch is on
                timeTable.add(System.nanoTime() - stopwatch);

                if(timeTable.size() > MAX_PERFORMANCE_HISTORY){
                    timeTable.remove();
                }
                stopwatch = 0;
            }
        }

        /**
         * @return the average value of all the runs
         */
        public long getAverageTimeTable() {
            if(timeTable.size() == 0)
                return 0; // Table has no values atm.

            long averageTime = 0;
            for (Long time : timeTable) {
                averageTime += time;
            }

            return averageTime/timeTable.size();
        }
    }
}
