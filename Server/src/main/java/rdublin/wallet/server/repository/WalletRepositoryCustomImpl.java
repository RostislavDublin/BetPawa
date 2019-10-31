package rdublin.wallet.server.repository;

import org.springframework.util.Assert;
import rdublin.wallet.server.domain.Wallet;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class WalletRepositoryCustomImpl implements WalletRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public void persist(Wallet wallet) {
        Assert.notNull(wallet, "Entities must not be null!");
        em.persist(wallet);
    }
}
