package teamrocket.portfolio_manager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import teamrocket.portfolio_manager.model.Action;

import java.math.BigDecimal;
import java.util.Date;

@Entity
public class StockTransaction {
    @Id
    @GeneratedValue
    private Integer id;
    private Integer stockId;
    private Date date;
    private BigDecimal amount;
    private Action action;

    public StockTransaction(Integer stockId, Date date, BigDecimal amount, Action action) {
        this.stockId = stockId;
        this.date = date;
        this.amount = amount;
        this.action = action;
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

}
