package utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by xxhedbet on 2017-03-24.
 */
public class DataMonitorTest {

    private Integer variable;

    @Before
    public void setUp() throws Exception {
        variable = 0;
    }

    @After
    public void tearDown() throws Exception {
        DataMonitor.instance.clearAllData();
    }

    @Test
    public void getDataOnId() throws Exception {
        DataMonitor.instance.registerData("myInt", () -> variable);
       for(; variable < 100; variable++ ){
            DataMonitor.instance.updateDataOnId("myInt");
            assertEquals(variable,DataMonitor.instance.getDataOnId("myInt"));
        }
    }

    @Test
    public void getDataListOnId() throws Exception {
        DataMonitor.instance.registerData("myInt", () -> variable);
        for(; variable < 10; variable++ )
            DataMonitor.instance.updateDataOnId("myInt");

        for(; variable < 100; variable++ ){
            DataMonitor.instance.updateDataOnId("myInt");
            int i = variable;
            Object[] dataList = DataMonitor.instance.getDataListOnId("myInt");
            for (Object aDataList : dataList) {
                assertEquals(i, aDataList);
                i--;
            }
        }
    }

    @Test
    public void registerData() throws Exception {
        for(int id = 0; id < 100;id++)
            DataMonitor.instance.registerData(String.valueOf(id), () -> variable);

        DataMonitor.instance.updateAllData();

        for(int id = 0; id < 100;id++)
            assertEquals(variable,DataMonitor.instance.getDataOnId(String.valueOf(id)));
    }

    @Test
    public void updateDataOnId() throws Exception {
        for(int id = 0; id < 100;id++)
            DataMonitor.instance.registerData(String.valueOf(id), () -> variable);

        for(int id = 0; id < 100;id++){
            variable = id;
            DataMonitor.instance.updateDataOnId(String.valueOf(id));
            assertEquals(variable,DataMonitor.instance.getDataOnId(String.valueOf(id)));
        }
    }

    @Test
    public void updateAllData() throws Exception {
        for(int id = 0; id < 100;id++)
            DataMonitor.instance.registerData(String.valueOf(id), () -> variable);

        for(int i = 0; i < 100; i++){
            variable = i;
            DataMonitor.instance.updateAllData();

            for(int id = 0; id < 100;id++)
                assertEquals(variable,DataMonitor.instance.getDataOnId(String.valueOf(id)));
        }
    }
}