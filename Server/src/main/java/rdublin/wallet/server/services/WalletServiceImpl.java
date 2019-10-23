package rdublin.wallet.server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rdublin.wallet.server.domain.Wallet;
import rdublin.wallet.server.repository.WalletRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WalletServiceImpl implements WalletService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletServiceImpl.class);
    public static final String OK_MESSAGE = "OK";
    public static final String INSUFFICIENT_FUNDS_MESSAGE = "Insufficient funds";
    public static final String UNKNOWN_CURRENCY_MESSAGE = "Unknown currency";
    public static final String UNKNOWN_ERROR_MESSAGE = "Unknown error";
    public static final Map<String, Integer> BALANCE_MAP_TEMPLATE;
    private static final Set<String> KNOWN_CURRENCY_CODES;

    static {
        KNOWN_CURRENCY_CODES = new HashSet(Arrays.asList("USD", "EUR", "GBP"));

        Map<String, Integer> map = KNOWN_CURRENCY_CODES.stream().collect(Collectors.toMap(k -> k, k -> 0));
        BALANCE_MAP_TEMPLATE = Collections.unmodifiableMap(map);
    }
    @Autowired
    private WalletRepository walletRepository;


    @Override
    public String withdraw(int userId, int amount, String currencyCode) {

        if (!KNOWN_CURRENCY_CODES.contains(currencyCode)) {
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
    public String deposit(int userId, int amount, String currencyCode) {

        if (!KNOWN_CURRENCY_CODES.contains(currencyCode)) {
            return UNKNOWN_CURRENCY_MESSAGE;
        }
        Optional<Wallet> walletIfPresent = walletRepository.findById(userId);
        Wallet wallet;
        if (!walletIfPresent.isPresent()) {
            wallet = new Wallet();
            wallet.setUserId(userId);
            setBalance(wallet, amount, currencyCode);
        } else {
            wallet = walletIfPresent.get();
            setBalance(wallet, getBalance(wallet, currencyCode) + amount, currencyCode);
        }
        walletRepository.save(wallet);
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

    private Integer getBalance(Wallet wallet, String currencyCode) {
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
