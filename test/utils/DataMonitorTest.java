package utils;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by xxhedbet on 2017-03-24.
 */
public class DataMonitorTest {

    public Integer variable;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void monitorData() throws Exception {
        variable = 0;
        DataMonitor.instance.register("myInt", () -> variable);
        DataMonitor.instance.updateDataOnId("myInt");
        DataMonitor.instance.printDataOnId("myInt");
        variable = 100;
        DataMonitor.instance.updateDataOnId("myInt");
        DataMonitor.instance.printDataOnId("myInt");
        variable = 300;
        DataMonitor.instance.updateDataOnId("myInt");
        DataMonitor.instance.printDataOnId("myInt");

    }

    @Test
    public void getDataOnIdn() throws Exception {

    }
}