package rdublin.wallet.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WalletServerApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(WalletServerApplication.class, args);
    }
}
