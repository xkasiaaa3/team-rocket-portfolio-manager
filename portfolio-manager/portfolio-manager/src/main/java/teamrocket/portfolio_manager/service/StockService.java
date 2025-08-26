package teamrocket.portfolio_manager.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamrocket.portfolio_manager.entity.Portfolio;
import teamrocket.portfolio_manager.entity.Stock;
import teamrocket.portfolio_manager.entity.StockHistory;
import teamrocket.portfolio_manager.exception.PortfolioNotFoundException;
import teamrocket.portfolio_manager.exception.StockNotFoundException;
import teamrocket.portfolio_manager.model.StockWithChangeDTO;
import teamrocket.portfolio_manager.repository.PortfolioRepository;
import teamrocket.portfolio_manager.repository.StockHistoryRepository;
import teamrocket.portfolio_manager.repository.StockRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StockService {
    @Autowired
    StockRepository stockRepository;
    @Autowired
    StockHistoryRepository stockHistoryRepository;
    @Autowired
    PortfolioRepository portfolioRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public List<StockWithChangeDTO> getAllValidStocks() {
        Date currentDate = portfolioRepository.findAll().stream().findFirst().get().getCurrentDate();
        List<Stock> stocks = stockRepository.findByCurrentPriceIsNotNull();
        List<StockHistory> allHistories = stockHistoryRepository.findAllStockHistoriesBeforeDateSorted(currentDate);
        Map<Integer, StockHistory> latestHistories = new HashMap<>();
        for (StockHistory stockHistory : allHistories) {
            Integer stockId = stockHistory.getStock().getId();
            if (!latestHistories.containsKey(stockId)) {
                latestHistories.put(stockId, stockHistory);
            }
        }

        return stocks.stream().map(s -> {
            StockHistory stockHistory = latestHistories.get(s.getId());
            BigDecimal currentPrice = s.getCurrentPrice();
            BigDecimal previousPrice = stockHistory.getPrice();

            double change = (currentPrice.doubleValue() - previousPrice.doubleValue()) / previousPrice.doubleValue();
            return s.toDTO(change);
        }).toList();

    }

    public Integer getStockId(String stockSymbol) {
        Stock stock = stockRepository.findByStockSymbol(stockSymbol).orElseThrow(() -> new StockNotFoundException(stockSymbol));
        return stock.getId();
    }

    public List<StockHistory> getStockHistoryByStockIdForLastMonth(Integer stockId) {
        Date currentDate = portfolioRepository.findAll().stream().findFirst().get().getCurrentDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.MONTH, -1);
        Date previousDate = calendar.getTime();

        List<StockHistory> stockHistories = stockHistoryRepository.findAllByStockIdAndDateBetween(stockId,previousDate,currentDate);
        stockHistories.sort(Comparator.comparing(StockHistory::getDate));
        return stockHistories;
    }

    public double getStockChangeByStockId(Integer stockId) {
        // FIX TO CATCH EXCEPTION WHEN PREVIOUS DAY IS WEEKEND
        Stock stock = stockRepository.findById(stockId).orElseThrow(() -> new StockNotFoundException(stockId));
        Date currentDate = portfolioRepository.findAll().stream().findFirst().get().getCurrentDate();

        BigDecimal currentPrice = stock.getCurrentPrice();
        Optional<StockHistory> previousStockHistory = stockHistoryRepository.findFirstByStockIdAndDateLessThanEqualOrderByDateDesc(stockId,currentDate);
        BigDecimal previousPrice = previousStockHistory.isPresent() ? previousStockHistory.get().getPrice() : currentPrice;

        return (currentPrice.doubleValue() - previousPrice.doubleValue()) / previousPrice.doubleValue();
    }

    @Transactional
    public void updateStocks() {
        clean();
        updateStocksFromCsv();

        List<Stock> stocks = stockRepository.findAll();
        Date currentDate = portfolioRepository.findAll().stream().findFirst().get().getCurrentDate();

        stocks.forEach(stock -> {
            updateHistoryForStock(stock.getStockSymbol().toUpperCase(), stock.getId());

            StockHistory stockHistory = stockHistoryRepository.findByStockIdAndDate(stock.getId(), currentDate);
            stock.setCurrentPrice(stockHistory != null ? stockHistory.getPrice() : null);
            stockRepository.save(stock);

        });
    }

    @Transactional
    private void updateHistoryForStock(String stockSymbol, Integer stockId) {

        try {
            int updated = entityManager.createNativeQuery("INSERT INTO stock_history(stock_id, date, price) SELECT " + stockId + ", price, open FROM CSVREAD('portfolio-manager/data/stock_data/" + stockSymbol + ".csv', NULL, 'charset=UTF-8') OFFSET 2;").executeUpdate();
            System.out.println("Updated rows: [" + updated + "] for stock " + stockSymbol);
        } catch (Exception e) {
            System.out.println("Problem with updating stock " + stockSymbol);
        }
    }

    @Transactional
    private void clean() {
        stockRepository.deleteAll();
        stockHistoryRepository.deleteAll();
    }

    @Transactional
    private void updateStocksFromCsv() {
        stockRepository.updateStocksFromCsv();
    }
}
