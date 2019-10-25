package rdublin.wallet.server;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;
import rdublin.wallet.server.domain.Wallet;

public class TestBase {
    public static final int USER_ID = 777;
    public static final int ABSENT_USER_ID = 99999999;

    public static final int USD_AMOUNT = 111;
    public static final int EUR_AMOUNT = 222;
    public static final int GBP_AMOUNT = 333;
    @Rule
    public ErrorCollector collector = new ErrorCollector();
    protected Wallet createdWallet;
    protected Wallet existingWallet;

    @Before
    public void setUp() throws Exception {
        existingWallet = new Wallet(USER_ID, TestBase.USD_AMOUNT, TestBase.EUR_AMOUNT, TestBase.GBP_AMOUNT);
        createdWallet = new Wallet(ABSENT_USER_ID);
    }

}
