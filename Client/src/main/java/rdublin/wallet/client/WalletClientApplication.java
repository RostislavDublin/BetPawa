package rdublin.wallet.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WalletClientApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletClientApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(WalletClientApplication.class, args);
    }
}
