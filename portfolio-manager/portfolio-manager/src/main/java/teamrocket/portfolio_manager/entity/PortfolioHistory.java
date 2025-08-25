package teamrocket.portfolio_manager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Date;

@Entity
public class PortfolioHistory {
    @Id
    @GeneratedValue
    private Integer id;
    private Integer portfolioId;
    Date date;
    @Positive
    BigDecimal netWorth;

    public PortfolioHistory() {
    }
    public PortfolioHistory(BigDecimal netWorth, Date date) {
        this.netWorth = netWorth;
        this.date = date;
    }

    public BigDecimal getNetWorth() {
        return netWorth;
    }

    public Date getDate() {
        return date;
    }

    public void setNetWorth(BigDecimal netWorth) {
        this.netWorth = netWorth;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
