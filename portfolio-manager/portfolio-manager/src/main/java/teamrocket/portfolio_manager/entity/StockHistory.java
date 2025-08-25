package teamrocket.portfolio_manager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Date;

@Entity
public class StockHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private Integer stockId;
    private Date date;
    @Positive
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
