package rdublin.wallet.client.services.bulk;

import rdublin.wallet.client.services.WalletClientService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import static rdublin.wallet.client.services.WalletClientShellComponent.EUR;
import static rdublin.wallet.client.services.WalletClientShellComponent.USD;

public class WalletBulkOperationsRoundA implements WalletBulkOperationsRound {
    @Override
    public List<BiFunction<WalletClientService, Integer, String>> operations() {
        List<BiFunction<WalletClientService, Integer, String>> operations = new ArrayList<>();

        operations.add((wcs, userId) -> wcs.deposit(userId, 100, USD));
        operations.add((wcs, userId) -> wcs.withdraw(userId, 200, USD));
        operations.add((wcs, userId) -> wcs.deposit(userId, 100, EUR));
        operations.add((wcs, userId) -> wcs.balance(userId).toString());
        operations.add((wcs, userId) -> wcs.withdraw(userId, 100, USD));
        operations.add((wcs, userId) -> wcs.balance(userId).toString());
        operations.add((wcs, userId) -> wcs.withdraw(userId, 100, USD));

        return operations;
    }

    @Override
    public Character getCode() {
        return 'A';
    }
}
