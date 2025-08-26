package teamrocket.portfolio_manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import teamrocket.portfolio_manager.entity.*;
import teamrocket.portfolio_manager.exception.*;
import teamrocket.portfolio_manager.model.Action;
import teamrocket.portfolio_manager.model.PortfolioNetworthDTO;
import teamrocket.portfolio_manager.model.TransactionDTO;
import teamrocket.portfolio_manager.repository.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
        portfolio.addBalance(changeInBalance);
        portfolioRepository.save(portfolio);
        return portfolio.getBalance();
    }

    public List<StockTransaction> getPortfolioTransactions(Integer portfolioId) {
//        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> new PortfolioNotFoundException(portfolioId));
        return stockTransactionRepository.findByPortfolioId(portfolioId);
    }

    public List<StockTransaction> getPortfolioTransactionsBySymbol(Integer portfolioId, Integer stockId) {
        return stockTransactionRepository.findByPortfolioIdAndStockId(portfolioId, stockId);
    }

    public List<PortfolioNetworthDTO> getPortfolioHistoriesForLastMonth(Integer portfolioId) {
        Portfolio currentPortfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> new PortfolioNotFoundException(portfolioId));

        Date currentDate = currentPortfolio.getCurrentDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.MONTH, -1);
        Date monthAgoDate = calendar.getTime();

        List<PortfolioNetworthDTO> portfolioNetworths = new ArrayList<>(
                portfolioHistoryRepository.findAllByPortfolioIdAndDateBetween(portfolioId, monthAgoDate, currentDate)
                        .stream()
                        .map(PortfolioHistory::toPortfolioNetworthDTO)
                        .collect(Collectors.toList())
        );

        portfolioNetworths.add(new PortfolioNetworthDTO(currentDate, getPortfolioNetworth(portfolioId)));
        portfolioNetworths.sort(Comparator.comparing(PortfolioNetworthDTO::getDate));
        return portfolioNetworths;
    }

    StockTransaction buyStock(Integer stockId, Integer portfolioId, BigDecimal amount) {
        Stock stock = stockRepository.findById(stockId).orElseThrow(() -> new StockNotFoundException(stockId));
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> new PortfolioNotFoundException(portfolioId));
        checkEnoughBalance(stock.getCurrentPrice(), amount, portfolio.getBalance());
        StockTransaction stockTransaction = stockTransactionRepository.save(
                new StockTransaction(stock, portfolio, portfolio.getCurrentDate(), amount, Action.BUYING, stock.getCurrentPrice()));
        portfolio.subtractBalance(stock.getCurrentPrice().multiply(amount));
        portfolioRepository.save(portfolio);
        return stockTransaction;
    }

    StockTransaction sellStock(Integer stockId, Integer portfolioId, BigDecimal amount) {
        Stock stock = stockRepository.findById(stockId).orElseThrow(() -> new StockNotFoundException(stockId));
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> new PortfolioNotFoundException(portfolioId));
        checkStockAmount(stockId, portfolioId, amount);
        StockTransaction stockTransaction = stockTransactionRepository.save(new StockTransaction(
                stock, portfolio, portfolio.getCurrentDate(), amount, Action.SELLING, stock.getCurrentPrice()));
        portfolio.addBalance(stock.getCurrentPrice().multiply(amount));
        portfolioRepository.save(portfolio);
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
        transactions.forEach(t -> portfolioStockIds.add(t.getStock().getId()));
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
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> new PortfolioNotFoundException(portfolioId));

        BigDecimal currentNetworth = getPortfolioNetworth(portfolioId);
        Optional<PortfolioHistory> previousPortfolioHistory = portfolioHistoryRepository.findFirstByPortfolioIdAndDateLessThanEqualOrderByDateDesc(portfolioId,portfolio.getCurrentDate());
        BigDecimal previousNetworth = previousPortfolioHistory.isPresent() ? previousPortfolioHistory.get().getNetworth() : currentNetworth;

        return (currentNetworth.doubleValue() - previousNetworth.doubleValue()) / previousNetworth.doubleValue();
    }

    public Date forwardDayAndUpdateValues(Integer portfolioId) {
        // Add today's networth to the portfolio history, then move forward one day and update stock prices
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> new PortfolioNotFoundException(portfolioId));
        BigDecimal networth = getPortfolioNetworth(portfolioId);
        portfolioHistoryRepository.save(new PortfolioHistory(networth, portfolio.getCurrentDate(), portfolioId));
        System.out.println(portfolio.getCurrentDate());
        portfolio.forwardNextWeekDay();
        System.out.println(portfolio.getCurrentDate());
        while (!isMarketOpen(portfolio.getCurrentDate())) {
            portfolio.forwardNextWeekDay();
        }
        System.out.println(portfolio.getCurrentDate());

        portfolioRepository.save(portfolio);
        System.out.println("Start");
        List<StockHistory> stockHistories = stockHistoryRepository.findAllByDate(portfolio.getCurrentDate());
        for (StockHistory sh : stockHistories) {
            Stock stock = stockRepository.findById(sh.getStock().getId()).orElseThrow(() -> new StockNotFoundException(sh.getStock().getId()));
            stock.setCurrentPrice(sh.getPrice());
            stockRepository.save(stock);
        }
        System.out.println("End");
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
            default -> throw new InvalidActionException();
        }
    }

    private boolean isMarketOpen(Date date) {
        return stockHistoryRepository.existsByDate(date);
    }

    public BigDecimal getMoneyInvested(Integer portfolioId) {
        List <StockTransaction> stocks = getPortfolioTransactions(portfolioId);
        return stocks.stream()
                .filter(st -> st.getAction() == Action.BUYING)
                .map(st -> st.getAmount().multiply(st.getActionPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getProfit(Integer portfolioId) {
        List <StockTransaction> stocks = getPortfolioTransactions(portfolioId);
        BigDecimal sells = stocks.stream()
                .filter(st -> st.getAction() == Action.SELLING)
                .map(st -> st.getAmount().multiply(st.getActionPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return getPortfolioNetworth(portfolioId).add(sells);
    }

    public Portfolio createPortfolio(String name) {
        Portfolio portfolio = new Portfolio(name, BigDecimal.ZERO);
        return portfolioRepository.save(portfolio);
    }
}
