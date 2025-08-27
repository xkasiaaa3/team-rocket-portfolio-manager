package teamrocket.portfolio_manager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import teamrocket.portfolio_manager.model.Action;
import teamrocket.portfolio_manager.model.StockTransactionWithChangeDTO;

import java.math.BigDecimal;
import java.util.Date;

@Entity
public class StockTransaction {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;
    @ManyToOne
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;
    private Date date;
    @Positive
    private BigDecimal amount;
    private Action action;

    public BigDecimal getActionPrice() {
        return actionPrice;
    }

    private BigDecimal actionPrice;

    public StockTransaction(Stock stock, Portfolio portfolio, Date date, BigDecimal amount, Action action, BigDecimal actionPrice) {
        this.stock = stock;
        this.portfolio = portfolio;
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

    public Stock getStock() {
        return stock;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public StockTransactionWithChangeDTO toDTO(){
        return new StockTransactionWithChangeDTO(id, stock,date,amount,action,actionPrice);
    }
}
