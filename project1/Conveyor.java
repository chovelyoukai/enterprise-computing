/*
    Course: CNT 4714 Summer 2021
    Assignment title: Project 1 - Multi-threaded programming in Java
    Date: June 6, 2021

    Class: Conveyor
*/

import java.util.concurrent.locks.ReentrantLock;

public class Conveyor
{
    private ReentrantLock lock = new ReentrantLock();
    private int id;

    public Conveyor(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public boolean lock()
    {
        return lock.tryLock();
    }

    public void unlock()
    {
        lock.unlock();
    }

}
