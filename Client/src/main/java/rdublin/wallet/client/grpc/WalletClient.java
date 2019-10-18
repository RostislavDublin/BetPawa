package rdublin.wallet.client.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rdublin.wallet.grpc.WalletBalance;
import rdublin.wallet.grpc.WalletBalanceResult;
import rdublin.wallet.grpc.WalletOperationConfirmation;
import rdublin.wallet.grpc.WalletOperationDeposit;
import rdublin.wallet.grpc.WalletOperationWithdraw;
import rdublin.wallet.grpc.WalletServiceGrpc;
import sun.jvm.hotspot.utilities.Assert;

import javax.annotation.PostConstruct;
import java.util.Currency;
import java.util.Map;
import java.util.Objects;

@Component
public class WalletClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(WalletClient.class);

    private WalletServiceGrpc.WalletServiceBlockingStub walletServiceBlockingStub;

    @PostConstruct
    private void init() {
        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 6565).usePlaintext().build();

        walletServiceBlockingStub = WalletServiceGrpc.newBlockingStub(managedChannel);
    }

    public String deposit(final int userId, final int amount, final Currency currency) {

        Objects.requireNonNull(currency, "Non-empty currency required");
        Assert.that(amount > 0, "Positive amount required");

        WalletOperationDeposit walletOperationDeposit
                = WalletOperationDeposit.newBuilder()
                                        .setUserId(1)
                                        .setAmount(amount)
                                        .setCurrency(currency.getCurrencyCode())
                                        .build();

        LOGGER.debug("\nTry DEPOSIT\n{}", walletOperationDeposit);
        WalletOperationConfirmation confirmation = walletServiceBlockingStub.deposit(walletOperationDeposit);
        LOGGER.debug("\nRes.DEPOSIT\n{}", confirmation);

        return confirmation.getMessage();
    }

    public String withdraw(final int userId, final int amount, final Currency currency) {

        Objects.requireNonNull(currency, "Non-empty currency required");
        Assert.that(amount > 0, "Positive amount required");

        WalletOperationWithdraw walletOperationWithdraw
                = WalletOperationWithdraw.newBuilder()
                                         .setUserId(1)
                                         .setAmount(amount)
                                         .setCurrency(currency.getCurrencyCode())
                                         .build();

        LOGGER.debug("\nTry WITHDRAW\n{}", walletOperationWithdraw);
        WalletOperationConfirmation confirmation = walletServiceBlockingStub.withdraw(walletOperationWithdraw);
        LOGGER.debug("\nRes.WITHDRAW\n{}", confirmation);

        return confirmation.getMessage();
    }

    public Map<String, Integer> balance(final int userId) {

        WalletBalance walletBalanceRequest = WalletBalance.newBuilder().setUserId(1).build();

        LOGGER.debug("\nAsk BALANCE\n{}", walletBalanceRequest);
        WalletBalanceResult balance = walletServiceBlockingStub.balance(walletBalanceRequest);
        LOGGER.debug("\nGot BALANCE\n{}", balance);

        return balance.getAmountsMap();
    }

}
