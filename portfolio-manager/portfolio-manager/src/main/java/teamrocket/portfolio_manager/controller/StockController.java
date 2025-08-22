package teamrocket.portfolio_manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import teamrocket.portfolio_manager.entity.Stock;
import teamrocket.portfolio_manager.entity.StockHistory;
import teamrocket.portfolio_manager.entity.StockTransaction;
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
    public List<Stock> getAllStocks(){
        return stockService.getAllStocks();
    }
    @GetMapping("/history/{stockSymbol}")
    @ResponseStatus(HttpStatus.OK)
    public List<StockHistory> getStockHistoryBySymbol(@PathVariable String stockSymbol){
        return stockService.getStockHistoryBySymbol(stockSymbol);
    }
}
