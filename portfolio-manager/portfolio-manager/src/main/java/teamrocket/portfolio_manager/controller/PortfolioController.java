package teamrocket.portfolio_manager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import teamrocket.portfolio_manager.entity.Stock;
import teamrocket.portfolio_manager.entity.StockTransaction;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/portfolios")
public class PortfolioController {

    // Inject Service class here

    // Read
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Stock> getAllPortfolios(String stockSymbol){
        return null;
    }
    @GetMapping("/{portfolioId}")
    @ResponseStatus(HttpStatus.OK)
    public void getAllStockPerformancesByPortfolioId(@PathVariable int portfolioId){
    }
    @GetMapping("/{portfolioId}/balance")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal getPortfolioBalance(String stockSymbol){
        return null;
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
    @GetMapping("/owned")
    @ResponseStatus(HttpStatus.OK)
    public List<Stock> getAllOwnedStocks(){
        return null;
    }

    // Update
    @PutMapping("/date")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Date updateDate(Date newDate) {
        return null;
    }
    @PutMapping("/{portfolioId}/balance")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BigDecimal updateBalance(BigDecimal changeInBalance) {
        return null;
    }
    @PostMapping("/transactions")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public StockTransaction addStockTransaction(String stockSymbol, Integer amount, String action) {
        return null;
    }
}
