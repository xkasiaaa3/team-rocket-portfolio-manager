package teamrocket.portfolio_manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import teamrocket.portfolio_manager.entity.Stock;
import teamrocket.portfolio_manager.entity.StockHistory;
import teamrocket.portfolio_manager.exception.StockNotFoundException;
import teamrocket.portfolio_manager.repository.StockHistoryRepository;
import teamrocket.portfolio_manager.repository.StockRepository;

import java.util.Comparator;
import java.util.List;

@Service
public class StockService {
    @Autowired
    StockRepository stockRepository;
    @Autowired
    StockHistoryRepository stockHistoryRepository;

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
    };
}
