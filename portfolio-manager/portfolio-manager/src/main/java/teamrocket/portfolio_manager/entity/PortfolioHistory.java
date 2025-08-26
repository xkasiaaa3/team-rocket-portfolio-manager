package teamrocket.portfolio_manager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Positive;
import teamrocket.portfolio_manager.model.PortfolioNetworthDTO;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class PortfolioHistory {
    @Id
    @GeneratedValue
    private Integer id;
    private Integer portfolioId;
    Date date;
    @Positive
    BigDecimal networth;

    public PortfolioHistory() {
    }
    public PortfolioHistory(BigDecimal networth, Date date) {
        this.networth = networth;
        this.date = date;
    }

    public BigDecimal getNetworth() {
        return networth;
    }

    public Date getDate() {
        return date;
    }

    public void setNetWorth(BigDecimal netWorth) {
        this.networth = netWorth;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public PortfolioNetworthDTO toPortfolioNetworthDTO() {
        return new PortfolioNetworthDTO(date, networth);
    }
}
