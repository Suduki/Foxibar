package utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by xxhedbet on 2017-03-24.
 */
public class DataMonitorTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void monitorData() throws Exception {
        DataMonitor.instance.MonitorData("data",100);
        DataMonitor.instance.MonitorData("data2",200);
    }

    @Test
    public void getDataOnIdn() throws Exception {
        DataMonitor.instance.MonitorData("data",100);
        DataMonitor.instance.MonitorData("data",200);
        DataMonitor.instance.MonitorData("data",300);
        DataMonitor.instance.MonitorData("data",400);
        DataMonitor.instance.MonitorData("data2",200);
        DataMonitor.instance.MonitorData("data3","hello");
        assertEquals(400,DataMonitor.instance.getDataOnId("data"));
        assertEquals(200,DataMonitor.instance.getDataOnId("data2"));
        assertEquals("hello",DataMonitor.instance.getDataOnId("data3"));
        DataMonitor.instance.printDataOnAll();
        DataMonitor.instance.printDataOnId("data");
    }
}