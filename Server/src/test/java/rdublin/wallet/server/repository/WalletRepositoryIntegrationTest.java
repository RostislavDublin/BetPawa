package rdublin.wallet.server.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import rdublin.wallet.server.TestBaseServer;
import rdublin.wallet.server.domain.Wallet;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(SpringRunner.class)
@DataJpaTest
public class WalletRepositoryIntegrationTest {
    @Autowired
    private WalletRepository walletRepository;

    @Before
    public void setUp() throws Exception {
        Wallet wallet = new Wallet(TestBaseServer.USER_ID, TestBaseServer.USD_AMOUNT, TestBaseServer.EUR_AMOUNT, TestBaseServer.GBP_AMOUNT);
        walletRepository.save(wallet);
        wallet = new Wallet(1, 1, 2, 3);
        walletRepository.save(wallet);
        wallet = new Wallet(2, 11, 22, 33);
        walletRepository.save(wallet);
    }
    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Test
    public void whenFindByUserId_thenReturnProperWalletWithMoney() {
        Optional<Wallet> walletIfAny = walletRepository.findById(TestBaseServer.USER_ID);
        Assert.assertTrue(walletIfAny.isPresent());
        Wallet wallet = walletIfAny.get();
        collector.checkThat(wallet.getUserId(), equalTo(TestBaseServer.USER_ID));
        collector.checkThat(wallet.getUsdBalance(), equalTo(TestBaseServer.USD_AMOUNT));
        collector.checkThat(wallet.getEurBalance(), equalTo(TestBaseServer.EUR_AMOUNT));
        collector.checkThat(wallet.getGbpBalance(), equalTo(TestBaseServer.GBP_AMOUNT));
    }
}