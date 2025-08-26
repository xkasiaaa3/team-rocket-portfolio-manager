package teamrocket.portfolio_manager.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import teamrocket.portfolio_manager.entity.StockHistory;

import java.util.Date;
import java.util.List;

@Repository
public interface StockHistoryRepository extends JpaRepository<StockHistory, Integer> {
    List<StockHistory> findAllByStockId(Integer stock_id);
    StockHistory findByStockIdAndDate(int id, Date date);

    boolean existsByDate(Date date);
}
