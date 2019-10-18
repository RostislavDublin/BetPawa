package rdublin.wallet.server.service;

import com.google.type.Money;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rdublin.wallet.grpc.WalletBalance;
import rdublin.wallet.grpc.WalletBalanceResult;
import rdublin.wallet.grpc.WalletOperationConfirmation;
import rdublin.wallet.grpc.WalletOperationDeposit;
import rdublin.wallet.grpc.WalletOperationWithdraw;
import rdublin.wallet.grpc.WalletServiceGrpc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@GRpcService
public class WalletServiceImpl extends WalletServiceGrpc.WalletServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletServiceImpl.class);
    private static final WalletOperationConfirmation OK;
    private static final WalletOperationConfirmation INSUFFICIENT_FUNDS;
    private static final WalletOperationConfirmation UNCKNOWN_CURRENCY;
    private static final Set<String> KNOWN_CURRENCY_CODES = new HashSet(Arrays.asList("USD", "EUR", "GBP"));

    static {
        OK = WalletOperationConfirmation.newBuilder().setMessage("OK").build();
        INSUFFICIENT_FUNDS = WalletOperationConfirmation.newBuilder().setMessage("Insufficient funds").build();
        UNCKNOWN_CURRENCY = WalletOperationConfirmation.newBuilder().setMessage("Unknown currency").build();
    }

    /**
     * @param request
     * @param responseObserver
     */
    @Override
    public void withdraw(WalletOperationWithdraw request,
                         StreamObserver<WalletOperationConfirmation> responseObserver) {
        LOGGER.debug("\nRequested WITHDRAW:\n{}", request);
        WalletOperationConfirmation confirmation;
        if (request.getAmount() > 500) {
            confirmation = INSUFFICIENT_FUNDS;
        } else if (!KNOWN_CURRENCY_CODES.contains(request.getCurrency())) {
            confirmation = UNCKNOWN_CURRENCY;
        } else {
            confirmation = OK;
        }

        responseObserver.onNext(confirmation);
        responseObserver.onCompleted();
        LOGGER.debug("\nResponded on WITHDRAW:\n{}", confirmation);
    }

    /**
     * @param request
     * @param responseObserver
     */
    @Override
    public void deposit(WalletOperationDeposit request, StreamObserver<WalletOperationConfirmation> responseObserver) {
        LOGGER.debug("\nRequested DEPOSIT:\n{}", request);
        WalletOperationConfirmation confirmation;
        if (!KNOWN_CURRENCY_CODES.contains(request.getCurrency())) {
            confirmation = UNCKNOWN_CURRENCY;
        } else {
            confirmation = OK;
        }
        responseObserver.onNext(confirmation);
        responseObserver.onCompleted();
        LOGGER.debug("\nResponded on DEPOSIT:\n{}", confirmation);
    }

    /**
     * @param request
     * @param responseObserver
     */
    @Override
    public void balance(WalletBalance request, StreamObserver<WalletBalanceResult> responseObserver) {
        LOGGER.debug("\nRequested BALANCE:\n{}", request);
        Money balance = Money.newBuilder().setCurrencyCode("1").setUnits(100).setNanos(0).build();
        WalletBalanceResult balances =
                WalletBalanceResult
                        .newBuilder()
                        .putAmounts("USD", 123)
                        .putAmounts("EUR", 456)
                        .putAmounts("GBP", 789)
                        .build();
        responseObserver.onNext(balances);
        responseObserver.onCompleted();
        LOGGER.debug("\nResponded BALANCE:\n{}", balances);
    }
}
