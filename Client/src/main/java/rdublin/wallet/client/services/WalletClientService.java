package rdublin.wallet.client.services;

import java.util.Currency;
import java.util.Map;

public interface WalletClientService {

    String deposit(int userId, int amount, Currency currency);

    String withdraw(int userId, int amount, Currency currency);

    Map<String, Integer> balance(int userId);
}
