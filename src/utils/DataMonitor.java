package utils;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xxhedbet on 2017-03-24.
 */
public class DataMonitor {

    public static DataMonitor instance = new DataMonitor();

    private final int MAX_DATA_HISTORY = 10;
    private boolean active;

    private ConcurrentHashMap<String,DataKeeper> dataMap;

    private DataMonitor() {
        dataMap = new ConcurrentHashMap<>();
        active = false;
    }

    public Object getDataOnId(String id){
        if(!dataMap.containsKey(id)){
            System.err.println("Error : Unknown id requested in getAveragePerformanceOn(String id)");
            return null;
        }
        return dataMap.get(id).getLast();
    }

    public Object[] getDataListOnId(String id){
        if(!dataMap.containsKey(id)){
            System.err.println("Error : Unknown id requested in getAveragePerformanceOn(String id)");
            return null;
        }
        return dataMap.get(id).getListInOrder();
    }

    /**
     * Prints the percentages of all stopwatches to Stdout
     */
    public void printDataOnAll(){
        System.out.println("\n--- Data ---");
        for(Map.Entry<String,DataKeeper> entry : dataMap.entrySet()){
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
            for (Object o : dataMap.get(id).getListInOrder()) {
                if(o != null)
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

    public void register(String id,Callable callable) {
        if(!dataMap.containsKey(id))
            dataMap.put(id, new DataKeeper(id,callable));
    }

    public void updateDataOnId(String id){
        dataMap.get(id).updateData();
    }

    public void updateAllData() {
        for (DataKeeper dk : dataMap.values()){
            dk.updateData();
        }
    }

    /**
     *
     */
    public class DataKeeper {

        private String id;
        private Callable retrieveData;

        private Object[] dataStore;
        private int index = 0;

        public DataKeeper(String id, Callable callable) {
            this.id = id;
            this.retrieveData = callable;
            dataStore = new Object[MAX_DATA_HISTORY];
        }

        public Object getFirst(){
            return dataStore[index];
        }

        public void updateData(){
            try {
                dataStore[index] = retrieveData.call();
            } catch (Exception e) {
                e.printStackTrace();
            }

            index++;

            if(index >= MAX_DATA_HISTORY)
                index = 0;
        }

        public Object getLast(){
            if(index-1 < 0)
                return dataStore[MAX_DATA_HISTORY-1];

            return dataStore[index-1];
        }

        public Object[] getList() {
            return dataStore;
        }

        public Object[] getListInOrder() {
            Object[] sortedList = new Object[MAX_DATA_HISTORY];
            for(int i = 0; i < MAX_DATA_HISTORY; i++){
                sortedList[i] = dataStore[index];
                index--;
                if(index < 0 )
                    index = MAX_DATA_HISTORY - 1;
            }
            return sortedList;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DataKeeper)) return false;

            DataKeeper that = (DataKeeper) o;

            return id.equals(that.id);
        }
    }
}
