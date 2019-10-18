package rdublin.wallet.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rdublin.wallet.client.grpc.WalletClient;

import java.util.Currency;

@SpringBootApplication
public class WalletClientApplication implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletClientApplication.class);
    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency GBP = Currency.getInstance("GBP");

    @Autowired
    private WalletClient walletClient;

    public static void main(String[] args) {
        SpringApplication.run(WalletClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.debug("Perform wallet operations");
        walletClient.deposit(1, 100, USD);
        walletClient.withdraw(1, 100, EUR);
        walletClient.balance(1);
        walletClient.withdraw(1, 1000, EUR);
    }
}
