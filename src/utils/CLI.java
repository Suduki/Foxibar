package utils;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import javax.swing.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.IntBuffer;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import static org.lwjgl.glfw.GLFW.glfwCreateWindow;

/**
 * Created by xxhedbet on 2017-03-23.
 */
public class CLI extends JFrame implements Runnable {

    public static CLI instance = new CLI();
    private final BufferedReader bufferedReader;
    private boolean active;
    private Timer timer; // Timer for scheduled tasks

    // The window handle
    private long window;

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
                System.out.print("[CLI] >");
                command = bufferedReader.readLine();

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
                    case "h":
                    case "help":
                        System.out.println("Welcome to Foxibar CLI\n" +
                                "Commands :\n" +
                                "logger - log\n" +
                                "pm - PerformanceMontior\n" +
                                "dm - DataMonitor\n" +
                                "help - Display this helptext :>\n");
                        break;
                    default:
                        System.out.println("Unknown command");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleDataMonitor(String command) throws IOException {
        while (true) {
            System.out.print("(" + (DataMonitor.instance.isActive() ? "ON" : "OFF") + ")" + "[DataMonitor] > ");
            command = bufferedReader.readLine();
            switch (command.toLowerCase()) {
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
                    System.out.print("id > ");
                    command = bufferedReader.readLine();
                    DataMonitor.instance.printDataOnId(command);
                    break;
                case "printall":
                    DataMonitor.instance.printDataOnAll();
                    break;

                case "h":
                case "help":
                    System.out.println("handleDataMonitor\n" +
                            "Commands :\n" +
                            "activate - activate the monitoring\n" +
                            "toggle - Toggle printout on all ids\n" +
                            "print - Print data on ID\n" +
                            "help - Display this helptext :>\n");
                    break;
                case "q":
                case "exit":
                    return;
            }
        }
    }

    public void handleLogger(String command) throws IOException {
        while (true) {
            System.out.print("[log] > ");
            command = bufferedReader.readLine();
            switch (command.toLowerCase()) {
                case "activate":
                    System.out.print("activate flag > ");
                    command = bufferedReader.readLine();

                    break;
                case "h":
                case "help":
                    System.out.println("log\n" +
                            "Commands :\n" +
                            "activate - activate id on logger\n" +
                            "help - Display this helptext :>\n");
                    break;
                case "q":
                case "exit":
                    return;
            }
        }
    }


    public void handlePerformanceMonitor(String command) throws IOException {
        while (true) {
            System.out.print("(" + (PerformanceMonitor.instance.isActive() ? "ON" : "OFF") + ")" + "[Performance Monitor] > ");
            command = bufferedReader.readLine();
            switch (command.toLowerCase()) {
                case "activate":
                    PerformanceMonitor.instance.setActive(!PerformanceMonitor.instance.isActive());
                    break;
                case "toggle":
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            PerformanceMonitor.instance.printPercentagesOnAll();
                        }
                    }, 0, 1000);
                    break;
                case "print":
                    System.out.print("id > ");
                    command = bufferedReader.readLine();
                    PerformanceMonitor.instance.printId(command);
                    break;
                case "printall":
                    PerformanceMonitor.instance.printPercentagesOnAll();
                    break;
                case "h":
                case "help":
                    System.out.println("handlePerformanceMonitor\n" +
                            "Commands :\n" +
                            "activate - activate the monitoring\n" +
                            "toggle - Toggle printout on all ids\n" +
                            "print - Print data on ID\n" +
                            "printAll  - Print data on all ID\n" +
                            "help - Display this helptext :>\n");
                    break;
                case "q":
                case "exit":
                    return;
            }
        }
    }
}
