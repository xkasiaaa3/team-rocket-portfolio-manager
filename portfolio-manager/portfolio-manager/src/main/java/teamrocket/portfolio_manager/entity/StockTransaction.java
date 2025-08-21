package teamrocket.portfolio_manager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.annotations.CollectionIdJdbcTypeCode;

import java.math.BigDecimal;
import java.util.Date;

@Entity
public class StockTransaction {
    @Id
    @GeneratedValue
    int id;
    Date date;
    BigDecimal amount;
}
