package rdublin.wallet.server.domain;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Wallet")
public class Wallet {
    @Id
    @Column(nullable = false, unique = true)
    private Integer userId;
    @Column(nullable = false)
    private Integer usdBalance = 0;
    private Integer eurBalance = 0;
    private Integer gbpBalance = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return userId.equals(wallet.userId);
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUsdBalance() {
        return usdBalance;
    }

    public void setUsdBalance(Integer usdBalance) {
        this.usdBalance = usdBalance;
    }

    public Integer getEurBalance() {
        return eurBalance;
    }

    public void setEurBalance(Integer eurBalance) {
        this.eurBalance = eurBalance;
    }

    public Integer getGbpBalance() {
        return gbpBalance;
    }

    public void setGbpBalance(Integer gbpBalance) {
        this.gbpBalance = gbpBalance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
