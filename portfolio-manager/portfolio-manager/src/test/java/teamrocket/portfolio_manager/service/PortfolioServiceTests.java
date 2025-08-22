package teamrocket.portfolio_manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import teamrocket.portfolio_manager.entity.Stock;
import teamrocket.portfolio_manager.entity.StockTransaction;
import teamrocket.portfolio_manager.exception.NotEnoughStocksException;
import teamrocket.portfolio_manager.model.Action;
import teamrocket.portfolio_manager.repository.StockRepository;
import teamrocket.portfolio_manager.repository.StockTransactionRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PortfolioServiceTests {

    private StockRepository stockRepository;
    private StockTransactionRepository stockTransactionRepository;
    private PortfolioService portfolioService;

    @BeforeEach
    void setUp() {
        stockRepository = Mockito.mock(StockRepository.class);
        stockTransactionRepository = Mockito.mock(StockTransactionRepository.class);
        portfolioService = new PortfolioService(stockRepository, stockTransactionRepository);

        when(stockTransactionRepository.save(any(StockTransaction.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
    }

    @Test
    void buyStock() {
        Stock stock = new Stock(1, "AUL", "Aula super stock", "USD", BigDecimal.valueOf(550));
        when(stockRepository.findById(stock.getId())).thenReturn(Optional.of(stock));

        StockTransaction stockTransaction = portfolioService.buyStock(stock.getId(), BigDecimal.valueOf(5));

        assertEquals(BigDecimal.valueOf(5), stockTransaction.getAmount());
        assertEquals(stock.getId(), stockTransaction.getStockId());
        assertEquals(Action.BUYING, stockTransaction.getAction());
        assertEquals(BigDecimal.valueOf(550), stockTransaction.getActionPrice());

    }

    @Test
    void sellStock() {
        Stock stock = new Stock(1, "AUL", "Aula super stock", "USD", BigDecimal.valueOf(550));
        when(stockRepository.findById(stock.getId())).thenReturn(Optional.of(stock));
        StockTransaction boughtTransaction = new StockTransaction(stock.getId(), new Date(), BigDecimal.valueOf(5), Action.BUYING, BigDecimal.valueOf(550));
        when(stockTransactionRepository.findAllByStockId(stock.getId())).thenReturn(List.of(boughtTransaction));

        StockTransaction stockTransaction = portfolioService.sellStock(stock.getId(), BigDecimal.valueOf(3));

        assertEquals(BigDecimal.valueOf(3), stockTransaction.getAmount());
        assertEquals(stock.getId(), stockTransaction.getStockId());
        assertEquals(Action.SELLING, stockTransaction.getAction());
        assertEquals(BigDecimal.valueOf(550), stockTransaction.getActionPrice());
    }

    @Test
    void notEnoughStockToSell() {
        Stock stock = new Stock(1, "AUL", "Aula super stock", "USD", BigDecimal.valueOf(550));
        when(stockRepository.findById(stock.getId())).thenReturn(Optional.of(stock));
        StockTransaction boughtTransaction = new StockTransaction(stock.getId(), new Date(), BigDecimal.valueOf(5), Action.BUYING, BigDecimal.valueOf(550));
        StockTransaction soldTransaction = new StockTransaction(stock.getId(), new Date(), BigDecimal.valueOf(3), Action.SELLING, BigDecimal.valueOf(550));

        when(stockTransactionRepository.findAllByStockId(stock.getId())).thenReturn(List.of(boughtTransaction,soldTransaction));

        assertThrows(NotEnoughStocksException.class, () -> portfolioService.sellStock(stock.getId(), BigDecimal.valueOf(4)));
    }
}
