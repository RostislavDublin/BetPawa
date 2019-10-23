package rdublin.wallet.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class WalletClientAsyncConfiguration {

    public static final int MAX_POOL_SIZE = 16;
    public static final int CORE_POOL_SIZE = 16;
    public static final int QUEUE_CAPACITY = Integer.MAX_VALUE;
    private static final Logger LOGGER = LoggerFactory.getLogger(WalletClientAsyncConfiguration.class);

    @Bean(name = "walletOperationsTaskExecutor")
    public TaskExecutor walletOperationsTaskExecutor() {
        LOGGER.debug("Creating Async Task Executor");
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }
}
