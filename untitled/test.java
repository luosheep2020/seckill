import java.util.concurrent.*;

public class test {

    public static void main(String[] args) throws InterruptedException {
        try (
                ExecutorService threadPool = new ThreadPoolExecutor(
                        3,
                        100,
                        10,
                        TimeUnit.SECONDS,
                        new ArrayBlockingQueue<>(5),
                        Executors.defaultThreadFactory(),
                        new ThreadPoolExecutor.AbortPolicy()
                )) {

            for (int i = 0; i < 1000; i++) {
                int finalI = i;
                threadPool.submit(()->{
                    System.out.println(STR."\{Thread.currentThread().getName()}---->\{finalI}");
                });
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        Thread.yield();
    }
}
