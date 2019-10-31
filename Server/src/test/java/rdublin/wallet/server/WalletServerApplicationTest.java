package rdublin.wallet.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.SpringApplication;

import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SpringApplication.class, WalletServerApplication.class})
public class WalletServerApplicationTest extends TestBaseServer {

    @Test
    public void whenWalletServerApplication_thenSpringApplicationRun() {
        spy(WalletServerApplication.class);
        mockStatic(SpringApplication.class);

        WalletServerApplication.main(new String[]{});
        verifyStatic(SpringApplication.class);
        SpringApplication.run(WalletServerApplication.class, new String[]{});
    }
}