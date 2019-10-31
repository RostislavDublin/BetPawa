package rdublin.wallet.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.SpringApplication;

import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SpringApplication.class, WalletClientApplication.class})
public class WalletClientApplicationTest {

    @Test
    public void whenWalletServerApplication_thenSpringApplicationRun() {
        spy(WalletClientApplication.class);
        mockStatic(SpringApplication.class);

        WalletClientApplication.main(new String[]{});
        verifyStatic(SpringApplication.class);
        SpringApplication.run(WalletClientApplication.class, new String[]{});
    }
}