package teamrocket.portfolio_manager.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import teamrocket.portfolio_manager.entity.PortfolioHistory;
import teamrocket.portfolio_manager.entity.StockHistory;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockHistoryRepository extends JpaRepository<StockHistory, Integer> {
    List<StockHistory> findAllByStockIdAndDateBetween(Integer stockId, Date startDate, Date endDate);

    StockHistory findByStockIdAndDate(int id, Date date);

    Optional<StockHistory> findFirstByStockIdAndDateLessThanEqualOrderByDateDesc(Integer stockId, Date date);

    boolean existsByDate(Date date);
    @Query("SELECT sh FROM StockHistory sh WHERE sh.date < :currentDate ORDER BY sh.stock.id, sh.date DESC")
    List<StockHistory> findAllStockHistoriesBeforeDateSorted(@Param("currentDate") Date currentDate);
}
