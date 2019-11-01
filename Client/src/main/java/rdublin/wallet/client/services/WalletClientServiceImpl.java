package rdublin.wallet.client.services;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rdublin.wallet.grpc.WalletBalance;
import rdublin.wallet.grpc.WalletBalanceResult;
import rdublin.wallet.grpc.WalletOperationDeposit;
import rdublin.wallet.grpc.WalletOperationResult;
import rdublin.wallet.grpc.WalletOperationWithdraw;
import rdublin.wallet.grpc.WalletServiceGrpc;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Currency;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static rdublin.utils.CurrencyUtils.*;
import static rdublin.utils.MetricsUtils.getDuration;

@Component
public class WalletClientServiceImpl implements WalletClientService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WalletClientServiceImpl.class);
    private final long OPERATION_TIMEOUT = 30_000;
    private WalletServiceGrpc.WalletServiceBlockingStub walletServiceBlockingStub;

    @Value("${wallet.server.address}")
    private String walletServerAddress;
    @Value("${wallet.server.port}")
    private int walletServerPort;

    @PostConstruct
    private void init() {
        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress(walletServerAddress, walletServerPort).usePlaintext().build();

        walletServiceBlockingStub = WalletServiceGrpc.newBlockingStub(managedChannel);
    }

    @Override
    public String deposit(@Min(1) int userId, @Min(1) int amount, @NotNull final Currency currency) {

        long start = System.currentTimeMillis();
        LOGGER.debug("\nRequesting DEPOSIT for user {}, {}{}", userId, currency, amount);
        WalletOperationDeposit walletOperationDeposit
                = WalletOperationDeposit.newBuilder()
                                        .setUserId(userId)
                                        .setAmount(amount)
                                        .setCurrency(currency.getCurrencyCode())
                                        .build();

        WalletOperationResult walletOperationResult = walletServiceBlockingStub
                .withDeadlineAfter(OPERATION_TIMEOUT, TimeUnit.MILLISECONDS)
                .deposit(walletOperationDeposit);

        LOGGER.debug("\nResponded on DEPOSIT in {}ms: {}", getDuration(start), walletOperationResult.getMessage());

        return walletOperationResult.getMessage();
    }

    @Override
    public String withdraw(@Min(1) int userId, @Min(1) int amount, @NotNull final Currency currency) {

        long start = System.currentTimeMillis();
        LOGGER.debug("\nRequesting WITHDRAW for user {}, {}{}", userId, currency, amount);
        WalletOperationWithdraw walletOperationWithdraw
                = WalletOperationWithdraw.newBuilder()
                                         .setUserId(userId)
                                         .setAmount(amount)
                                         .setCurrency(currency.getCurrencyCode())
                                         .build();

        WalletOperationResult walletOperationResult = walletServiceBlockingStub
                .withDeadlineAfter(OPERATION_TIMEOUT, TimeUnit.MILLISECONDS)
                .withdraw(walletOperationWithdraw);

        LOGGER.debug("\nResponded on WITHDRAW in {}ms: {}", getDuration(start), walletOperationResult.getMessage());

        return walletOperationResult.getMessage();
    }

    @Override
    public Map<String, Integer> balance(@Min(1) int userId) {

        long start = System.currentTimeMillis();
        LOGGER.debug("\nRequesting BALANCE for user {}", userId);
        WalletBalance walletBalanceRequest = WalletBalance.newBuilder().setUserId(userId).build();

        WalletBalanceResult balances = walletServiceBlockingStub
                .withDeadlineAfter(OPERATION_TIMEOUT, TimeUnit.MILLISECONDS)
                .balance(walletBalanceRequest);

        LOGGER.debug("\nResponded BALANCE in {}ms: USD{}, EUR{}, GBP{}", getDuration(start),
                balances.getAmountsMap().get(USD_CODE),
                balances.getAmountsMap().get(EUR_CODE),
                balances.getAmountsMap().get(GBP_CODE));

        return balances.getAmountsMap();
    }
}
