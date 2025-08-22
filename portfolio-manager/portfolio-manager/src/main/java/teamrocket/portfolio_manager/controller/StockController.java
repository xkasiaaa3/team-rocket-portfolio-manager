package teamrocket.portfolio_manager.controller;

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
    StockService stockService;

    // Read
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Stock> getAllStocks(){
        return stockService.getAllStocks();
    }
    @GetMapping("/owned")
    @ResponseStatus(HttpStatus.OK)
    public List<Stock> getAllOwnedStocks(){
        return null;
    }
    @GetMapping("/history/{stockSymbol}")
    @ResponseStatus(HttpStatus.OK)
    public List<StockHistory> getStockHistoryBySymbol(@PathVariable String stockSymbol){
        return stockService.getStockHistoryBySymbol(stockSymbol);
    }
    @GetMapping("/transactions")
    @ResponseStatus(HttpStatus.OK)
    public List<StockTransaction> getAllStockTransactions(){
        return null;
    }
    @GetMapping("/transactions/{stockSymbol}")
    @ResponseStatus(HttpStatus.OK)
    public List<StockTransaction> getStockTransactionsBySymbol(@PathVariable String stockSymbol){
        return null;
    }

    // Create
    @PostMapping("/transactions")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public StockTransaction addStockTransaction(String stockSymbol, Integer amount, String action) {
        return null;
    }
}
