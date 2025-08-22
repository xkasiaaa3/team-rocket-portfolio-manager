package teamrocket.portfolio_manager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import teamrocket.portfolio_manager.entity.Stock;

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
    public void getAllStockPerformancesByPortfolioId(){
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public void getStockPerformanceBySymbolByPortfolioId(String stockSymbol){
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal getPortfolioBalance(String stockSymbol){
        return null;
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Stock> getAllPortfolios(String stockSymbol){
        return null;
    }

    // Update
    @PutMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Date updateDate(Date newDate) {
        return null;
    }
    @PutMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BigDecimal updateBalance(BigDecimal changeInBalance) {
        return null;
    }
}
