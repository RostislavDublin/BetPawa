package rdublin.wallet.server.repository;

import org.springframework.transaction.annotation.Transactional;
import rdublin.wallet.server.domain.Wallet;

public interface WalletRepositoryCustom {
    @Transactional
    void persist(Wallet wallet);
}
