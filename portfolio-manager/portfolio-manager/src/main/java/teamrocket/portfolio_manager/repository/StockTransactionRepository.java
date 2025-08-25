package teamrocket.portfolio_manager.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import teamrocket.portfolio_manager.entity.Stock;
import teamrocket.portfolio_manager.entity.StockTransaction;

import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Integer> {
    List<StockTransaction> findAllByStockId(Integer stockId);
    List<StockTransaction> findByPortfolioIdAndStockId(Integer portfolioId, Integer stockId);
    List<StockTransaction> findByPortfolioId(Integer portfolioId);
}
