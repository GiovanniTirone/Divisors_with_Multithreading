public class Ex_01_DivisorsWithThreads {


    private int finalInteger;

    private int finalNumberOfDivisors;

    private int globalMin;

    private int globalMax;

    private int numberOfThreads;


    public Ex_01_DivisorsWithThreads(int globalMin, int globalMax, int numberOfThreads){
        finalInteger=0;
        finalNumberOfDivisors = 0;
        this.globalMin = globalMin;
        this.globalMax = globalMax;
        this.numberOfThreads = numberOfThreads;
    }

    public int getFinalInteger() {
        return finalInteger;
    }

    public void setFinalInteger(int finalInteger) {
        this.finalInteger = finalInteger;
    }

    public int getFinalNumberOfDivisors() {
        return finalNumberOfDivisors;
    }

    public void setFinalNumberOfDivisors(int finalNumberOfDivisors) {
        this.finalNumberOfDivisors = finalNumberOfDivisors;
    }

    private synchronized void changeFinalInteger (int newValue, int newNumberOfDivisors) {
        if(newNumberOfDivisors > finalNumberOfDivisors){
            finalNumberOfDivisors = newNumberOfDivisors;
            finalInteger = newValue;
        }
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
            changeFinalInteger(integerWithMaxDivisors, maxNumberOfDivisors);
        };
    }

    public Thread[] CreateAllThreads () {
        int step = (globalMax-globalMin)/numberOfThreads;
        Thread[] threads = new Thread[numberOfThreads];
        for(int i=0; i<numberOfThreads; i++){
            if(i<numberOfThreads-1) {
                threads[i] = new Thread(findDivisorsInInterval_Runnable(globalMin + i*step,globalMin + i*step + step));
            }else{
                threads[i] = new Thread(findDivisorsInInterval_Runnable(globalMin + i+step, globalMax));
            }
        }
        return threads;
    }

    private int countDivisor (int number) {
        int counter = 0;
        for(int i=1; i<=number; i++){
            if(number%i==0) counter++;
        }
        return counter;
    }

    public static void main(String[] args) {
            int numberOfThreads = 3;
            Ex_01_DivisorsWithThreads dwt = new Ex_01_DivisorsWithThreads(1,100000,numberOfThreads);
            Thread [] worker = dwt.CreateAllThreads();

            long startTime = System.currentTimeMillis();

            for (int i = 0; i < numberOfThreads; i++)
                worker[i].start();

            for (int i = 0; i < numberOfThreads; i++) {
                while (worker[i].isAlive()) {
                    try {
                        worker[i].join();
                    }
                    catch (InterruptedException e) {
                    }
                }
            }

            long elapsedTime = System.currentTimeMillis() - startTime;

            System.out.println("Found integer: " + dwt.getFinalInteger());
            System.out.println("Number of divisors: " + dwt.getFinalNumberOfDivisors());
            System.out.println("\nTotal elapsed time:  " + (elapsedTime/1000.0) + " seconds.\n");

    }

}
