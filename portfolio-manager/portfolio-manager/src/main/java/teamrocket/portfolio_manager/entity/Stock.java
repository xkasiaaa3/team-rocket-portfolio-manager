package teamrocket.portfolio_manager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import javax.annotation.processing.Generated;
import java.math.BigDecimal;
import java.util.List;

@Entity
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String stockSymbol;
    private String stockName;
    private String currency;
    @Positive
    private BigDecimal currentPrice;

//    @OneToMany(mappedBy = "stockId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
//    private List<StockTransaction> stockTransactions;

//    @OneToMany(mappedBy = "stockId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
//    private List<StockHistory> stockHistory;

    public Stock() {}
    public Stock(String stockSymbol, String stockName, String currency, BigDecimal currentPrice) {
        this.stockSymbol = stockSymbol;
        this.stockName = stockName;
        this.currency = currency;
        this.currentPrice = currentPrice;
    }

    public Stock(Integer id, String stockSymbol, String stockName, String currency, BigDecimal currentPrice) {
        this.id = id;
        this.stockSymbol = stockSymbol;
        this.stockName = stockName;
        this.currency = currency;
        this.currentPrice = currentPrice;
    }

    public Integer getId() {
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
