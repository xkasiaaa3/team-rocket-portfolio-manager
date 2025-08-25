package teamrocket.portfolio_manager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.springframework.beans.factory.annotation.Value;
import teamrocket.portfolio_manager.service.PortfolioService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
public class Portfolio {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @Positive
    private BigDecimal balance;;
    @OneToMany(mappedBy = "portfolioId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<StockTransaction> stockTransactions;
    private Date currentPortfolioDate;

    public Date getCurrentDate() {
        return currentPortfolioDate;
    }

    public Date forwardNextDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentPortfolioDate);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        currentPortfolioDate = calendar.getTime();

        return currentPortfolioDate;
    }

    public Portfolio() {}
    public Portfolio(String name, BigDecimal balance) {
        this.name = name;
        this.balance = balance;
        this.currentPortfolioDate = Date.from(Instant.parse("1988-01-01T00:00:00Z"));
    }

    public Integer getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public List<StockTransaction> getStockTransactions() {
        return stockTransactions;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    public void setStockTransactions(List<StockTransaction> stockTransactions) {
        this.stockTransactions = stockTransactions;
    }
}
