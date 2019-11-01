package rdublin.wallet.client.services.bulk;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;
import rdublin.wallet.client.TestBaseClient;
import rdublin.wallet.client.services.WalletClientService;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WalletBulkOperationsServiceImpl.class})
public class WalletBulkOperationsServiceImplTest extends TestBaseClient {

    List<CompletableFuture<WalletBulkOperationsService.OperationRoundSetResult>> roundSetResultList = emptyList();
    @Mock
    WalletBulkOperationsService.OperationRoundSetResult operationRoundSetResult;
    private WalletBulkOperationsServiceImpl walletBulkOperationsService;
    @Mock
    private ApplicationContext context;
    @Mock
    private WalletClientService walletClientService;

    @Before
    public void setUp() throws Exception {
        walletBulkOperationsService = spy(new WalletBulkOperationsServiceImpl());
    }

    @Test
    public void performOperationsBatch() throws ExecutionException, InterruptedException {
        doReturn(roundSetResultList).when(walletBulkOperationsService)
                                    .runOperationsBatch(USERS, THREADS_PER_USER, ROUNDS_PER_THREAD);

        CompletableFuture<String> result;
        result = walletBulkOperationsService.performOperationsBatch(USERS, THREADS_PER_USER, ROUNDS_PER_THREAD);
        verify(walletBulkOperationsService).runOperationsBatch(USERS, THREADS_PER_USER, ROUNDS_PER_THREAD);

        assertEquals(BATCH_COMPLETED, result.get());
    }

    @Test
    public void runOperationsBatch() {
        Whitebox.setInternalState(walletBulkOperationsService, "context", context);
        when(context.getBean(WalletBulkOperationsService.class)).thenReturn(walletBulkOperationsService);
        Whitebox.setInternalState(operationRoundSetResult, "operationsNumber", OPERATIONS_NUMBER);

        when(walletBulkOperationsService.runOperationsRoundSet(anyInt(), eq(ROUNDS_PER_THREAD)))
                .thenReturn(CompletableFuture.completedFuture(operationRoundSetResult));

        List<CompletableFuture<WalletBulkOperationsService.OperationRoundSetResult>> results =
                walletBulkOperationsService.runOperationsBatch(USERS, THREADS_PER_USER, ROUNDS_PER_THREAD);

        int numberOfRoundSets = THREADS_PER_USER * USERS;

        verify(walletBulkOperationsService, times(numberOfRoundSets))
                .runOperationsRoundSet(anyInt(), eq(ROUNDS_PER_THREAD));

        assertEquals(numberOfRoundSets, results.size());
    }

    @Test
    public void runOperationsRoundSet() throws ExecutionException, InterruptedException {
        Whitebox.setInternalState(walletBulkOperationsService, "walletClientService", walletClientService);
        CompletableFuture<WalletBulkOperationsService.OperationRoundSetResult> result =
                walletBulkOperationsService.runOperationsRoundSet(USER_ID, ROUNDS_PER_THREAD);

        Collection<Invocation> invocations = Mockito.mockingDetails(walletClientService).getInvocations();

        assertEquals(result.get().operationsNumber, invocations.size());
    }
}