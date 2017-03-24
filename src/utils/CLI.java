package utils;

import sun.misc.Perf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xxhedbet on 2017-03-23.
 */
public class CLI implements Runnable {

    public static CLI instance = new CLI();
    private final BufferedReader bufferedReader;
    private boolean active;
    private Timer timer; // Timer for scheduled tasks

    private CLI (){
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        timer = new Timer();
        active = true;
    }

    @Override
    public void run() {
        System.out.println("CLI Started");
        while(active){
            String command = "";
            try {
                command = bufferedReader.readLine();
                System.out.println("command : " + command);
                switch (command.toLowerCase()){
                    case "logger":
                        handleLogger(command);
                        break;
                    case "pm":
                        handlePerformanceMonitor(command);
                        break;
                    case "dm":
                        handleDataMonitor(command);
                        break;
                    case "help":
                        System.out.println("Welcome to Foxibar CLI\n" +
                                "Commands :\n" +
                                "Logger - \n" +
                                "PerformanceMontior - \n" +
                                "help - Display this helptext :>\n");
                        break;
                    default:
                        System.out.println("Unknown command");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Logger.instance.info(this,"hello");
    }

    private void handleDataMonitor(String command) throws IOException {
        System.out.println("[DataMonitor] >");
        command = bufferedReader.readLine();
        switch (command) {
            case "activate":
                DataMonitor.instance.setActive(!PerformanceMonitor.instance.isActive());
                break;
            case "toggle":
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        DataMonitor.instance.printDataOnAll();
                    }
                }, 0, 1000);
                break;
            case "print":
                command = bufferedReader.readLine();
                DataMonitor.instance.printDataOnId(command);
                break;
        }
    }

    public void handleLogger(String Command){
        System.out.println("[Logger] >");

    }


    public void handlePerformanceMonitor(String command) throws IOException {
        System.out.print("[Performance Monitor] >");
        command = bufferedReader.readLine();
        switch (command) {
            case "activate":
                PerformanceMonitor.instance.setActive(!PerformanceMonitor.instance.isActive());
                break;
            case "toggle":
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        PerformanceMonitor.instance.printPercentagesOnAll();
                    }
                },0,1000);
                break;
            case "print":
                PerformanceMonitor.instance.printPercentagesOnAll();
                break;
        }
    }
}
