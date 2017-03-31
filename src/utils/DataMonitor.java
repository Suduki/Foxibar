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

    /**
     * Retrieves the most recent data change on a given id
     * @param id the id of the data
     * @return the most recent data, if it exists
     */
    public Object getDataOnId(String id){
        if(!dataMap.containsKey(id)){
            log.trace(this,"Error : Unknown id requested in getAveragePerformanceOn(String id)");
            return null;
        }
        return dataMap.get(id).getLast();
    }

    /**
     * Retrieves a list of data history on a given id.
     * @param id the id of the data
     * @return A list containing data change history if it exists.
     */
    public Object[] getDataListOnId(String id){
        if(!dataMap.containsKey(id)){
            log.trace(this,"Error : Unknown id requested in getAveragePerformanceOn(String id)");
            return null;
        }
        return dataMap.get(id).getListInOrder();
    }

    /**
     * Prints the percentages of all stopwatches to Stdout
     */
    public void printDataOnAll(){
        log.info(this,"\n--- Data ---");
        for(Map.Entry<String,DataKeeper> entry : dataMap.entrySet()){
            log.info(this,String.format("id : %1$-20s value : %2$-5s", entry.getKey(), entry.getValue().getLast().toString()));
        }
        log.info(this,"---      ---");
    }

    /**
     * Prints the percentages of all stopwatches to Stdout
     */
    public void printDataOnId(String id){
        if(dataMap.containsKey(id)){
            log.info(this,"\n--- Data ---");
            log.info(this,String.format("id : %1$-20s value : ",id));
            for (Object o : dataMap.get(id).getListInOrder()) {
                if(o != null)
                    log.info(this,String.format("%1$-5s",o.toString()));
            }
            log.info(this,"\n---       ---");
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     *  Register the data with a string and a callable getter for that data.
     *  OBS!, The data won't update until you run updateData
     * @param id       The id to register the data to
     * @param callable Callable getter function for that data.
     */
    public void registerData(String id, Callable callable) {
        if(!dataMap.containsKey(id))
            dataMap.put(id, new DataKeeper(id,callable));
    }

    /**
     * Updates the data of the given Id
     * @param id id of the registred data
     */
    public void updateDataOnId(String id){
        if(dataMap.get(id) != null)
            dataMap.get(id).updateData();
        else
            log.trace(this,"Tried to fetch unregistred data");
    }

    /**
     * iterates through all dataKeepers and forces them to update their data.
     */
    public void updateAllData() {
        for (DataKeeper dk : dataMap.values()){
            dk.updateData();
        }
    }


    public void clearData(String id){
        if(dataMap.remove("id") == null)
            log.trace(this,"failed to clear data on id : " + id);
    }


    public void clearAllData(){
        dataMap.clear();
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

        /**
         * inserts new data into the dataStore by
         * calling the get function for this data.
         */
        public void updateData(){
            try {
                dataStore[index] = retrieveData.call();
            } catch (Exception e) {
                // Yes it is an exception,the motherclass of all exceptions :<
                // Why java, why u make me do dis :<
                e.printStackTrace();
                log.trace(this,"Tried to update data, but something is wrong with callable");
            }

            index++;

            if(index >= MAX_DATA_HISTORY)
                index = 0;
        }

        /**
         * retrieves the most recently inserted data
         * @return data object
         */
        public Object getLast(){
            if(index-1 < 0)
                return dataStore[MAX_DATA_HISTORY-1];

            return dataStore[index-1];
        }

        public Object[] getList() {
            return dataStore;
        }

        /**
         * retrieves the list in order of which they were inserted
         * @return a list
         */
        public Object[] getListInOrder() {
            Object[] sortedList = new Object[MAX_DATA_HISTORY];
            for(int i = 0; i < MAX_DATA_HISTORY; i++){
                index--;
                if(index < 0 )
                    index = MAX_DATA_HISTORY - 1;

                sortedList[i] = dataStore[index];
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
