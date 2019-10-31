package rdublin.wallet.server.domain;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.Objects;

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Wallet")
public class Wallet {
    @Id
    @Column(nullable = false, unique = true)
    private int userId;
    @Column(nullable = false)
    private int usdBalance = 0;
    private int eurBalance = 0;
    private int gbpBalance = 0;
    @Version
    private int version;

    public Wallet(int userId) {
        this.userId = userId;
    }

    public Wallet() {
    }

    public Wallet(int userId, int usdBalance, int eurBalance, int gbpBalance) {
        this.userId = userId;
        this.usdBalance = usdBalance;
        this.eurBalance = eurBalance;
        this.gbpBalance = gbpBalance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return userId == wallet.userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUsdBalance() {
        return usdBalance;
    }

    public void setUsdBalance(int usdBalance) {
        this.usdBalance = usdBalance;
    }

    public int getEurBalance() {
        return eurBalance;
    }

    public void setEurBalance(int eurBalance) {
        this.eurBalance = eurBalance;
    }

    public int getGbpBalance() {
        return gbpBalance;
    }

    public void setGbpBalance(int gbpBalance) {
        this.gbpBalance = gbpBalance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return String.format("Wallet of user %d: USD%d, EUR%d, GBP%d", userId, usdBalance, eurBalance, gbpBalance);
    }
}
