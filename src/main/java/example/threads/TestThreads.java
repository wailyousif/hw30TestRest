package example.threads;

import java.util.concurrent.TimeUnit;

/**
 * Created by jasonskipper on 3/7/17.
 */
public class TestThreads {

    public static void main(String[] stupidQuestion){
        Runnable runnable = getRunnableWithSleep();

        Thread thread = new Thread(runnable);
        thread.start();
    }

    private static Runnable getRunnableWithSleep() {
        return () -> {
                try {
                    String name = Thread.currentThread().getName();
                    System.out.println("Foo " + name);
                    TimeUnit.SECONDS.sleep(50);
                    System.out.println("Bar " + name);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
    }
}
