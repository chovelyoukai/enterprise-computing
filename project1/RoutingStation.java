/*
    Course: CNT 4714 Summer 2021
    Assignment title: Project 1 - Multi-threaded programming in Java
    Date: June 6, 2021

    Class: RoutingStation 
*/

import java.util.Random;

public class RoutingStation implements Runnable
{
    private Conveyor input, output;
    private int id, workload;
    private boolean inputLocked, outputLocked;
    
    final int MAX_SLEEP = 100;

    public RoutingStation(int id, int workload, Conveyor input,
        Conveyor output)
    {
        this.id = id;
        this.workload = workload;
        this.input = input;
        this.output = output;

        System.out.println(">> S" + id + ": input connection set to conveyor C" + input.getId());
        System.out.println(">> S" + id + ": output connection set to conveyor C" + output.getId());
        System.out.println(">> S" + id + ": workload set to " + workload);
        System.out.println();
    }
    
    public boolean getInputLock()
    {
       if (input.lock())
       {
           System.out.println("   S" + id + ": Lock acquired on input conveyor C" + input.getId());
           return true;
       }
       else
       {
           System.out.println("X> S" + id + ": Failed to lock input conveyor C" + input.getId());
           return false;
       }
    }
    
    public boolean getOutputLock()
    {
       if (output.lock())
       {
           System.out.println("   S" + id + ": Lock acquired on output conveyor C" + output.getId());
           return true;
       }
       else
       {
           System.out.println("X> S" + id + ": Failed to lock output conveyor C" + output.getId() +
               ", releasing input conveyor C" + input.getId());
           return false;
       }
    }
    
    public void releaseInputLock()
    {
        input.unlock();
        System.out.println("   S" + id + ": Releasing input conveyor C" + input.getId());
    }
    
    public void releaseOutputLock()
    {
        output.unlock();
        System.out.println("   S" + id + ": Releasing output conveyor C" + output.getId());
    }

    public void run()
    {
        Random rand = new Random();
        while (workload > 0)
        {
            inputLocked = false;
            outputLocked = false;
            while (!inputLocked && !outputLocked)
            {
                // try to get input lock
                while (!inputLocked)
                {
                    inputLocked = getInputLock();
                    if (!inputLocked)
                    {
                        try
                        {
                            Thread.sleep(rand.nextInt(MAX_SLEEP));
                        }
                        catch (Exception e)
                        {
                        }
                    }
                }
                // try to get output lock
                outputLocked = getOutputLock();
                if (!outputLocked)
                {
                    releaseInputLock();
                    inputLocked = false;
                    try
                    {
                        Thread.sleep(rand.nextInt(MAX_SLEEP));
                    }
                    catch (Exception e)
                    {
                    }
                }
            }
            doWork();
            releaseInputLock();
            releaseOutputLock();
        }
        System.out.println("\n** S" + id + ": Finished, going idle.\n");
        return;
    }
    
    private void doWork()
    {
        Random rand = new Random();
        System.out.println(">> S" + id + ": Accepting packaged on conveyor C" + input.getId());
        System.out.println(">> S" + id + ": Outputting packaged on conveyor C" + output.getId());
        try
        {
            Thread.sleep(rand.nextInt(MAX_SLEEP));
        }
        catch (Exception e)
        {
        }
        
        workload--;
        System.out.println(">> S" + id + ": Remaining workload is: " + workload);
    }
}
