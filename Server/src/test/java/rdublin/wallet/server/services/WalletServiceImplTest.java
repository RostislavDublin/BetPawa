package rdublin.wallet.server.services;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import rdublin.wallet.server.repository.WalletRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
class WalletServiceImplTest {

    @Autowired
    private WalletService walletService;

    @Test
    //@Transactional
    void withdraw() {
        System.out.println("111");
        walletService.withdraw(1, 100, "USD");
        System.out.println("222");
        walletService.withdraw(1, 100, "USD");
        System.out.println("333");
    }

    @Test
    void deposit() {
    }

    @Test
    void balance() {
    }
}