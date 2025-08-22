package teamrocket.portfolio_manager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import teamrocket.portfolio_manager.service.PortfolioService;

import java.math.BigDecimal;
import java.util.List;

@Entity
public class Portfolio {
    @Id
    @GeneratedValue
    private Integer id;
    @Positive
    private BigDecimal balance;

    @OneToMany(mappedBy = "portfolioId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<StockTransaction> stockTransactions;

    public Portfolio() {}
    public Portfolio(BigDecimal balance, List<StockTransaction> stockTransactions) {
        this.balance = balance;
        this.stockTransactions = stockTransactions;
    }

    public Integer getId() {
        return id;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public List<StockTransaction> getStockTransactions() {
        return stockTransactions;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    public void setStockTransactions(List<StockTransaction> stockTransactions) {
        this.stockTransactions = stockTransactions;
    }
}
