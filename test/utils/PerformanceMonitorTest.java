package utils;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by xxhedbet on 2017-03-22.
 */
public class PerformanceMonitorTest {

    @Test
    public void toggleMonitoring() throws Exception {

        for(int i = 0; i < 10; i++){
            PerformanceMonitor.instance.ToggleMonitoring("hello");
            Thread.sleep(500);
            PerformanceMonitor.instance.ToggleMonitoring("hello");
        }
        PerformanceMonitor.instance.ToggleMonitoring("hello");
        Thread.sleep(2000);
        PerformanceMonitor.instance.ToggleMonitoring("hello");


        PerformanceMonitor.instance.ToggleMonitoring("test2");
        Thread.sleep(2000);
        PerformanceMonitor.instance.ToggleMonitoring("test2");
    }

    @Test
    public void getAveragePerformanceOn() throws Exception {
        assertTrue(Math.abs(800-PerformanceMonitor.instance.getAveragePerformanceOn("hello")) < 50);
        assertTrue(Math.abs(2000-PerformanceMonitor.instance.getAveragePerformanceOn("test2")) < 50);

        for(Map.Entry<String,Long> i : PerformanceMonitor.instance.getAveragePerformanceOnAll().entrySet()){
            System.out.println("id : " + i.getKey() +", value : " + i.getValue());
        }
    }

}