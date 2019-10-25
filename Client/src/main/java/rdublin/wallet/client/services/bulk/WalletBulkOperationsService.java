package rdublin.wallet.client.services.bulk;

import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface WalletBulkOperationsService {

    /**
     * Async batch execution operation. Monitors execution, periodically reports statistics.
     *
     * @param users           wallet owner
     * @param threadsPerUser  how many threads to run for each user in parallel
     * @param roundsPerThread how many operations rounds to perform sequentially in each user thread
     * @return overall completion status future
     */
    //@Async("walletOperationsTaskExecutor")
    public CompletableFuture<String> performOperationsBatch(int users, int threadsPerUser, int roundsPerThread);

    /**
     * Sync batch definition, queueing and launch task
     *
     * @param users           wallet owner
     * @param threadsPerUser  how many threads to run for each user in parallel
     * @param roundsPerThread how many operations rounds to perform sequentially in each user thread
     * @return roundsets completion status future set
     */
    List<CompletableFuture<OperationRoundSetResult>> runOperationsBatch(int users, int threadsPerUser, int roundsPerThread);

    /**
     * Async task to execute on certain user wallet.
     *
     * @param userId       wallet owner
     * @param roundsNumber how many operations rounds to perform sequentially
     * @return overall operation result description.
     */
    @Async("walletOperationsTaskExecutor")
    CompletableFuture<OperationRoundSetResult> runOperationsRoundSet(int userId, int roundsNumber);

    class OperationRoundSetResult {
        int operationsNumber = 0;
    }
}
