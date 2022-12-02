import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Ex_04_DivisorsWithExecutorService {

    private int numberOfTasks;

    private int globalMin;

    private int globalMax;

    public Ex_04_DivisorsWithExecutorService(int numberOfTasks, int globalMin, int globalMax){
        this.numberOfTasks = numberOfTasks;
        this.globalMin = globalMin;
        this.globalMax = globalMax;
    }

    private Callable<List<Integer>> createTask__callable (int min, int max){
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
            List<Integer> result = new ArrayList<>();
            result.add(integerWithMaxDivisors);
            result.add(maxNumberOfDivisors);
            return result;
        };
    }


    private int countDivisor (int number) {
        int counter = 0;
        for(int i=1; i<=number; i++){
            if(number%i==0) counter++;
        }
        return counter;
    }


    public Callable<List<Integer>>[] createTasks () {
        Callable<List<Integer>> [] tasks = new Callable[numberOfTasks];
        int step = (globalMax-globalMin)/numberOfTasks;
        for(int i= 0; i<numberOfTasks; i++){
            if(i<numberOfTasks-1){
                tasks[i] = createTask__callable(globalMin + i*step,globalMin + i*step + step);
            }else{
                tasks[i] = createTask__callable(globalMin + i+step, globalMax);
            }
        }
        return tasks;
    }


    public static void main(String[] args) {

        int processors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(processors);

        Ex_04_DivisorsWithExecutorService dwes = new Ex_04_DivisorsWithExecutorService(1000,1,100000);
        Callable [] tasks = dwes.createTasks();
        List<Future<List<Integer>>> results = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for(int i=0;i<tasks.length;i++){
            Future<List<Integer>> result = executor.submit(tasks[i]);
            results.add(result);
        }

        long elapsedTime = System.currentTimeMillis() - startTime;

        List<Integer> finalList = new ArrayList<>();
        finalList.add(0);
        finalList.add(0);

        for ( Future<List<Integer>> res : results) {
            try {
                 List<Integer> tempResList = res.get();  // Waits for task to complete!
                if(tempResList.get(1)>finalList.get(1)){
                    finalList.set(0,tempResList.get(0));
                    finalList.set(1,tempResList.get(1));
                }
            }
            catch (Exception e) { // Should not occur in this program.
            }
        }

        System.out.println("Found integer: " + finalList.get(0));
        System.out.println("Number of divisors: " + finalList.get(1));
        System.out.println("\nTotal elapsed time:  " + (elapsedTime/1000.0) + " seconds.\n");

        executor.shutdown();

    }

}
