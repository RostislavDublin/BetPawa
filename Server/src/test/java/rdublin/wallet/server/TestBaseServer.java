package rdublin.wallet.server;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;
import rdublin.wallet.server.domain.Wallet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static rdublin.utils.CurrencyUtils.*;

public class TestBaseServer {
    public static final int USER_ID = 777;
    public static final int ABSENT_USER_ID = 99999999;

    public static final int USD_AMOUNT = 111;
    public static final int EUR_AMOUNT = 222;
    public static final int GBP_AMOUNT = 333;
    protected static Map<String, Integer> TEST_BALANCE_MAP;
    @Rule
    public ErrorCollector collector = new ErrorCollector();
    protected Wallet createdWallet;
    protected Wallet existingWallet;

    @BeforeClass
    public static void setUpClass() throws Exception {
        Map<String, Integer> testBalanceMap = new HashMap<>();
        testBalanceMap.put(USD_CODE, USD_AMOUNT);
        testBalanceMap.put(EUR_CODE, EUR_AMOUNT);
        testBalanceMap.put(GBP_CODE, GBP_AMOUNT);
        TEST_BALANCE_MAP = Collections.unmodifiableMap(testBalanceMap);
    }

    @Before
    public void setUp() throws Exception {
        existingWallet = new Wallet(USER_ID, TestBaseServer.USD_AMOUNT, TestBaseServer.EUR_AMOUNT, TestBaseServer.GBP_AMOUNT);
        createdWallet = new Wallet(ABSENT_USER_ID);
    }

}
