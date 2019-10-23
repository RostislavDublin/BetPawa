package rdublin.wallet.server.services;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface WalletService {

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    String withdraw(int userId, int amount, String currencyCode);

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    String deposit(int userId, int amount, String currencyCode);

    Map<String, Integer> balance(int userId);
}
