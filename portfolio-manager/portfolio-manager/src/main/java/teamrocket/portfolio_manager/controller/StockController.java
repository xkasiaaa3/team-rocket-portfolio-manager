package teamrocket.portfolio_manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import teamrocket.portfolio_manager.entity.Stock;
import teamrocket.portfolio_manager.entity.StockHistory;
import teamrocket.portfolio_manager.service.StockService;

import java.util.List;

@RestController
@RequestMapping("/stocks")
public class StockController {

    // Inject Service class
    @Autowired
    StockService stockService;

    // Read
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Stock> getAllValidStocks(){
        return stockService.getAllStocks();
    }

    @GetMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public void updateStocks(){
        stockService.updateStocks();
    }

    @GetMapping("/{stockId}/history")
    @ResponseStatus(HttpStatus.OK)
    public List<StockHistory> getStockHistoryBySymbol(@PathVariable Integer stockId){
        return stockService.getStockHistoryByStockId(stockId);
    }
    @GetMapping("/{stockId}/change")
    @ResponseStatus(HttpStatus.OK)
    public double getStockChangeBySymbol(@PathVariable Integer stockId){
        // PORTFOLIO ID IS SET TO 1 CHANGE LATER
        return stockService.getStockChangeByStockId(stockId, 1);
    }
}
