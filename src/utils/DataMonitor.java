package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by xxhedbet on 2017-03-24.
 */
public class DataMonitor {

    public static DataMonitor instance = new DataMonitor();

    private final int MAX_DATA_HISTORY = 10;
    private boolean active;

    private ConcurrentHashMap<String,ConcurrentLinkedDeque> dataMap;

    private DataMonitor() {
        dataMap = new ConcurrentHashMap<>();
        active = true;
    }


    /**
     * starts and stops monitoring a given id
     * @param id the id to be monitored
     */
    public void MonitorData(String id, Object data){
        if(!active)
            return;

        if(!dataMap.containsKey(id))
            dataMap.put(id, new ConcurrentLinkedDeque());

        dataMap.get(id).add(data);

        if(dataMap.get(id).size() > MAX_DATA_HISTORY){
            dataMap.get(id).remove();
        }
    }

    public Object getDataOnId(String id){
        if(!dataMap.containsKey(id)){
            System.err.println("Error : Unknown id requested in getAveragePerformanceOn(String id)");
            return 0;
        }
        return dataMap.get(id).getLast();
    }

    /**
     * Prints the percentages of all stopwatches to Stdout
     */
    public void printDataOnAll(){
        System.out.println("\n--- Data ---");
        for(Map.Entry<String,ConcurrentLinkedDeque> entry : dataMap.entrySet()){
            System.out.println(String.format("id : %1$-20s value : %2$-5s", entry.getKey(), entry.getValue().getLast().toString()));
        }
        System.out.println("---      ---");
    }
    /**
     * Prints the percentages of all stopwatches to Stdout
     */
    public void printDataOnId(String id){
        if(dataMap.containsKey(id)){
            System.out.println("\n--- Data ---");
            System.out.print(String.format("id : %1$-20s value : ",id));
            for (Object o : dataMap.get(id)) {
                System.out.print(String.format("%1$-5s",o.toString()));
            }
            System.out.println();
            System.out.println("---       ---");
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
