package teamrocket.portfolio_manager.model;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class StockWithAmountDTO {
    private Integer id;
    private String stockSymbol;
    private String stockName;
    private String currency;
    private BigDecimal currentPrice;
    private BigDecimal amount;

    public StockWithAmountDTO(Integer id, String stockSymbol, String stockName, String currency, BigDecimal currentPrice, BigDecimal amount) {
        this.id = id;
        this.stockSymbol = stockSymbol;
        this.stockName = stockName;
        this.currency = currency;
        this.currentPrice = currentPrice;
        this.amount = amount;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void subtractAmount(BigDecimal value) {
        this.amount.subtract(value);
    }

    public void addAmount(BigDecimal value) {
        this.amount.add(value);
    }
}
