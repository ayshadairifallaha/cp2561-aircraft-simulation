import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SupervisedRunner implements Runnable {
    private static final Logger logger = Logger.getLogger(SupervisedRunner.class.getName());

    private final String workerName;
    private final Runnable workerTask;
    private final AtomicBoolean runningFlag;

    private final long initialBackoffMs = 100;
    private final long maxBackoffMs = 5_000;
    private final int restartBudgetCount = 5;
    private final long restartBudgetWindowMs = 30_000;
    private final long successWindowMs = 10_000;
    private final long monitorPollMs = 200;

    public SupervisedRunner(String workerName, Runnable workerTask, AtomicBoolean runningFlag) {
        this.workerName = workerName;
        this.workerTask = workerTask;
        this.runningFlag = runningFlag;
    }

    @Override
    public void run() {
        long backoff = initialBackoffMs;
        Deque<Long> failureTimes = new ArrayDeque<>();

        boolean exceededBudget = false;

        System.out.println("[Supervisor] Starting supervised worker: " + workerName);

        while (runningFlag.get() && !exceededBudget) {
            AtomicReference<Throwable> failureRef = new AtomicReference<>();
            Thread workerThread = new Thread(() -> {
                try {
                    workerTask.run();
                } catch (Throwable t) {
                    // Record failure for the supervisor to handle
                    failureRef.set(t);
                }
            }, workerName + "-thread");
            workerThread.setDaemon(true);
            workerThread.start();

            long startedAt = System.currentTimeMillis();
            boolean successWindowReached = false;

            // Monitor the running worker thread
            while (runningFlag.get()) {
                Throwable ex = failureRef.get();
                long now = System.currentTimeMillis();

                if (ex != null) {
                    // Failure occurred
                    failureTimes.addLast(now);
                    // purge old failure timestamps outside the budget window
                    while (!failureTimes.isEmpty() && now - failureTimes.peekFirst() > restartBudgetWindowMs) {
                        failureTimes.removeFirst();
                    }

                    // Log failure with stack trace and current backoff time
                    logger.log(Level.SEVERE, String.format("Worker '%s' failed; backoff=%dms", workerName, backoff), ex);
                    System.err.println(String.format("[Supervisor] Worker '%s' crashed: %s", workerName, ex.getMessage()));
                    ex.printStackTrace();

                    if (failureTimes.size() >= restartBudgetCount) {
                        logger.severe(String.format("worker '%s' exceeded restart budget", workerName));
                        System.err.println(String.format("[Supervisor] Worker '%s' exceeded restart budget (%d failures in %d seconds); will not be restarted", 
                            workerName, restartBudgetCount, restartBudgetWindowMs/1000));
                        exceededBudget = true;
                    } else {
                        System.out.println(String.format("[Supervisor] Restarting worker '%s' in %dms (failure %d/%d)", 
                            workerName, backoff, failureTimes.size(), restartBudgetCount));
                        // Sleep for the current backoff before restarting
                        try {
                            Thread.sleep(backoff);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            exceededBudget = true;
                        }
                        // increase backoff with cap
                        backoff = Math.min(backoff * 2, maxBackoffMs);
                    }

                    // ensure the worker thread isn't left running
                    if (workerThread.isAlive()) {
                        workerThread.interrupt();
                        try {
                            workerThread.join(500);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    break; // break monitor loop to either stop (exceeded) or restart worker
                } else {
                    // No failure observed
                    if (!successWindowReached && now - startedAt >= successWindowMs) {
                        // Worker ran 10 consecutive seconds without failure -> reset backoff and failure history
                        backoff = initialBackoffMs;
                        failureTimes.clear();
                        successWindowReached = true;
                        logger.info(String.format("Worker '%s' ran %dms without failure; resetting backoff and failure count.", workerName, successWindowMs));
                        System.out.println(String.format("[Supervisor] Worker '%s' stable for 10 seconds, reset backoff to %dms", workerName, initialBackoffMs));
                    }

                    // If worker thread terminated normally without throwing an exception, break to restart it
                    if (!workerThread.isAlive()) {
                        break;
                    }

                    // Poll a bit before checking again
                    try {
                        Thread.sleep(monitorPollMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            } // end monitor loop

            // If runningFlag turned false, ensure worker thread is stopped and exit
            if (!runningFlag.get()) {
                if (workerThread.isAlive()) {
                    workerThread.interrupt();
                    try {
                        workerThread.join(500);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
                break;
            }

            if (exceededBudget) {
                // stop retrying
                break;
            }

            // otherwise loop continues and a new worker thread will be started
        } // end supervise loop
        
        System.out.println("[Supervisor] Stopped supervising worker: " + workerName);
    }
}