package teamrocket.portfolio_manager.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import teamrocket.portfolio_manager.entity.Stock;
import teamrocket.portfolio_manager.entity.StockTransaction;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Integer> {
}
