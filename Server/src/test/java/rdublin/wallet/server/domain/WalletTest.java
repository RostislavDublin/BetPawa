package rdublin.wallet.server.domain;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import rdublin.wallet.server.TestBaseServer;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static rdublin.utils.CurrencyUtils.*;

public class WalletTest extends TestBaseServer {

    @Rule
    public ErrorCollector collector = new ErrorCollector();
    private Wallet wallet;

    @Before
    public void before() {
        wallet = new Wallet();
    }

    @Test
    public void testEquals() {
        collector.checkThat("Should be true for the same object", wallet, equalTo(wallet));
        Wallet newWallet = new Wallet();
        collector.checkThat("Should be true for the equal object", wallet, equalTo(newWallet));
        newWallet.setUserId(USER_ID + 1);
        collector.checkThat("Should be false for not equal object", wallet, not(equalTo(newWallet)));
    }

    @Test
    public void whenSetUserId_thenGetTheSame() {
        wallet.setUserId(USER_ID);
        assertEquals(USER_ID, wallet.getUserId());
    }

    @Test
    public void whenSetUsdBalance_thenGetTheSame() {
        wallet.setUsdBalance(USD_AMOUNT);
        assertEquals(USD_AMOUNT, wallet.getUsdBalance());
    }

    @Test
    public void whenSetEurBalance_thenGetTheSame() {
        wallet.setEurBalance(EUR_AMOUNT);
        assertEquals(EUR_AMOUNT, wallet.getEurBalance());
    }

    @Test
    public void whenSetGbpBalance_thenGetTheSame() {
        wallet.setGbpBalance(GBP_AMOUNT);
        assertEquals(GBP_AMOUNT, wallet.getGbpBalance());
    }

    @Test
    public void testHashCode() {
        int walletHashCode = wallet.hashCode();
        collector.checkThat("Shouldn't be 0", walletHashCode, not(equalTo(0)));
        wallet.setUsdBalance(USD_AMOUNT);
        collector.checkThat("Shouldn't change if identifying fields aren't changed",
                walletHashCode, equalTo(wallet.hashCode()));

        wallet.setUserId(USER_ID + 1);
        collector.checkThat("Should change if identifying fields changed",
                walletHashCode, not(equalTo(wallet.hashCode())));

    }

    @Test
    public void testToString() {
        String walletToString = wallet.toString();
        collector.checkThat(walletToString.contains(USD_CODE.concat(String.valueOf(0))), equalTo(true));
        collector.checkThat(walletToString.contains(EUR_CODE.concat(String.valueOf(0))), equalTo(true));
        collector.checkThat(walletToString.contains(GBP_CODE.concat(String.valueOf(0))), equalTo(true));

        wallet = new Wallet(USER_ID, USD_AMOUNT, EUR_AMOUNT, GBP_AMOUNT);
        walletToString = wallet.toString();
        collector.checkThat(walletToString.contains(USD_CODE.concat(String.valueOf(USD_AMOUNT))), equalTo(true));
        collector.checkThat(walletToString.contains(EUR_CODE.concat(String.valueOf(EUR_AMOUNT))), equalTo(true));
        collector.checkThat(walletToString.contains(GBP_CODE.concat(String.valueOf(GBP_AMOUNT))), equalTo(true));
    }
}