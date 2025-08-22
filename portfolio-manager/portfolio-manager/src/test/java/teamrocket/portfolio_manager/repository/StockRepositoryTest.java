package teamrocket.portfolio_manager.repository;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import teamrocket.portfolio_manager.entity.Stock;

import java.math.BigDecimal;
import java.util.List;

@DataJpaTest
public class StockRepositoryTest {

    @Mock
    StockRepository stockRepository;

    @Test
    public void testAddStock() {
        List<Stock> stocks = stockRepository.findAll();
        Assertions.assertEquals(0, stocks.size(), "Initial list is empty");
        Stock newStock1 = new Stock("NSTK", "New Stock 1", "USD", new BigDecimal("123.45"));
        Stock newStock2 = new Stock("NSTM", "New Stock 2", "USD", new BigDecimal("124.45"));
        Stock newStock3 = new Stock("NSTN", "New Stock 3", "USD", new BigDecimal("125.45"));
        Stock newStock4 = new Stock("NSTO", "New Stock 4", "USD", new BigDecimal("126.45"));
        stockRepository.save(newStock1);
        stockRepository.save(newStock2);
        stockRepository.save(newStock3);
        stockRepository.save(newStock4);
        stocks = stockRepository.findAll();
        Assertions.assertEquals(4, stocks.size(), "List has 4 items");
    }
}
