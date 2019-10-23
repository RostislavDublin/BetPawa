package rdublin.wallet.server.services;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface WalletService {
    public static final String OK_MESSAGE = "OK";
    public static final String INSUFFICIENT_FUNDS_MESSAGE = "Insufficient funds";
    public static final String UNKNOWN_CURRENCY_MESSAGE = "Unknown currency";
    public static final String UNKNOWN_ERROR_MESSAGE = "Unknown error";

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    String withdraw(int userId, int amount, String currencyCode);

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    String deposit(int userId, int amount, String currencyCode);

    Map<String, Integer> balance(int userId);
}
