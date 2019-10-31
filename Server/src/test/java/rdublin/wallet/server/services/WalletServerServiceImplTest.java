package rdublin.wallet.server.services;

import io.grpc.stub.StreamObserver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import rdublin.wallet.grpc.WalletBalance;
import rdublin.wallet.grpc.WalletBalanceResult;
import rdublin.wallet.grpc.WalletOperationDeposit;
import rdublin.wallet.grpc.WalletOperationResult;
import rdublin.wallet.grpc.WalletOperationWithdraw;
import rdublin.wallet.server.TestBaseServer;

import static org.mockito.AdditionalMatchers.geq;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.spy;
import static rdublin.utils.CurrencyUtils.*;
import static rdublin.wallet.server.services.WalletServerServiceImpl.*;
import static rdublin.wallet.server.services.WalletService.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WalletServiceImpl.class})
public class WalletServerServiceImplTest extends TestBaseServer {
    @Rule
    public ErrorCollector collector = new ErrorCollector();
    @Mock
    StreamObserver<WalletOperationResult> resultObserver;
    @Mock
    StreamObserver<WalletBalanceResult> balanceResultObserver;
    @Mock
    private WalletService walletService;
    private WalletServerServiceImpl walletServerService;
    private int SMALL_SUM = 50, BIG_SUM = 100;

    @Before
    public void setUp() throws Exception {
        walletServerService = spy(new WalletServerServiceImpl());
        Whitebox.setInternalState(walletServerService, "walletService", walletService);

        when(walletService.deposit(anyInt(), anyInt(), anyString())).thenReturn(OK_MESSAGE);
        when(walletService.deposit(anyInt(), anyInt(), eq("ABC"))).thenReturn(UNKNOWN_CURRENCY_MESSAGE);

        when(walletService.withdraw(anyInt(), anyInt(), anyString())).thenReturn(OK_MESSAGE);
        when(walletService.withdraw(eq(USER_ID), geq(BIG_SUM), anyString())).thenReturn(INSUFFICIENT_FUNDS_MESSAGE);

        when(walletService.balance(USER_ID)).thenReturn(TEST_BALANCE_MAP);
    }

    @Test
    public void whenWithdrawAvailableFunds_thenMessageOk() {
        WalletOperationWithdraw request;
        request = getWalletOperationWithdrawBuilder().setAmount(SMALL_SUM).setCurrency(USD_CODE).build();
        walletServerService.withdraw(request, resultObserver);
        request = getWalletOperationWithdrawBuilder().setAmount(SMALL_SUM).setCurrency(EUR_CODE).build();
        walletServerService.withdraw(request, resultObserver);
        request = getWalletOperationWithdrawBuilder().setAmount(SMALL_SUM).setCurrency(GBP_CODE).build();
        walletServerService.withdraw(request, resultObserver);

        verify(walletService, times(3)).withdraw(eq(USER_ID), eq(SMALL_SUM), anyString());
        verify(resultObserver, times(3)).onNext(OK_WALLET_OPERATION_RESULT);
    }

    @Test
    public void whenWithdrawInsufficientFunds_thenErrorMessage() {
        WalletOperationWithdraw request;
        request = getWalletOperationWithdrawBuilder().setAmount(BIG_SUM).setCurrency(USD_CODE).build();
        walletServerService.withdraw(request, resultObserver);
        request = getWalletOperationWithdrawBuilder().setAmount(BIG_SUM).setCurrency(EUR_CODE).build();
        walletServerService.withdraw(request, resultObserver);
        request = getWalletOperationWithdrawBuilder().setAmount(BIG_SUM).setCurrency(GBP_CODE).build();
        walletServerService.withdraw(request, resultObserver);

        verify(walletService, times(3)).withdraw(eq(USER_ID), eq(BIG_SUM), anyString());
        verify(resultObserver, times(3)).onNext(INSUFFICIENT_FUNDS_WALLET_OPERATION_RESULT);
    }

    @Test
    public void whenDeposit_thenMessageOk() {
        WalletOperationDeposit request;
        request = getWalletOperationDepositBuilder().setAmount(SMALL_SUM).setCurrency(USD_CODE).build();
        walletServerService.deposit(request, resultObserver);
        request = getWalletOperationDepositBuilder().setAmount(SMALL_SUM).setCurrency(EUR_CODE).build();
        walletServerService.deposit(request, resultObserver);
        request = getWalletOperationDepositBuilder().setAmount(SMALL_SUM).setCurrency(GBP_CODE).build();
        walletServerService.deposit(request, resultObserver);

        verify(walletService, times(3)).deposit(eq(USER_ID), eq(SMALL_SUM), anyString());
        verify(resultObserver, times(3)).onNext(OK_WALLET_OPERATION_RESULT);
    }
    @Test
    public void whenDepositWithUnknownCurrency_thenMessageError() {
        WalletOperationDeposit request;
        request = getWalletOperationDepositBuilder().setAmount(SMALL_SUM).setCurrency("ABC").build();
        walletServerService.deposit(request, resultObserver);

        verify(resultObserver).onNext(UNKNOWN_CURRENCY_WALLET_OPERATION_RESULT);
    }

    @Test
    public void whenBalance_thenProperSumsReturned() {

        WalletBalance request = WalletBalance.newBuilder().setUserId(USER_ID).build();
        walletServerService.balance(request, balanceResultObserver);
        verify(walletService).balance(USER_ID);
        verify(balanceResultObserver).onNext(any(WalletBalanceResult.class));
    }

    private WalletOperationWithdraw.Builder getWalletOperationWithdrawBuilder() {
        return WalletOperationWithdraw.newBuilder().setUserId(USER_ID);
    }

    private WalletOperationDeposit.Builder getWalletOperationDepositBuilder() {
        return WalletOperationDeposit.newBuilder().setUserId(USER_ID);
    }

}