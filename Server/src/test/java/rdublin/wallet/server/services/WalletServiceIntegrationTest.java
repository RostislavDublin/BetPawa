package rdublin.wallet.server.services;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import rdublin.wallet.server.TestBaseServer;
import rdublin.wallet.server.repository.WalletRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static rdublin.utils.CurrencyUtils.USD_CODE;

@RunWith(SpringRunner.class)
//@DataJpaTest
@SpringBootTest
@AutoConfigureTestDatabase
public class WalletServiceIntegrationTest extends TestBaseServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WalletServiceIntegrationTest.class);
    @Rule
    public ErrorCollector collector = new ErrorCollector();
    @Autowired
    private WalletService walletService;
    @Autowired
    private WalletRepository walletRepository;

    @Before
    public void setUp() {
    }

    /**
     * Here we test many (N) parallel threads treating the same wallet. The wallet doesn't exist (killed) on the
     * start. We use optimistic locking, so (N-1) threads face "Unique index or primary key violation" trying to create
     * the wallet. Then they retry many times, now trying to update the created wallet. Most of them then face
     * "ObjectOptimisticLockingFailureException" and retry again. To optimize those retries we use special "random"
     * @Backoff policy to de-synchronise retries. The case is extremal, cause it is not realistic in production that
     * many (more than 2 or 3) simultaneous sessions of the same user treat its wallet in parallel (in such an active
     * mode where transactions go one-by-one without any pause). Finally, here we test the reliability of chosen lock
     * mode - after all these multiple parallel deposits/withdraws the final resulting sum should become right.
     *
     * @throws InterruptedException
     */
    @Test
    public void whenManyThreadsDepositAndWithdrawNewWalletInParallel_thenRightEndBalance() throws InterruptedException {

        if (walletRepository.findById(USER_ID).isPresent()) {
            walletRepository.deleteById(USER_ID);
        }

        int threadsNumber = 10;
        CountDownLatch startLatch = new CountDownLatch(threadsNumber);
        CountDownLatch endLatch = new CountDownLatch(threadsNumber);

        Runnable depositor = () -> {
            startLatch.countDown();
            walletService.deposit(USER_ID, USD_AMOUNT * 3, USD_CODE);
            walletService.withdraw(USER_ID, USD_AMOUNT, USD_CODE);
            endLatch.countDown();
        };

        for (int i = 1; i <= threadsNumber; i++) {
            new Thread(depositor, "T" + i).start();
        }

        endLatch.await(60, TimeUnit.SECONDS);

        int desiredFinalBalance = USD_AMOUNT * threadsNumber * 2;
        int actualFinalBalance = walletService.balance(USER_ID).get(USD_CODE);
        collector.checkThat(actualFinalBalance, equalTo(desiredFinalBalance));
        LOGGER.info("Final wallet balance: USD{}", actualFinalBalance);
    }

}