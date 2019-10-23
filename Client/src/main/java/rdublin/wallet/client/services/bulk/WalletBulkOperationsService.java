package rdublin.wallet.client.services.bulk;

import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface WalletBulkOperationsService {

    /**
     * Sync batch definition, queueing and launch task
     * @param users wallet owner
     * @param threadsPerUser how many threads to run for each user in parallel
     * @param roundsPerThread how many operations rounds to perform sequentially in each user thread
     * @return
     */
    List<CompletableFuture<String>> runOperationsBatch(int users, int threadsPerUser, int roundsPerThread);

    /**
     * Async task to execute on certain user wallet.
     *
     * @param userId       wallet owner
     * @param roundsNumber how many operations rounds to perform sequentially
     * @return overall operation result description.
     */
    @Async("walletOperationsTaskExecutor")
    CompletableFuture<String> runOperationsRoundSet(int userId, int roundsNumber);
}
