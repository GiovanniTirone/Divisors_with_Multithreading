import java.util.List;
import java.util.concurrent.ForkJoinPool;



public class Ex_03_DivisorsWithJavaForkJoinPool {

    public static void main(String[] args) {

        ForkJoinPool forkJoinPool = new ForkJoinPool();

        Ex_02_DivisorsWithThreadPool dwtp = new Ex_02_DivisorsWithThreadPool(1,100000,0,1000);
        Runnable [] tasks = dwtp.createTasks();

        long startTime = System.currentTimeMillis();

        for(int i=0;i<tasks.length;i++){
            forkJoinPool.execute(tasks[i]);
        }

        long elapsedTime = System.currentTimeMillis() - startTime;

        List<Integer> finalList = dwtp.getFinalIntAndDivNum__Map();

        System.out.println("Found integer: " + finalList.get(0));
        System.out.println("Number of divisors: " + finalList.get(1));
        System.out.println("\nTotal elapsed time:  " + (elapsedTime/1000.0) + " seconds.\n");

    }

}
