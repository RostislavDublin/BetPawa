package rdublin.wallet.client;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static rdublin.utils.CurrencyUtils.*;

public class TestBaseClient {
    public static final int USER_ID = 777;
    public static final int ABSENT_USER_ID = 99999999;

    public static final int USD_AMOUNT = 111;
    public static final int EUR_AMOUNT = 222;
    public static final int GBP_AMOUNT = 333;

    public static final String OK_MESSAGE = "OK";
    protected static final int ROUNDS_PER_THREAD = 7;
    protected static final int THREADS_PER_USER = 8;
    protected static final int USERS = 9;
    protected static final Integer OPERATIONS_NUMBER = 5;

    protected static Map<String, Integer> TEST_BALANCE_MAP;
    protected final String BATCH_COMPLETED = "Batch Completed";
    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @BeforeClass
    public static void setUpClass() throws Exception {
        Map<String, Integer> testBalanceMap = new HashMap<>();
        testBalanceMap.put(USD_CODE, USD_AMOUNT);
        testBalanceMap.put(EUR_CODE, EUR_AMOUNT);
        testBalanceMap.put(GBP_CODE, GBP_AMOUNT);
        TEST_BALANCE_MAP = Collections.unmodifiableMap(testBalanceMap);
    }

}
