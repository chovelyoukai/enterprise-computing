/*
    Course: CNT 4714 Summer 2021
    Assignment title: Project 1 - Multi-threaded programming in Java
    Date: June 6, 2021

    Class: PackageOperation
*/

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class PackageOperation
{
    final static int MAX = 10;
    
    public static void main(String[] args)
    {
        int routingStations;

        try
        {
            Scanner file = new Scanner(new File("config.txt"));
            ArrayList<Integer> config = new ArrayList<Integer>();
            ExecutorService executor = Executors.newFixedThreadPool(MAX);

            while (file.hasNext())
            {
                config.add(file.nextInt());
            }
            file.close();

            routingStations = config.get(0);

            Conveyor[] conveyors = new Conveyor[routingStations];

            for (int i = 0; i < routingStations; i++)
            {
                conveyors[i] = new Conveyor(i);
            }
            
            System.out.println("CNT 4714 - Project 1 - Summer 2021");
            System.out.println("Simulation beginning.\n");

            for (int i = 0; i < routingStations; i++)
            {
                try
                {
                    executor.execute(new RoutingStation(i, config.get(i + 1),
                        conveyors[i], conveyors[(i + routingStations - 1)
                        % routingStations]));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            
            executor.shutdown();
            while (!executor.isTerminated())
            {
            }
            
            System.out.println("Simulation Complete.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
