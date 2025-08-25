package teamrocket.portfolio_manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import teamrocket.portfolio_manager.entity.Portfolio;
import teamrocket.portfolio_manager.entity.Stock;
import teamrocket.portfolio_manager.entity.StockHistory;
import teamrocket.portfolio_manager.exception.PortfolioNotFoundException;
import teamrocket.portfolio_manager.exception.StockNotFoundException;
import teamrocket.portfolio_manager.repository.PortfolioRepository;
import teamrocket.portfolio_manager.repository.StockHistoryRepository;
import teamrocket.portfolio_manager.repository.StockRepository;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
public class StockService {
    @Autowired
    StockRepository stockRepository;
    @Autowired
    StockHistoryRepository stockHistoryRepository;
    @Autowired
    PortfolioRepository portfolioRepository;

    public List<Stock> getAllStocks() {
        List<Stock> stocks = stockRepository.findAll();
        stocks.sort(Comparator.comparing(Stock::getStockName));
        return stocks;
    }

    public Integer getStockId(String stockSymbol) {
        Stock stock = stockRepository.findByStockSymbol(stockSymbol).orElseThrow(() -> new StockNotFoundException(stockSymbol));
        return stock.getId();
    }

    public List<StockHistory> getStockHistoryBySymbol(String stockSymbol) {
        Stock stock = stockRepository.findByStockSymbol(stockSymbol).orElseThrow(() -> new StockNotFoundException(stockSymbol));

        List<StockHistory> stockHistories = stockHistoryRepository.findAllByStockId(stock.getId());
        stockHistories.sort(Comparator.comparing(StockHistory::getDate));

        return stockHistories;
    }

    public double getStockChangeBySymbol(String stockSymbol) {
        Stock stock = stockRepository.findByStockSymbol(stockSymbol).orElseThrow(() -> new StockNotFoundException(stockSymbol));
        Portfolio portfolio = portfolioRepository.findById(1).orElseThrow(() -> new PortfolioNotFoundException(1));

        int dateDifference = 1;

        Date currentDate = portfolio.getCurrentDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, -dateDifference);
        Date previousDate = calendar.getTime();

        BigDecimal currentPrice = stock.getCurrentPrice();
        StockHistory previousStockHistory = stockHistoryRepository.findByStockIdAndDate(stock.getId(), previousDate);
        BigDecimal previousPrice = previousStockHistory.getPrice();

        return (currentPrice.doubleValue() - previousPrice.doubleValue()) / previousPrice.doubleValue();
    }
}
