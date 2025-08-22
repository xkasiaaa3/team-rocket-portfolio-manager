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
    private String name;
    @Positive
    private BigDecimal balance;

    @OneToMany(mappedBy = "portfolioId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<StockTransaction> stockTransactions;

    public Portfolio() {}
    public Portfolio(String name, BigDecimal balance) {
        this.name = name;
        this.balance = balance;
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
