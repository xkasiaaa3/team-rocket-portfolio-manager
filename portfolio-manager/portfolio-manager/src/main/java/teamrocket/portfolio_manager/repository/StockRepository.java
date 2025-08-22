package teamrocket.portfolio_manager.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import teamrocket.portfolio_manager.entity.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {
    Stock findByStockSymbol(String symbol);
}
