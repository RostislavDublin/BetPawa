package rdublin.wallet.client.services.bulk;

import rdublin.wallet.client.services.WalletClientService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import static rdublin.utils.CurrencyUtils.GBP;

public class WalletBulkOperationsRoundB implements WalletBulkOperationsRound {
    @Override
    public List<BiFunction<WalletClientService, Integer, String>> operations() {
        List<BiFunction<WalletClientService, Integer, String>> operations = new ArrayList<>();

        operations.add((wcs, userId) -> wcs.withdraw(userId, 100, GBP));
        operations.add((wcs, userId) -> wcs.deposit(userId, 300, GBP));
        operations.add((wcs, userId) -> wcs.withdraw(userId, 100, GBP));
        operations.add((wcs, userId) -> wcs.withdraw(userId, 100, GBP));
        operations.add((wcs, userId) -> wcs.withdraw(userId, 100, GBP));

        return operations;
    }

    @Override
    public Character getCode() {
        return 'B';
    }

}
