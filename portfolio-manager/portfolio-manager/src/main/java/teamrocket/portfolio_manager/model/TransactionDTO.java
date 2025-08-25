package teamrocket.portfolio_manager.model;

import java.math.BigDecimal;

public class TransactionDTO {
    Integer stockId;
    BigDecimal amount;
    Action action;

    public Integer getStockId() {

        return stockId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Action getAction() {
        return action;
    }

    public void setStockId(Integer stockId) {
        this.stockId = stockId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
