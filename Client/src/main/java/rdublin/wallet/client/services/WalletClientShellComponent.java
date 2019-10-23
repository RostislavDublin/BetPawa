package rdublin.wallet.client.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import rdublin.wallet.client.services.bulk.WalletBulkOperationsService;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@ShellComponent
public class WalletClientShellComponent {

    public static final Currency USD = Currency.getInstance("USD");
    public static final Currency EUR = Currency.getInstance("EUR");
    public static final Currency GBP = Currency.getInstance("GBP");

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletClientShellComponent.class);

    @Autowired
    TaskExecutor walletOperationsTaskExecutor;

    @Autowired
    WalletBulkOperationsService walletBulkOperationsService;

    private int users = 1;
    private int threadsPerUser = 1;
    private int roundsPerThread = 1;
    @Autowired
    private WalletClientService walletClientService;

    private static long getDuration(long start) {
        return System.currentTimeMillis() - start;
    }

    @ShellMethod(value = "Set the number of concurrent users emulated",
            group = "1. Wallet operations batch setup")
    public void users(@Min(1) int number) {
        this.users = number;
    }

    @ShellMethod(value = "Set the number of concurrent requests a user will make",
            group = "1. Wallet operations batch setup")
    public void threadsPerUser(@Min(1) int number) {
        this.threadsPerUser = number;
    }

    @ShellMethod(value = "Number of rounds each thread is executing",
            group = "1. Wallet operations batch setup")
    public void roundsPerThread(@Min(1) int number) {
        this.roundsPerThread = number;
    }

    @ShellMethod(value = "Configure Task Executor thread pool core and max size",
            group = "1. Wallet operations batch setup")
    public void setupExecutor(@Min(1) int coreSize, @Min(1) int maxSize) {
        Stream.of((ThreadPoolTaskExecutor) walletOperationsTaskExecutor).forEach(e -> {
            e.setCorePoolSize(coreSize);
            e.setMaxPoolSize(maxSize);
        });
    }

    @ShellMethod(value = "Show current configuration parameters",
            group = "1. Wallet operations batch setup")
    public String currentConfig() {
        int[] sizes = new int[2];
        Stream.of((ThreadPoolTaskExecutor) walletOperationsTaskExecutor).forEach(e -> {
            sizes[0] = e.getCorePoolSize();
            sizes[1] = e.getMaxPoolSize();
        });
        return String.format(
                "Executor:\n - core pool size: %d\n - max pool size: %d\n" +
                        "Batch:\n - users: %d\n - threads per user: %d\n - rounds per thread: %d",
                sizes[0], sizes[1], users, threadsPerUser, roundsPerThread);
    }

    @ShellMethod(value = "Run the workload after all parameters customization", group = "2. Wallet workload execution")
    public void runFromScratch(@Min(1) int users, @Min(1) int threadsPerUser, @Min(1) int roundsPerThread) {
        users(users);
        threadsPerUser(threadsPerUser);
        roundsPerThread(roundsPerThread);
        runPrepared();
    }

    @ShellMethod(value = "Run the batch after all parameters customization", group = "2. Wallet workload execution")
    public void runPrepared() {
        List<CompletableFuture<String>> roundSets
                = walletBulkOperationsService.runOperationsBatch(users, threadsPerUser, roundsPerThread);
        List<CompletableFuture<String>> newCompletedRoundSets;
        long start = System.currentTimeMillis(), startCycle, cycleDuration = 0, totalDuration = 0,
                totalRoundSets = roundSets.size(), completedRoundSets = 0;
        while (!roundSets.isEmpty()) {
            if (cycleDuration < 10000) {
                try {
                    Thread.sleep(10000 - cycleDuration);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
            }
            startCycle = System.currentTimeMillis();
            newCompletedRoundSets = new ArrayList<>();
            for (CompletableFuture<String> roundSet : roundSets) {
                if (roundSet.isDone()) {
                    newCompletedRoundSets.add(roundSet);
                }
            }
            completedRoundSets += newCompletedRoundSets.size();
            roundSets.removeAll(newCompletedRoundSets);
            totalDuration = getDuration(start);
            LOGGER.info("\nBATCH: {} of {} roundsets completed in {}ms, avg: {}/s",
                    completedRoundSets, totalRoundSets, getDuration(start),
                    (completedRoundSets * 1000 / totalDuration));
            cycleDuration = getDuration(startCycle);
        }
        totalDuration = getDuration(start);
        LOGGER.info("\nBATCH: all {} roundsets completed in {}ms, avg: {}/s",
                totalRoundSets, totalDuration,
                (totalRoundSets * 1000 / totalDuration));
    }

    @ShellMethod(value = "Run single withdraw for user and amount", group = "3. Single wallet operations")
    public void runWithdraw(@Min(1) int userId, @Min(1) int amount, @Size(min = 3, max = 3) String currencyCode) {
        walletClientService.withdraw(userId, amount, Currency.getInstance(currencyCode));
    }

    @ShellMethod(value = "Run single deposit for user and amount", group = "3. Single wallet operations")
    public void runDeposit(@Min(1) int userId, @Min(1) int amount, @Size(min = 3, max = 3) String currencyCode) {
        walletClientService.deposit(userId, amount, Currency.getInstance(currencyCode));
    }

    @ShellMethod(value = "Run single balance request for user", group = "3. Single wallet operations")
    public void runBalance(@Min(1) int userId) {
        walletClientService.balance(userId);
    }
}
