package rdublin.wallet.server.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rdublin.wallet.server.domain.Wallet;

@Repository
public interface WalletRepository extends CrudRepository<Wallet, Integer> {
}
