package rdublin.wallet.server.services;

import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import rdublin.wallet.grpc.WalletBalance;
import rdublin.wallet.grpc.WalletBalanceResult;
import rdublin.wallet.grpc.WalletOperationDeposit;
import rdublin.wallet.grpc.WalletOperationResult;
import rdublin.wallet.grpc.WalletOperationWithdraw;
import rdublin.wallet.grpc.WalletServiceGrpc;

import static rdublin.utils.CurrencyUtils.*;
import static rdublin.utils.MetricsUtils.getDuration;
import static rdublin.wallet.server.services.WalletServiceImpl.*;

@GRpcService
public class WalletServerServiceImpl extends WalletServiceGrpc.WalletServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletServerServiceImpl.class);
    public static final WalletOperationResult OK_WALLET_OPERATION_RESULT;
    private static final WalletOperationResult INSUFFICIENT_FUNDS_WALLET_OPERATION_RESULT;
    private static final WalletOperationResult UNKNOWN_CURRENCY_WALLET_OPERATION_RESULT;
    private static final WalletOperationResult UNKNOWN_ERROR_WALLET_OPERATION_RESULT;

    static {
        OK_WALLET_OPERATION_RESULT =
                WalletOperationResult.newBuilder().setMessage(OK_MESSAGE).build();
        INSUFFICIENT_FUNDS_WALLET_OPERATION_RESULT =
                WalletOperationResult.newBuilder().setMessage(INSUFFICIENT_FUNDS_MESSAGE).build();
        UNKNOWN_CURRENCY_WALLET_OPERATION_RESULT =
                WalletOperationResult.newBuilder().setMessage(UNKNOWN_CURRENCY_MESSAGE).build();
        UNKNOWN_ERROR_WALLET_OPERATION_RESULT =
                WalletOperationResult.newBuilder().setMessage(UNKNOWN_ERROR_MESSAGE).build();
    }

    @Autowired
    private WalletService walletService;

    @Override
    public void withdraw(WalletOperationWithdraw request,
                         StreamObserver<WalletOperationResult> responseObserver) {
        long start = System.currentTimeMillis();
        LOGGER.debug("\nRequested WITHDRAW for user {}, {}{}",
                request.getUserId(), request.getCurrency(), request.getAmount());

        String resultMessage = walletService.withdraw(request.getUserId(), request.getAmount(), request.getCurrency());
        WalletOperationResult walletOperationResult = getResultByResultMessage(resultMessage);

        responseObserver.onNext(walletOperationResult);
        responseObserver.onCompleted();
        LOGGER.debug("\nResponding on WITHDRAW in {}ms: {}", getDuration(start), walletOperationResult.getMessage());
    }

    @Override
    public void deposit(WalletOperationDeposit request, StreamObserver<WalletOperationResult> responseObserver) {
        long start = System.currentTimeMillis();
        LOGGER.debug("\nRequested DEPOSIT for user {}, {}{}",
                request.getUserId(), request.getCurrency(), request.getAmount());

        String resultMessage = walletService.deposit(request.getUserId(), request.getAmount(), request.getCurrency());
        WalletOperationResult walletOperationResult = getResultByResultMessage(resultMessage);

        responseObserver.onNext(walletOperationResult);
        responseObserver.onCompleted();
        LOGGER.debug("\nResponding on DEPOSIT in {}ms: {}", getDuration(start), walletOperationResult.getMessage());
    }

    @Override
    public void balance(WalletBalance request, StreamObserver<WalletBalanceResult> responseObserver) {
        long start = System.currentTimeMillis();
        LOGGER.debug("\nRequested BALANCE for user {}", request.getUserId());

        WalletBalanceResult balances;
        balances = WalletBalanceResult.newBuilder().putAllAmounts(walletService.balance(request.getUserId())).build();

        responseObserver.onNext(balances);
        responseObserver.onCompleted();

        LOGGER.debug("\nResponding BALANCE in {}ms: USD{}, EUR{}, GBP{}", getDuration(start),
                balances.getAmountsMap().get(USD_CODE),
                balances.getAmountsMap().get(EUR_CODE),
                balances.getAmountsMap().get(GBP_CODE));
    }

    private WalletOperationResult getResultByResultMessage(String resultMessage) {
        switch (resultMessage) {
            case OK_MESSAGE:
                return OK_WALLET_OPERATION_RESULT;
            case INSUFFICIENT_FUNDS_MESSAGE:
                return INSUFFICIENT_FUNDS_WALLET_OPERATION_RESULT;
            case UNKNOWN_CURRENCY_MESSAGE:
                return UNKNOWN_CURRENCY_WALLET_OPERATION_RESULT;
            default:
                return UNKNOWN_ERROR_WALLET_OPERATION_RESULT;
        }
    }
}
