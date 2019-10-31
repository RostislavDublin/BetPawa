package rdublin.wallet.client.services.bulk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import rdublin.wallet.client.services.WalletClientService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

import static rdublin.utils.MetricsUtils.getDuration;

@Service
@Scope("singleton")
public class WalletBulkOperationsServiceImpl implements WalletBulkOperationsService {

    public static final int INTERMEDIATE_REPORT_PERIOD = 5000;
    private static final Logger LOGGER = LoggerFactory.getLogger(WalletBulkOperationsServiceImpl.class);
    private static final WalletBulkOperationsRound[] ROUNDS = new WalletBulkOperationsRound[]{
            new WalletBulkOperationsRoundA(), new WalletBulkOperationsRoundB(), new WalletBulkOperationsRoundC()
    };
    Random random = new Random();

    AtomicInteger completedRoundSets = new AtomicInteger(0);
    AtomicInteger completedOperations = new AtomicInteger(0);

    @Autowired
    private ApplicationContext context;
    @Autowired
    private WalletClientService walletClientService;

    @Override
    public CompletableFuture<String> performOperationsBatch(int users, int threadsPerUser, int roundsPerThread) {
        completedRoundSets.set(0);
        completedOperations.set(0);
        List<CompletableFuture<OperationRoundSetResult>> roundSets =
                runOperationsBatch(users, threadsPerUser, roundsPerThread);

        long startCycle, start = startCycle = System.currentTimeMillis(), totalDuration = 0,
                totalRoundSets = roundSets.size();
        while (true) {
            synchronized (completedRoundSets) {
                while (completedRoundSets.longValue() < totalRoundSets &&
                        getDuration(startCycle) < INTERMEDIATE_REPORT_PERIOD) {
                    try {
                        completedRoundSets.wait(INTERMEDIATE_REPORT_PERIOD * 2);
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                }
            }
            if (completedRoundSets.longValue() >= totalRoundSets) {
                break;
            } else if (getDuration(startCycle) >= INTERMEDIATE_REPORT_PERIOD) {
                totalDuration = getDuration(start);
                LOGGER.info("\nBATCH: {} operations in {} of {} roundsets completed in {}ms, avg: {}ops/s",
                        completedOperations, completedRoundSets, totalRoundSets, getDuration(start),
                        (completedOperations.intValue() * 1000 / totalDuration));
                startCycle = System.currentTimeMillis();
            }
        }
        totalDuration = getDuration(start);
        LOGGER.info("\nBATCH: all {} operations in {} roundsets completed in {}ms, avg: {}ops/s",
                completedOperations, totalRoundSets, totalDuration,
                (completedOperations.intValue() * 1000 / totalDuration));
        return CompletableFuture.completedFuture("Batch Completed");
    }

    public List<CompletableFuture<OperationRoundSetResult>> runOperationsBatch(int users, int threadsPerUser,
                                                                               int roundsPerThread) {
        long start = System.currentTimeMillis();
        LOGGER.info("\nBATCH: Starting for {} users * {} threads * {} rounds", users, threadsPerUser, roundsPerThread);
        //because @Async doesn't work with 'this'
        WalletBulkOperationsService self = context.getBean(WalletBulkOperationsService.class);
        List<CompletableFuture<OperationRoundSetResult>> roundSets = new ArrayList<>();
        for (int i = 0; i < threadsPerUser; i++) {
            for (int j = 1; j <= users; j++) {
                roundSets.add(self.runOperationsRoundSet(j, roundsPerThread));
            }
        }
        LOGGER.info("\nBATCH: all {} roundsets queued in {}ms, waiting for completion",
                roundSets.size(), getDuration(start));
        return roundSets;
    }

    @Override
    public CompletableFuture<OperationRoundSetResult> runOperationsRoundSet(int userId, int roundsNumber) {
        LOGGER.debug("Run {} round(s) of bulk operations for user {}", roundsNumber, userId);
        OperationRoundSetResult result = new OperationRoundSetResult();
        WalletBulkOperationsRound round;
        for (int i = 1; i <= roundsNumber; i++) {
            round = ROUNDS[random.nextInt(ROUNDS.length)];
            LOGGER.debug(". Run round {}'{}' of {} of bulk operations for user {}",
                    i, round.getCode(), roundsNumber, userId);
            for (BiFunction<WalletClientService, Integer, String> operation : round.operations()) {
                operation.apply(walletClientService, userId);
                result.operationsNumber++;
            }
        }
        synchronized (completedRoundSets) {
            completedRoundSets.incrementAndGet();
            completedOperations.addAndGet(result.operationsNumber);
            completedRoundSets.notifyAll();
        }
        return CompletableFuture.completedFuture(result);
    }
}
