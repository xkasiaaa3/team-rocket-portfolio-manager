package teamrocket.portfolio_manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import teamrocket.portfolio_manager.entity.Portfolio;
import teamrocket.portfolio_manager.entity.PortfolioHistory;
import teamrocket.portfolio_manager.entity.Stock;
import teamrocket.portfolio_manager.entity.StockTransaction;
import teamrocket.portfolio_manager.exception.*;
import teamrocket.portfolio_manager.model.Action;
import teamrocket.portfolio_manager.repository.PortfolioHistoryRepository;
import teamrocket.portfolio_manager.repository.PortfolioRepository;
import teamrocket.portfolio_manager.repository.StockRepository;
import teamrocket.portfolio_manager.repository.StockTransactionRepository;

import javax.sound.sampled.Port;
import java.math.BigDecimal;
import java.util.*;

@Service
public class PortfolioService {

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private StockTransactionRepository stockTransactionRepository;
    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private PortfolioHistoryRepository portfolioHistoryRepository;

    public PortfolioService(StockRepository stockRepository,
                            StockTransactionRepository stockTransactionRepository,
                            PortfolioRepository portfolioRepository) {
        this.stockRepository = stockRepository;
        this.stockTransactionRepository = stockTransactionRepository;
        this.portfolioRepository = portfolioRepository;
    }

    public List<Portfolio> getAllPortfolios() {
        return portfolioRepository.findAll();
    }

    public BigDecimal getPortfolioBalance(Integer portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> new PortfolioNotFoundException(portfolioId));
        return portfolio.getBalance();
    }

    public List<StockTransaction> getPortfolioTransactions(Integer portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> new PortfolioNotFoundException(portfolioId));
        return portfolio.getStockTransactions();
    }

    public List<StockTransaction> getPortfolioTransactionsBySymbol(Integer portfolioId, Integer stockId) {
        return stockTransactionRepository.findByPortfolioIdAndStockId(portfolioId, stockId);
    }

    public List<PortfolioHistory> getPortfolioHistories(Integer portfolioId) {
        return portfolioHistoryRepository.findByPortfolioId(portfolioId);
    }

    StockTransaction buyStock(Integer stockId, Integer portfolioId, BigDecimal amount) {
        Stock stock = stockRepository.findById(stockId).orElseThrow(() -> new StockNotFoundException(stockId));
        StockTransaction stockTransaction = stockTransactionRepository.save(
                new StockTransaction(stockId, portfolioId, new Date(), amount, Action.BUYING, stock.getCurrentPrice()));
        return stockTransaction;
    }

    StockTransaction sellStock(Integer stockId, Integer portfolioId, BigDecimal amount) {
        Stock stock = stockRepository.findById(stockId).orElseThrow(() -> new StockNotFoundException(stockId));
        checkStockAmount(stockId, portfolioId, amount);
        StockTransaction stockTransaction = stockTransactionRepository.save(new StockTransaction(
                stockId, portfolioId, new Date(), amount, Action.SELLING, stock.getCurrentPrice()));
        return stockTransaction;
    }

    private void checkStockAmount(Integer stockId, Integer portfolioId, BigDecimal amount) {
        List<StockTransaction> transactions = stockTransactionRepository.findByPortfolioIdAndStockId(portfolioId, stockId);
        BigDecimal sold = transactions.stream().filter(t -> t.getAction() == Action.SELLING).map(StockTransaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal bought = transactions.stream().filter(t -> t.getAction() == Action.BUYING).map(StockTransaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (bought.subtract(sold).compareTo(amount) < 0) {
            throw new NotEnoughStocksException(stockId);
        }
    }

    public List<Stock> getPortfolioStocks(Integer portfolioId) {
        List<StockTransaction> transactions = stockTransactionRepository.findByPortfolioId(portfolioId);
        HashSet<Integer> portfolioStockIds = new HashSet<>();
        transactions.forEach(t -> portfolioStockIds.add(t.getStockId()));
        List<Stock> stocks = new ArrayList<>();
        portfolioStockIds.forEach(i -> stocks.add(stockRepository.findById(i).orElseThrow(() -> new StockNotFoundException(i))));
        return stocks;
    }
}
