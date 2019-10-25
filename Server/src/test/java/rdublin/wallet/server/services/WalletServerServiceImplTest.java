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
import rdublin.wallet.grpc.WalletOperationResult;
import rdublin.wallet.grpc.WalletOperationWithdraw;
import rdublin.wallet.server.TestBase;

import static org.mockito.AdditionalMatchers.gt;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.spy;
import static rdublin.utils.CurrencyUtils.USD_CODE;
import static rdublin.wallet.server.services.WalletServerServiceImpl.OK_WALLET_OPERATION_RESULT;
import static rdublin.wallet.server.services.WalletService.INSUFFICIENT_FUNDS_MESSAGE;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WalletServiceImpl.class})
public class WalletServerServiceImplTest extends TestBase {
    @Rule
    public ErrorCollector collector = new ErrorCollector();
    @Mock
    StreamObserver<WalletOperationResult> resultObserver;
    @Mock
    private WalletService walletService;
    private WalletServerServiceImpl walletServerService;

    @Before
    public void setUp() throws Exception {
        walletServerService = spy(new WalletServerServiceImpl());
        Whitebox.setInternalState(walletServerService, "walletService", walletService);

        when(walletService.withdraw(anyInt(), anyInt(), any(String.class))).thenReturn(INSUFFICIENT_FUNDS_MESSAGE);
        when(walletService.withdraw(eq(USER_ID), gt(USD_AMOUNT), eq(USD_CODE))).thenReturn(INSUFFICIENT_FUNDS_MESSAGE);
        //when(walletService.withdraw(eq(USER_ID), leq(USD_AMOUNT), eq(USD_CODE))).thenReturn(OK_MESSAGE);
    }

    @Test
    public void whenWithdrawAvailableFunds_thenMessageOk() {
        WalletOperationWithdraw request;
        request = WalletOperationWithdraw.newBuilder().setAmount(50).setCurrency(USD_CODE).build();
        walletServerService.withdraw(request, resultObserver);
        verify(resultObserver).onNext(OK_WALLET_OPERATION_RESULT);
    }

    @Test
    public void deposit() {
    }

    @Test
    public void balance() {
    }
}