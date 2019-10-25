package rdublin.wallet.server.services;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import rdublin.wallet.server.TestBase;
import rdublin.wallet.server.domain.Wallet;
import rdublin.wallet.server.repository.WalletRepository;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.*;
import static rdublin.utils.CurrencyUtils.*;
import static rdublin.wallet.server.services.WalletService.INSUFFICIENT_FUNDS_MESSAGE;
import static rdublin.wallet.server.services.WalletService.OK_MESSAGE;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WalletServiceImpl.class})
public class WalletServiceImplTest extends TestBase {

    private WalletService walletService;
    @Mock
    private WalletRepository walletRepository;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        walletService = spy(new WalletServiceImpl());
        Whitebox.setInternalState(walletService, "walletRepository", walletRepository);

        whenNew(Wallet.class).withArguments(ABSENT_USER_ID).thenReturn(createdWallet);

        when(walletRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(walletRepository.findById(USER_ID)).thenReturn(Optional.of(existingWallet));
    }

    @Test
    public void whenWithdrawAvailableFundsFromExistingWallet_thenMessageOk() {
        collector.checkThat(walletService.withdraw(USER_ID, 50, USD_CODE), equalTo(OK_MESSAGE));
        collector.checkThat(walletService.withdraw(USER_ID, 50, EUR_CODE), equalTo(OK_MESSAGE));
        collector.checkThat(walletService.withdraw(USER_ID, 50, GBP_CODE), equalTo(OK_MESSAGE));

        verify(walletRepository, times(3)).findById(USER_ID);
        verify(walletRepository, times(3)).save(existingWallet);
    }

    @Test
    public void whenWithdrawFromNotExistingWallet_thenMessageInsufficientFunds() {
        collector.checkThat(walletService.withdraw(ABSENT_USER_ID, 1, USD_CODE), equalTo(INSUFFICIENT_FUNDS_MESSAGE));
        collector.checkThat(walletService.withdraw(ABSENT_USER_ID, 1, EUR_CODE), equalTo(INSUFFICIENT_FUNDS_MESSAGE));
        collector.checkThat(walletService.withdraw(ABSENT_USER_ID, 1, GBP_CODE), equalTo(INSUFFICIENT_FUNDS_MESSAGE));

        verify(walletRepository, times(3)).findById(ABSENT_USER_ID);
    }

    @Test
    public void whenWithdrawTooMuchFromExistingWallet_thenMessageInsufficientFunds() {
        collector.checkThat(walletService.withdraw(USER_ID, 5000, USD_CODE), equalTo(INSUFFICIENT_FUNDS_MESSAGE));
        collector.checkThat(walletService.withdraw(USER_ID, 5000, EUR_CODE), equalTo(INSUFFICIENT_FUNDS_MESSAGE));
        collector.checkThat(walletService.withdraw(USER_ID, 5000, GBP_CODE), equalTo(INSUFFICIENT_FUNDS_MESSAGE));
    }

    @Test
    public void whenDepositFundsOnExistingWallet_thenMessageOk() {
        collector.checkThat(walletService.deposit(USER_ID, 50, USD_CODE), equalTo(OK_MESSAGE));
        collector.checkThat(walletService.deposit(USER_ID, 50, EUR_CODE), equalTo(OK_MESSAGE));
        collector.checkThat(walletService.deposit(USER_ID, 50, GBP_CODE), equalTo(OK_MESSAGE));

        verify(walletRepository, times(3)).findById(USER_ID);
        verify(walletRepository, times(3)).save(existingWallet);
    }

    @Test
    public void whenDepositFundsOnNotExistingWallet_thenMessageOk() throws Exception {
        collector.checkThat(walletService.deposit(ABSENT_USER_ID, 50, USD_CODE), equalTo(OK_MESSAGE));
        collector.checkThat(walletService.deposit(ABSENT_USER_ID, 50, EUR_CODE), equalTo(OK_MESSAGE));
        collector.checkThat(walletService.deposit(ABSENT_USER_ID, 50, GBP_CODE), equalTo(OK_MESSAGE));

        verify(walletRepository, times(3)).findById(ABSENT_USER_ID);
        verifyNew(Wallet.class, times(3)).withArguments(ABSENT_USER_ID);
    }

    @Test
    public void whenAskBalanceOfExistingWallet_thenGetActualBalance() {

        Map<String, Integer> balances = walletService.balance(USER_ID);
        collector.checkThat(balances.getOrDefault(USD_CODE, -1), equalTo(USD_AMOUNT));
        collector.checkThat(balances.getOrDefault(EUR_CODE, -1), equalTo(EUR_AMOUNT));
        collector.checkThat(balances.getOrDefault(GBP_CODE, -1), equalTo(GBP_AMOUNT));

        verify(walletRepository, times(1)).findById(USER_ID);
    }

    @Test
    public void whenAskBalanceOfNotExistingWallet_thenGetZeroBalance() {
        Map<String, Integer> balances = walletService.balance(ABSENT_USER_ID);
        collector.checkThat(balances.getOrDefault(USD_CODE, -1), equalTo(0));
        collector.checkThat(balances.getOrDefault(EUR_CODE, -1), equalTo(0));
        collector.checkThat(balances.getOrDefault(GBP_CODE, -1), equalTo(0));

        verify(walletRepository, times(1)).findById(ABSENT_USER_ID);
    }
}