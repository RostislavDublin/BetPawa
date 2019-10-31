package rdublin.wallet.client;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static rdublin.wallet.client.WalletClientAsyncConfiguration.MAX_POOL_SIZE;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WalletClientAsyncConfiguration.class, ThreadPoolTaskExecutor.class})
public class WalletClientAsyncConfigurationTest {

    @Rule
    public ErrorCollector collector = new ErrorCollector();
    WalletClientAsyncConfiguration walletClientAsyncConfiguration;
    @Mock
    ThreadPoolTaskExecutor executor;

    @Before
    public void setUp() throws Exception {
        walletClientAsyncConfiguration = spy(new WalletClientAsyncConfiguration());
        whenNew(ThreadPoolTaskExecutor.class).withNoArguments().thenReturn(executor);
    }

    @Test
    public void whenConfigWalletOperationsTaskExecutor_thenExecutorConfigsProperly() {
        assertEquals(executor, walletClientAsyncConfiguration.walletOperationsTaskExecutor());
        verify(executor).setMaxPoolSize(MAX_POOL_SIZE);
    }
}