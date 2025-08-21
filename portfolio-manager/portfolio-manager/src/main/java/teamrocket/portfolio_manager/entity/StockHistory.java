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
    int id;
    Date date;
    BigDecimal price;


}
