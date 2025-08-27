package teamrocket.portfolio_manager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.springframework.beans.factory.annotation.Value;
import teamrocket.portfolio_manager.service.PortfolioService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
public class Portfolio {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private BigDecimal balance;
    @OneToMany(mappedBy = "portfolioId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PortfolioHistory> portfolioHistories;

    private Date currentPortfolioDate;

    public Portfolio(Integer id, String name, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public Date getCurrentDate() {
        return currentPortfolioDate;
    }

    public Date forwardNextWeekDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentPortfolioDate);
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        currentPortfolioDate = calendar.getTime();

        return currentPortfolioDate;
    }

    public Portfolio() {}
    public Portfolio(String name, BigDecimal balance) {
        this.name = name;
        this.balance = balance;
        this.currentPortfolioDate = Date.from(Instant.parse("2015-02-02T00:00:00Z"));
    }

    public Integer getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public List<PortfolioHistory> getPortfolioHistories() {
        return portfolioHistories;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    public void addBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
    public void subtractBalance(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }
    public void setPortfolioHistories(List<PortfolioHistory> portfolioHistories) {
        this.portfolioHistories = portfolioHistories;
    }
}
