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
import java.util.function.BiFunction;

import static rdublin.utils.MetricsUtils.getDuration;

@Service
@Scope("singleton")
public class WalletBulkOperationsServiceImpl implements WalletBulkOperationsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletBulkOperationsServiceImpl.class);
    private static final WalletBulkOperationsRound[] ROUNDS = new WalletBulkOperationsRound[]{
            new WalletBulkOperationsRoundA(), new WalletBulkOperationsRoundB(), new WalletBulkOperationsRoundC()
    };
    Random random = new Random();
    @Autowired
    private ApplicationContext context;
    @Autowired
    private WalletClientService walletClientService;

    public List<CompletableFuture<String>> runOperationsBatch(int users, int threadsPerUser, int roundsPerThread) {
        long start = System.currentTimeMillis();
        LOGGER.info("\nBATCH: Starting for {} users * {} threads * {} rounds", users, threadsPerUser, roundsPerThread);
        //because @Async doesn't work with 'this'
        WalletBulkOperationsService self = context.getBean(WalletBulkOperationsService.class);
        List<CompletableFuture<String>> roundSets = new ArrayList<>();
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
    public CompletableFuture<String> runOperationsRoundSet(int userId, int roundsNumber) {
        LOGGER.debug("Run {} round(s) of bulk operations for user {}", roundsNumber, userId);
        WalletBulkOperationsRound round;
        for (int i = 1; i <= roundsNumber; i++) {
            round = ROUNDS[random.nextInt(ROUNDS.length)];
            LOGGER.debug(". Run round {}'{}' of {} of bulk operations for user {}",
                    i, round.getCode(), roundsNumber, userId);
            for (BiFunction<WalletClientService, Integer, String> operation : round.operations()) {
                operation.apply(walletClientService, userId);
            }
        }
        return CompletableFuture.completedFuture("Completed");
    }
}
