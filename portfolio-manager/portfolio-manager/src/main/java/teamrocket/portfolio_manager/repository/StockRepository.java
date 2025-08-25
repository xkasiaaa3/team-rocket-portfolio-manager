package teamrocket.portfolio_manager.repository;


import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import teamrocket.portfolio_manager.entity.Stock;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {
    Optional<Stock> findByStockSymbol(String symbol);
    List<Stock> findByCurrentPriceIsNotNull();

    @Query(value = "INSERT INTO stock(stock_symbol, stock_name,currency, current_price) " +
            "SELECT ticker, company, 'USD', 0 " +
            "FROM CSVREAD('portfolio-manager/data/fortune1000_2024.csv', NULL, 'charset=UTF-8') " +
            "WHERE ticker IS NOT NULL;", nativeQuery = true)
    @Modifying
    @Transactional
    void updateStocksFromCsv();
}
