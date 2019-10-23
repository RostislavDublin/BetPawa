package rdublin.wallet.client.services.bulk;

import rdublin.wallet.client.services.WalletClientService;

import java.util.List;
import java.util.function.BiFunction;

public interface WalletBulkOperationsRound {
    List<BiFunction<WalletClientService, Integer, String>> operations();

    Character getCode();
}
