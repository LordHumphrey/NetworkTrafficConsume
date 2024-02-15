package utils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tao wong
 * @description TODO
 * @date 2024-02-14 16:52
 */
public class ThreadPoolUtils {
    private static volatile ThreadPoolExecutor executor;
    private static volatile ScheduledExecutorService singleThreadScheduledExecutor;

    private ThreadPoolUtils() {
        // private constructor to prevent instantiation
    }

    public static ExecutorService getExecutorService() {
        if (executor == null) {
            synchronized (ThreadPoolUtils.class) {
                if (executor == null) {
                    int corePoolSize = Config.DOWNLOAD_THREADS.getIntValue();
                    int maximumPoolSize = 2 * corePoolSize;
                    long keepAliveTime = 60L;
                    TimeUnit unit = TimeUnit.SECONDS;
                    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(1024);
                    RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
                    executor = new ThreadPoolExecutor(
                            corePoolSize,
                            maximumPoolSize,
                            keepAliveTime,
                            unit,
                            workQueue,
                            new CustomeThreadFactory("DownloadFilePool"),
                            handler);
                }
            }
        }
        return executor;
    }

    public static ScheduledExecutorService getSingleThreadScheduledExecutor() {
        if (singleThreadScheduledExecutor == null) {
            synchronized (ThreadPoolUtils.class) {
                if (singleThreadScheduledExecutor == null) {
                    singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor(
                            new CustomeThreadFactory("outputDownloadInfoPool"));
                }
            }
        }
        return singleThreadScheduledExecutor;
    }
}

class CustomeThreadFactory implements ThreadFactory {
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String threadName;

    public CustomeThreadFactory(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, threadName + "-thread-" + threadNumber.getAndIncrement());
    }
}
