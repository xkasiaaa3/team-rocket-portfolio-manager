package teamrocket.portfolio_manager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Positive;
import teamrocket.portfolio_manager.model.Action;

import java.math.BigDecimal;
import java.util.Date;

@Entity
public class StockTransaction {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Integer stockId;
    private Date date;
    @Positive
    private BigDecimal amount;
    private Action action;

    public BigDecimal getActionPrice() {
        return actionPrice;
    }

    private BigDecimal actionPrice;

    public StockTransaction(Integer stockId, Date date, BigDecimal amount, Action action, BigDecimal actionPrice) {
        this.stockId = stockId;
        this.date = date;
        this.amount = amount;
        this.action = action;
        this.actionPrice = actionPrice;
    }

    public StockTransaction() {
    }

    public Integer getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Action getAction() {
        return action;
    }

    public Integer getStockId() {
        return stockId;
    }
}
