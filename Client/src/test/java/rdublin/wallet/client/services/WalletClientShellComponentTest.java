package rdublin.wallet.client.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import rdublin.wallet.client.TestBaseClient;
import rdublin.wallet.client.services.bulk.WalletBulkOperationsService;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;
import static rdublin.utils.CurrencyUtils.USD;
import static rdublin.utils.CurrencyUtils.USD_CODE;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WalletClientShellComponent.class})
public class WalletClientShellComponentTest extends TestBaseClient {

    private static final int EXEC_CORE_SIZE = 4;
    private static final int EXEC_MAX_SIZE = 6;
    WalletClientShellComponent walletClientShellComponent;

    @Mock
    ThreadPoolTaskExecutor walletOperationsTaskExecutor;

    @Mock
    WalletBulkOperationsService walletBulkOperationsService;

    @Mock
    private WalletClientService walletClientService;


    @Before
    public void setUp() throws Exception {
        walletClientShellComponent = spy(new WalletClientShellComponent());
        Whitebox.setInternalState(walletClientShellComponent, "walletOperationsTaskExecutor",
                walletOperationsTaskExecutor);
        Whitebox.setInternalState(walletClientShellComponent, "walletBulkOperationsService",
                walletBulkOperationsService);
        Whitebox.setInternalState(walletClientShellComponent, "walletClientService",
                walletClientService);

        when(walletBulkOperationsService.performOperationsBatch(anyInt(), anyInt(), anyInt()))
                .thenReturn(CompletableFuture.completedFuture(BATCH_COMPLETED));
    }

    @Test
    public void whenSetUsers_thenRightInternalState() {
        collector.checkThat("Default [users] field value should be 1",
                (int) Whitebox.getInternalState(walletClientShellComponent, "users"), equalTo(1));
        walletClientShellComponent.users(USERS);
        collector.checkThat("Changed [users] field value should be properly set",
                (int) Whitebox.getInternalState(walletClientShellComponent, "users"), equalTo(USERS));
    }

    @Test
    public void whenSetThreadsPerUser_thenRightInternalState() {
        collector.checkThat("Default [threadsPerUser] field value should be 1",
                (int) Whitebox.getInternalState(walletClientShellComponent, "threadsPerUser"), equalTo(1));
        walletClientShellComponent.threadsPerUser(THREADS_PER_USER);
        collector.checkThat("Changed [threadsPerUser] field value should be properly set",
                (int) Whitebox.getInternalState(walletClientShellComponent, "threadsPerUser"),
                equalTo(THREADS_PER_USER));
    }

    @Test
    public void whenSetRoundsPerThread_thenRightInternalState() {
        collector.checkThat("Default [roundsPerThread] field value should be 1",
                (int) Whitebox.getInternalState(walletClientShellComponent, "roundsPerThread"), equalTo(1));
        walletClientShellComponent.roundsPerThread(ROUNDS_PER_THREAD);
        collector.checkThat("Changed [roundsPerThread] field value should be properly set",
                (int) Whitebox.getInternalState(walletClientShellComponent, "roundsPerThread"),
                equalTo(ROUNDS_PER_THREAD));
    }

    @Test
    public void whenSetupExecutor_thenExecutorSettingsSet() {
        Whitebox.setInternalState(walletClientShellComponent, "walletOperationsTaskExecutor",
                walletOperationsTaskExecutor);
        walletClientShellComponent.setupExecutor(EXEC_CORE_SIZE, EXEC_MAX_SIZE);

        verify(walletOperationsTaskExecutor).setCorePoolSize(EXEC_CORE_SIZE);
        verify(walletOperationsTaskExecutor).setMaxPoolSize(EXEC_MAX_SIZE);
    }

    @Test
    public void whenCurrentConfigRequested_thenActualSettingsGet() {
        when(walletOperationsTaskExecutor.getCorePoolSize()).thenReturn(EXEC_CORE_SIZE);
        when(walletOperationsTaskExecutor.getMaxPoolSize()).thenReturn(EXEC_MAX_SIZE);
        Whitebox.setInternalState(walletClientShellComponent, "users", USERS);
        Whitebox.setInternalState(walletClientShellComponent, "threadsPerUser", THREADS_PER_USER);
        Whitebox.setInternalState(walletClientShellComponent, "roundsPerThread", ROUNDS_PER_THREAD);

        String currentConfig = walletClientShellComponent.currentConfig();

        verify(walletOperationsTaskExecutor).getCorePoolSize();
        verify(walletOperationsTaskExecutor).getMaxPoolSize();

        collector.checkThat(currentConfig, containsString("- core pool size: " + EXEC_CORE_SIZE));
        collector.checkThat(currentConfig, containsString("- max pool size: " + EXEC_MAX_SIZE));
        collector.checkThat(currentConfig, containsString("- users: " + USERS));
        collector.checkThat(currentConfig, containsString("- threads per user: " + THREADS_PER_USER));
        collector.checkThat(currentConfig, containsString("- rounds per thread: " + ROUNDS_PER_THREAD));
    }

    @Test
    public void runFromScratch() {
        walletClientShellComponent.runFromScratch(USERS, THREADS_PER_USER, ROUNDS_PER_THREAD);

        verify(walletClientShellComponent).users(USERS);
        verify(walletClientShellComponent).threadsPerUser(THREADS_PER_USER);
        verify(walletClientShellComponent).roundsPerThread(ROUNDS_PER_THREAD);
        verify(walletClientShellComponent).runPrepared();
    }

    @Test
    public void runPrepared() {
        walletClientShellComponent.runPrepared();
        verify(walletBulkOperationsService).performOperationsBatch(1, 1, 1);
    }

    @Test
    public void runWithdraw() {
        walletClientShellComponent.runWithdraw(USERS, USD_AMOUNT, USD_CODE);
        verify(walletClientService).withdraw(USERS, USD_AMOUNT, USD);
    }

    @Test
    public void runDeposit() {
        walletClientShellComponent.runDeposit(USERS, USD_AMOUNT, USD_CODE);
        verify(walletClientService).deposit(USERS, USD_AMOUNT, USD);
    }

    @Test
    public void runBalance() {
        walletClientShellComponent.runBalance(USERS);
        verify(walletClientService).balance(USERS);
    }
}