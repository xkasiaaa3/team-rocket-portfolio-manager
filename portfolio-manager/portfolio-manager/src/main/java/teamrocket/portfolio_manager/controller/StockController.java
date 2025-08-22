package teamrocket.portfolio_manager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import teamrocket.portfolio_manager.entity.Stock;
import teamrocket.portfolio_manager.entity.StockHistory;
import teamrocket.portfolio_manager.entity.StockTransaction;

import java.util.List;

@RestController
@RequestMapping("/stocks")
public class StockController {

    // Inject Service class

    // Read
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Stock> getAllStocks(){
        return null;
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Stock> getAllOwnedStocks(){
        return null;
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<StockHistory> getStockHistoryBySymbol(String stockSymbol){
        return null;
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<StockTransaction> getAllStockTransactions(){
        return null;
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<StockTransaction> getStockTransactionsBySymbol(String stockSymbol){
        return null;
    }

    // Create
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public StockTransaction addStockTransaction(String stockSymbol, Integer amount, String action) {
        return null;
    }
}
