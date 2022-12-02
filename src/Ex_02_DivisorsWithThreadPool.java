import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Ex_02_DivisorsWithThreadPool {


    private int globalMin;

    private int globalMax;

    private ConcurrentLinkedQueue <Runnable> taskQueue;

    private LinkedBlockingDeque<Result> resultsQueue;

    private int numberOfWorkingThreads;

    private int numberOfTasks;

    public Ex_02_DivisorsWithThreadPool(int globalMin, int globalMax, int numberOfWorkingThreads, int numberOfTasks ){
        taskQueue = new ConcurrentLinkedQueue<>();
        resultsQueue = new LinkedBlockingDeque<>();
        this.numberOfWorkingThreads = numberOfWorkingThreads;
        this.numberOfTasks = numberOfTasks;
        this.globalMin = globalMin;
        this.globalMax = globalMax;
    }


    public ConcurrentLinkedQueue<Runnable> getTaskQueue() {
        return taskQueue;
    }

    private class WorkerThread extends Thread {

        public void run() {
            try {
                while (true) {
                    Runnable task = taskQueue.poll();
                    if (task == null) break;
                    task.run();
                  //  Thread.sleep(1);
                }
            } finally {
                System.out.println("The thread "+ getId() + " is terminated");;
            }
        }
    }

    public Thread [] createWorkingThreads () {
        Thread[] threads = new Thread[numberOfWorkingThreads];
        for(int i=0; i<numberOfWorkingThreads; i++){
            threads[i] = new WorkerThread();
        }
        return threads;
    }

    private Runnable findDivisorsInInterval_Runnable (int min, int max){
        return ()->{
            int integerWithMaxDivisors = 1;
            int maxNumberOfDivisors = 1;
            int newNumberOfDivisors;
            for(int i=min; i<max; i++){
                newNumberOfDivisors = countDivisor(i);
                if(newNumberOfDivisors > maxNumberOfDivisors){
                    maxNumberOfDivisors = newNumberOfDivisors;
                    integerWithMaxDivisors = i;
                }
            }
            resultsQueue.add(new Result(integerWithMaxDivisors,maxNumberOfDivisors));
        };
    }


    private int countDivisor (int number) {
        int counter = 0;
        for(int i=1; i<=number; i++){
            if(number%i==0) counter++;
        }
        return counter;
    }


    public Runnable[] createTasks () {
        Runnable [] tasks = new Runnable[numberOfTasks];
        int step = (globalMax-globalMin)/numberOfTasks;
        for(int i= 0; i<numberOfTasks; i++){
            if(i<numberOfTasks-1){
                tasks[i] = findDivisorsInInterval_Runnable(globalMin + i*step,globalMin + i*step + step);
            }else{
                tasks[i] = findDivisorsInInterval_Runnable(globalMin + i+step, globalMax);
            }
        }
        return tasks;
    }

    private class Result {
        private int foundInteger;
        private int foundNumberOfDivisors;

        public Result(int foundInteger, int foundNumberOfDivisors) {
            this.foundInteger = foundInteger;
            this.foundNumberOfDivisors = foundNumberOfDivisors;
        }

        public int getFoundInteger() {
            return foundInteger;
        }

        public int getFoundNumberOfDivisors() {
            return foundNumberOfDivisors;
        }
    }

    public Result getFinalResult () {
        Result finalResult = null;
        int finalNumberOfDivisors = 0;
        for (int i = 0; i < numberOfTasks; i++) {
            try {
                Result result = resultsQueue.take();
                if (result.getFoundNumberOfDivisors() > finalNumberOfDivisors) {
                    finalResult = result;
                }
            }
            catch (InterruptedException e) {
            }
        }
        return finalResult;
    }

    public List<Integer> getFinalIntAndDivNum__Map () {
        List<Integer> resultList = new ArrayList<>();
        Result result = getFinalResult();
        resultList.add(result.foundInteger);
        resultList.add(result.foundNumberOfDivisors);
        return resultList;
    }




    public static void main(String[] args) throws InterruptedException {

        int numberOfThreads = 10;
        Ex_02_DivisorsWithThreadPool dwtp = new Ex_02_DivisorsWithThreadPool(1,100000,numberOfThreads,1000);
        Runnable [] tasks = dwtp.createTasks();
        Thread [] workingThreads = dwtp.createWorkingThreads();

        ConcurrentLinkedQueue queue = dwtp.getTaskQueue();

       // Thread addTasksToQueue = new Thread(()->{
           for(int i=0;i<tasks.length;i++){
               queue.add(tasks[i]);
           }
        //});

        //addTasksToQueue.start();

        System.out.println("Number of tasks:" + queue.size());;

        long startTime = System.currentTimeMillis();

        for (int i=0; i<workingThreads.length; i++){
            workingThreads[i].start();
        }

        for (int i=0; i<workingThreads.length; i++){
            workingThreads[i].join();
        }

        long elapsedTime = System.currentTimeMillis() - startTime;

        Result finalResult = dwtp.getFinalResult();

        System.out.println("Found integer: " + finalResult.getFoundInteger());
        System.out.println("Number of divisors: " + finalResult.getFoundNumberOfDivisors());
        System.out.println("\nTotal elapsed time:  " + (elapsedTime/1000.0) + " seconds.\n");
    }

}
