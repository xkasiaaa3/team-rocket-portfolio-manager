package teamrocket.portfolio_manager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.util.Date;

@Entity
public class StockHistory {
    @Id
    @GeneratedValue
    private Integer id;
    private Integer stockId;
    private Date date;
    private BigDecimal price;

    public StockHistory(Integer id, Date date, BigDecimal price) {
        this.id = id;
        this.date = date;
        this.price = price;
    }

    public StockHistory() {
    }

    public Integer getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
