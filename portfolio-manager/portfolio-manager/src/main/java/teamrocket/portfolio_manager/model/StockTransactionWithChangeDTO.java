package teamrocket.portfolio_manager.model;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Positive;
import teamrocket.portfolio_manager.entity.Portfolio;
import teamrocket.portfolio_manager.entity.Stock;
import teamrocket.portfolio_manager.entity.StockHistory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

public class StockTransactionWithChangeDTO {
    private Integer id;
    private Stock stock;
    private Date date;
    private BigDecimal amount;
    private Action action;
    private BigDecimal actionPrice;
    private double change;

    public StockTransactionWithChangeDTO(Integer id, Stock stock, Date date, BigDecimal amount, Action action, BigDecimal actionPrice) {
        this.id = id;
        this.stock = stock;
        this.date = date;
        this.amount = amount;
        this.action = action;
        this.actionPrice = actionPrice;

        BigDecimal firstValue;
        BigDecimal secondValue;
        if (action == Action.BUYING) {
            firstValue = stock.getCurrentPrice();
            secondValue = actionPrice;
        } else {
            firstValue = actionPrice;
            secondValue = stock.getCurrentPrice();
        }
        this.change = (firstValue.doubleValue() - secondValue.doubleValue()) / secondValue.doubleValue();

    }

    public Integer getId() {
        return id;
    }

    public Stock getStock() {
        return stock;
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

    public BigDecimal getActionPrice() {
        return actionPrice;
    }

    public double getChange() {
        return change;
    }
}
