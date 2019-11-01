package rdublin.wallet.client.services;

import io.grpc.Deadline;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import rdublin.wallet.client.TestBaseClient;
import rdublin.wallet.grpc.WalletBalance;
import rdublin.wallet.grpc.WalletBalanceResult;
import rdublin.wallet.grpc.WalletOperationDeposit;
import rdublin.wallet.grpc.WalletOperationResult;
import rdublin.wallet.grpc.WalletOperationWithdraw;
import rdublin.wallet.grpc.WalletServiceGrpc;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static rdublin.utils.CurrencyUtils.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WalletClientServiceImpl.class, WalletServiceGrpc.WalletServiceBlockingStub.class})
public class WalletClientServiceImplTest extends TestBaseClient {

    private WalletClientServiceImpl walletClientService;
    @Mock
    private WalletServiceGrpc.WalletServiceBlockingStub walletServiceBlockingStub;

    private WalletOperationResult walletOperationResult;
    private WalletBalanceResult walletBalanceResult;

    @Before
    public void setUp() throws Exception {
        walletClientService = spy(WalletClientServiceImpl.class);
        Whitebox.setInternalState(walletClientService, "walletServiceBlockingStub", walletServiceBlockingStub);

        walletOperationResult = WalletOperationResult.newBuilder().setMessage(OK_MESSAGE).build();
        walletBalanceResult = WalletBalanceResult.newBuilder().putAllAmounts(TEST_BALANCE_MAP).build();
        when(walletServiceBlockingStub.withDeadlineAfter(anyLong(), any(TimeUnit.class)))
                .thenReturn(walletServiceBlockingStub);
        when(walletServiceBlockingStub.deposit(any())).thenReturn(walletOperationResult);
        when(walletServiceBlockingStub.withdraw(any())).thenReturn(walletOperationResult);
        when(walletServiceBlockingStub.balance(any())).thenReturn(walletBalanceResult);
    }

    @Test
    public void whenDeposit_thenWalletServiceBlockingStubUsed() {
        collector.checkThat(walletClientService.deposit(USER_ID, USD_AMOUNT, USD), equalTo(OK_MESSAGE));
        collector.checkThat(walletClientService.deposit(USER_ID, EUR_AMOUNT, EUR), equalTo(OK_MESSAGE));
        collector.checkThat(walletClientService.deposit(USER_ID, GBP_AMOUNT, GBP), equalTo(OK_MESSAGE));

        verify(walletServiceBlockingStub, times(3)).deposit(any(WalletOperationDeposit.class));
    }

    @Test
    public void whenWithdraws_thenWalletServiceBlockingStubUsed() {
        collector.checkThat(walletClientService.withdraw(USER_ID, USD_AMOUNT, USD), equalTo(OK_MESSAGE));
        collector.checkThat(walletClientService.withdraw(USER_ID, EUR_AMOUNT, EUR), equalTo(OK_MESSAGE));
        collector.checkThat(walletClientService.withdraw(USER_ID, GBP_AMOUNT, GBP), equalTo(OK_MESSAGE));

        verify(walletServiceBlockingStub, times(3)).withdraw(any(WalletOperationWithdraw.class));
    }

    @Test
    public void whenBalance_thenWalletServiceBlockingStubUsed() {
        collector.checkThat(walletClientService.balance(USER_ID), equalTo(TEST_BALANCE_MAP));

        verify(walletServiceBlockingStub).balance(any(WalletBalance.class));
    }

}