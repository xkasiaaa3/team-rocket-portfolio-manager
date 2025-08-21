package teamrocket.portfolio_manager.entity;

import jakarta.persistence.*;

import javax.annotation.processing.Generated;
import java.math.BigDecimal;
import java.util.List;

@Entity
public class Stock {
    @Id
    @GeneratedValue
    private Integer id;
    private String stockSymbol;
    private String stockName;
    private String currency;
    private BigDecimal currentPrice;

    @OneToMany(mappedBy = "stockId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<StockTransaction> stockTransactions;

    @OneToMany(mappedBy = "stockId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<StockHistory> stockHistory;

    public Stock() {}
    public Stock(String stockSymbol, String stockName, String currency, BigDecimal currentPrice) {
        this.stockSymbol = stockSymbol;
        this.stockName = stockName;
        this.currency = currency;
        this.currentPrice = currentPrice;
    }

    public int getId() {
        return id;
    }
    public String getStockSymbol() {
        return stockSymbol;
    }
    public String getStockName() {
        return stockName;
    }
    public String getCurrency() {
        return currency;
    }
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
}
