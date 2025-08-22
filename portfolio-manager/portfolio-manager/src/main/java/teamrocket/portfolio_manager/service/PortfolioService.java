package teamrocket.portfolio_manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import teamrocket.portfolio_manager.entity.Stock;
import teamrocket.portfolio_manager.entity.StockTransaction;
import teamrocket.portfolio_manager.exception.NotEnoughStocksException;
import teamrocket.portfolio_manager.exception.StockNotFoundException;
import teamrocket.portfolio_manager.model.Action;
import teamrocket.portfolio_manager.repository.StockRepository;
import teamrocket.portfolio_manager.repository.StockTransactionRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class PortfolioService {

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private StockTransactionRepository stockTransactionRepository;

    public PortfolioService(StockRepository stockRepository, StockTransactionRepository stockTransactionRepository) {
        this.stockRepository = stockRepository;
        this.stockTransactionRepository = stockTransactionRepository;
    }

    StockTransaction buyStock(Integer stockId, BigDecimal amount) {
        Stock stock = stockRepository.findById(stockId).orElseThrow(() -> new StockNotFoundException(stockId));
        StockTransaction stockTransaction = stockTransactionRepository.save(new StockTransaction(stockId, new Date(), amount, Action.BUYING, stock.getCurrentPrice()));
        return stockTransaction;
    }

    StockTransaction sellStock(Integer stockId, BigDecimal amount) {
        Stock stock = stockRepository.findById(stockId).orElseThrow(() -> new StockNotFoundException(stockId));
        checkStockAmount(stockId, amount);
        StockTransaction stockTransaction = stockTransactionRepository.save(new StockTransaction(stockId, new Date(), amount, Action.SELLING, stock.getCurrentPrice()));
        return stockTransaction;
    }

    private void checkStockAmount(Integer stockId, BigDecimal amount) {
        List<StockTransaction> transactions = stockTransactionRepository.findAllByStockId(stockId);
        BigDecimal sold = transactions.stream().filter(t -> t.getAction() == Action.SELLING).map(StockTransaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal bought = transactions.stream().filter(t -> t.getAction() == Action.BUYING).map(StockTransaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (bought.subtract(sold).compareTo(amount) < 0) {
            throw new NotEnoughStocksException(stockId);
        }
    }
}
