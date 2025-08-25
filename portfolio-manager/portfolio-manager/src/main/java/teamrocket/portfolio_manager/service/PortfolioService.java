package teamrocket.portfolio_manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import teamrocket.portfolio_manager.entity.*;
import teamrocket.portfolio_manager.exception.*;
import teamrocket.portfolio_manager.model.Action;
import teamrocket.portfolio_manager.model.TransactionDTO;
import teamrocket.portfolio_manager.repository.*;

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
    private StockHistoryRepository stockHistoryRepository;
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

    public BigDecimal updatePortfolioBalance(Integer portfolioId, BigDecimal changeInBalance) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> new PortfolioNotFoundException(portfolioId));
        portfolio.setBalance(portfolio.getBalance().add(changeInBalance));
        portfolioRepository.save(portfolio);
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
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> new PortfolioNotFoundException(portfolioId));
        checkEnoughBalance(stock.getCurrentPrice(), amount, portfolio.getBalance());
        StockTransaction stockTransaction = stockTransactionRepository.save(
                new StockTransaction(stockId, portfolioId, portfolio.getCurrentDate(), amount, Action.BUYING, stock.getCurrentPrice()));
        return stockTransaction;
    }

    StockTransaction sellStock(Integer stockId, Integer portfolioId, BigDecimal amount) {
        Stock stock = stockRepository.findById(stockId).orElseThrow(() -> new StockNotFoundException(stockId));
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> new PortfolioNotFoundException(portfolioId));
        checkStockAmount(stockId, portfolioId, amount);
        StockTransaction stockTransaction = stockTransactionRepository.save(new StockTransaction(
                stockId, portfolioId, portfolio.getCurrentDate(), amount, Action.SELLING, stock.getCurrentPrice()));
        return stockTransaction;
    }

    private BigDecimal checkStockAmount(Integer stockId, Integer portfolioId, BigDecimal amount) {
        List<StockTransaction> transactions = stockTransactionRepository.findByPortfolioIdAndStockId(portfolioId, stockId);
        BigDecimal sold = transactions.stream().filter(t -> t.getAction() == Action.SELLING).map(StockTransaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal bought = transactions.stream().filter(t -> t.getAction() == Action.BUYING).map(StockTransaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal amountOwned = bought.subtract(sold);
        if (amountOwned.compareTo(amount) < 0) {
            throw new NotEnoughStocksException(stockId);
        }
        return amountOwned;
    }

    private void checkEnoughBalance(BigDecimal currentPrice, BigDecimal amount, BigDecimal balance) {
        BigDecimal price = currentPrice.multiply(amount);
        if (balance.compareTo(price) < 0) {
            throw new NotEnoughBalanceException();
        }
    }

    public List<Stock> getPortfolioStocks(Integer portfolioId) {
        List<StockTransaction> transactions = stockTransactionRepository.findByPortfolioId(portfolioId);
        HashSet<Integer> portfolioStockIds = new HashSet<>();
        transactions.forEach(t -> portfolioStockIds.add(t.getStockId()));
        List<Stock> stocks = new ArrayList<>();
        portfolioStockIds.forEach(i -> {
            if (checkStockAmount(i, portfolioId, BigDecimal.ZERO).compareTo(BigDecimal.ZERO) > 0) {
                stocks.add(stockRepository.findById(i).orElseThrow(() -> new StockNotFoundException(i)));
            }
        });
        return stocks;
    }

    public BigDecimal getPortfolioNetworth(Integer portfolioId) {
        BigDecimal netWorth = BigDecimal.ZERO;
        List<Stock> stocks = getPortfolioStocks(portfolioId);
        for (Stock s : stocks) {
            netWorth = netWorth.add(checkStockAmount(s.getId(), portfolioId, BigDecimal.ZERO).multiply(s.getCurrentPrice()));
        }
        return netWorth;
    }

    public double getPortfolioNetworthChange(Integer portfolioId) {
        // FIX FOR PREVIOUS DAY IS WEEKEND
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> new PortfolioNotFoundException(portfolioId));

        int dateDifference = 1;

        Date currentDate = portfolio.getCurrentDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, -dateDifference);
        Date previousDate = calendar.getTime();

        BigDecimal currentNetworth = getPortfolioNetworth(portfolioId);
        PortfolioHistory previousPortfolioHistory = portfolioHistoryRepository.findByPortfolioIdAndDate(portfolioId, previousDate);
        BigDecimal previousNetworth = previousPortfolioHistory.getNetworth();

        return (currentNetworth.doubleValue() - previousNetworth.doubleValue()) / previousNetworth.doubleValue();
    }

    public void updatePortfolioHistory(Integer portfolioId) {
        BigDecimal networth = getPortfolioNetworth(portfolioId);
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> new PortfolioNotFoundException(portfolioId));
        portfolioHistoryRepository.save(new PortfolioHistory(networth, portfolio.getCurrentDate()));
    }

    public Date forwardDayAndUpdateValues(Integer portfolioId) {
        // Add today's networth to the portfolio history, then move forward one day and update stock prices
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> new PortfolioNotFoundException(portfolioId));
        updatePortfolioHistory(portfolioId);
        portfolio.forwardNextWeekDay();
        portfolioRepository.save(portfolio);
        List<Stock> stocks = stockRepository.findAll();
        for (Stock s : stocks) {
            StockHistory stockHistory = stockHistoryRepository.findByStockIdAndDate(s.getId(), portfolio.getCurrentDate());
            if (stockHistory != null) s.setCurrentPrice(stockHistory.getPrice());
        }
        stockRepository.saveAll(stocks);
        return portfolio.getCurrentDate();
    }

    public StockTransaction makeTransaction(Integer portfolioId, TransactionDTO transactionDTO) {
        Integer stockId = transactionDTO.getStockId();
        BigDecimal amount = transactionDTO.getAmount();
        switch (transactionDTO.getAction()) {
            case BUYING -> {
                return buyStock(stockId, portfolioId, amount);
            }
            case SELLING -> {
                return sellStock(stockId, portfolioId, amount);
            }
            default -> throw new RuntimeException();
        }
    }
}
