package rdublin.wallet.server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import rdublin.utils.CurrencyUtils;
import rdublin.wallet.server.domain.Wallet;
import rdublin.wallet.server.repository.WalletRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WalletServiceImpl implements WalletService {

    public static final Map<String, Integer> BALANCE_MAP_TEMPLATE;
    private static final Logger LOGGER = LoggerFactory.getLogger(WalletServiceImpl.class);

    static {
        Map<String, Integer> map =
                CurrencyUtils.KNOWN_CURRENCY_CODES.stream().collect(Collectors.toMap(k -> k, k -> 0));
        BALANCE_MAP_TEMPLATE = Collections.unmodifiableMap(map);
    }

    @Autowired
    private WalletRepository walletRepository;

    @Override
    @Retryable(include = {ObjectOptimisticLockingFailureException.class},
            maxAttempts = 100, backoff = @Backoff(random = true, delay = 100, maxDelay = 10000L, multiplier = 10))
    public String withdraw(int userId, int amount, String currencyCode) {

        if (!CurrencyUtils.KNOWN_CURRENCY_CODES.contains(currencyCode)) {
            return UNKNOWN_CURRENCY_MESSAGE;
        }
        Optional<Wallet> walletIfPresent = walletRepository.findById(userId);
        Wallet wallet;
        if (!walletIfPresent.isPresent()) {
            return INSUFFICIENT_FUNDS_MESSAGE;
        } else {
            wallet = walletIfPresent.get();
            int funds = getBalance(wallet, currencyCode);
            int newBalance = funds - amount;
            if (newBalance < 0) {
                return INSUFFICIENT_FUNDS_MESSAGE;
            } else {
                setBalance(wallet, newBalance, currencyCode);
                walletRepository.save(wallet);
                return OK_MESSAGE;
            }
        }
    }

    @Override
    @Retryable(
            include = {DataIntegrityViolationException.class, ObjectOptimisticLockingFailureException.class},
            maxAttempts = 100, backoff = @Backoff(random = true, delay = 100, maxDelay = 10000L, multiplier = 10))
    public String deposit(int userId, int amount, String currencyCode) {
        LOGGER.debug("User {} DEPOSIT {}{} - starting", userId, currencyCode, amount);
        if (!CurrencyUtils.KNOWN_CURRENCY_CODES.contains(currencyCode)) {
            return UNKNOWN_CURRENCY_MESSAGE;
        }
        Optional<Wallet> walletIfPresent = walletRepository.findById(userId);
        Wallet wallet;
        if (!walletIfPresent.isPresent()) {
            LOGGER.debug("User {} DEPOSIT {}{} - no wallet, create", userId, currencyCode, amount);
            wallet = new Wallet(userId);
            setBalance(wallet, amount, currencyCode);
            walletRepository.persist(wallet);
        } else {
            wallet = walletIfPresent.get();
            int currentBalance = getBalance(wallet, currencyCode);
            LOGGER.debug("User {} DEPOSIT {}{} - has wallet with {}, add",
                    userId, currencyCode, amount, currentBalance);

            setBalance(wallet, currentBalance + amount, currencyCode);
            //managed! walletRepository.save(wallet);
        }

        return OK_MESSAGE;
    }

    @Override
    public Map<String, Integer> balance(int userId) {

        Optional<Wallet> walletIfPresent = walletRepository.findById(userId);

        if (walletIfPresent.isPresent()) {
            Wallet wallet = walletIfPresent.get();
            Map<String, Integer> map = new HashMap<>(3);
            map.put("USD", wallet.getUsdBalance());
            map.put("EUR", wallet.getEurBalance());
            map.put("GBP", wallet.getGbpBalance());
            return map;
        } else {
            return BALANCE_MAP_TEMPLATE;
        }
    }

    private int getBalance(Wallet wallet, String currencyCode) {
        switch (currencyCode) {
            case "USD":
                return wallet.getUsdBalance();
            case "EUR":
                return wallet.getEurBalance();
            case "GBP":
                return wallet.getGbpBalance();
            default:
                return 0;
        }
    }

    private void setBalance(Wallet wallet, int i, String currencyCode) {
        switch (currencyCode) {
            case "USD":
                wallet.setUsdBalance(i);
                break;
            case "EUR":
                wallet.setEurBalance(i);
                break;
            case "GBP":
                wallet.setGbpBalance(i);
                break;
        }
    }

}
