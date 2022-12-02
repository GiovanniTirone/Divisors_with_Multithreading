import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class Ex_05_DivisorsWithParallelStream {

    public static void main(String[] args) {


        Ex_04_DivisorsWithExecutorService dwes = new Ex_04_DivisorsWithExecutorService(1000, 1, 100000);

        List<Callable> tasks = new ArrayList<>(Arrays.asList(dwes.createTasks()));

        long startTime = System.currentTimeMillis();

         Optional<List<Integer>> result =
                 tasks.parallelStream()
                         .map(task -> {
                            try {
                                return (List<Integer>)task.call();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                         })
                         .max(Comparator.comparingInt(resList -> resList.get(1)));

         List<Integer> finalRes = result.get();

        long elapsedTime = System.currentTimeMillis() - startTime;

        System.out.println("Found integer: " + finalRes.get(0));
        System.out.println("Number of divisors: " + finalRes.get(1));
        System.out.println("\nTotal elapsed time:  " + (elapsedTime/1000.0) + " seconds.\n");


    }
}
