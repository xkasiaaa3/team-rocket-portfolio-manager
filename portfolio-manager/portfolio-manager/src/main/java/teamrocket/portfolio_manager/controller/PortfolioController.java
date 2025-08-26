package teamrocket.portfolio_manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import teamrocket.portfolio_manager.entity.Portfolio;
import teamrocket.portfolio_manager.entity.Stock;
import teamrocket.portfolio_manager.entity.StockTransaction;
import teamrocket.portfolio_manager.model.PortfolioNetworthDTO;
import teamrocket.portfolio_manager.model.TransactionDTO;
import teamrocket.portfolio_manager.service.PortfolioService;
import teamrocket.portfolio_manager.service.StockService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "http://localhost:63342")
@RestController
@RequestMapping("/portfolios")
public class PortfolioController {

    // Inject Service class here
    @Autowired
    PortfolioService portfolioService;
    @Autowired
    StockService stockService;

    // Read
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Portfolio> getAllPortfolios() {
        return portfolioService.getAllPortfolios();
    }
    @GetMapping("/{portfolioId}/networth")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal getPortfolioNetworth(@PathVariable Integer portfolioId) {
        return portfolioService.getPortfolioNetworth(portfolioId);
    }

    @GetMapping("/{portfolioId}/change")
    @ResponseStatus(HttpStatus.OK)
    public double getPortfolioNetworthChange(@PathVariable Integer portfolioId) {
        return portfolioService.getPortfolioNetworthChange(portfolioId);
    }

    @GetMapping("/{portfolioId}/histories")
    @ResponseStatus(HttpStatus.OK)
    public List<PortfolioNetworthDTO> getPortfolioHistories(@PathVariable Integer portfolioId) {
        return portfolioService.getPortfolioHistoriesForLastMonth(portfolioId);
    }
    @GetMapping("/{portfolioId}/balance")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal getPortfolioBalance(@PathVariable Integer portfolioId) {
        return portfolioService.getPortfolioBalance(portfolioId);
    }
    @GetMapping("/{portfolioId}/transactions")
    @ResponseStatus(HttpStatus.OK)
    public List<StockTransaction> getStockTransactionsBySymbol(@PathVariable Integer portfolioId, @RequestParam(required = false) Integer stockId) {
        if (stockId == null) {
            return portfolioService.getPortfolioTransactions(portfolioId);
        } else {
            return portfolioService.getPortfolioTransactionsBySymbol(portfolioId, stockId);
        }
    }
    @GetMapping("/{portfolioId}/stocks")
    @ResponseStatus(HttpStatus.OK)
    public List<Stock> getOwnedStocks(@PathVariable Integer portfolioId) {
        return portfolioService.getPortfolioStocks(portfolioId);
    }
    @GetMapping("/{portfolioId}/money-invested")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal getMoneyInvested(@PathVariable Integer portfolioId) {
        return portfolioService.getMoneyInvested(portfolioId);
    }
    @GetMapping("/{portfolioId}/profit")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal getProfit(@PathVariable Integer portfolioId) {
        return portfolioService.getProfit(portfolioId);
    }

    // Update
    @PutMapping("/{portfolioId}/forward-date")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Date updateDate(@PathVariable Integer portfolioId) {
        return portfolioService.forwardDayAndUpdateValues(portfolioId);
    }
    @PutMapping("/{portfolioId}/balance")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BigDecimal updateBalance(@PathVariable Integer portfolioId, @RequestBody BigDecimal changeInBalance) {
        System.out.println("The Change is: " + changeInBalance);
        return portfolioService.updatePortfolioBalance(portfolioId, changeInBalance);
    }

    //Add
    @PostMapping("/{portfolioId}/transaction")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public StockTransaction addStockTransaction(@PathVariable Integer portfolioId, @RequestBody TransactionDTO transactionDTO) {
        return portfolioService.makeTransaction(portfolioId, transactionDTO);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Portfolio createPortfolio(@RequestBody String name) {
        return portfolioService.createPortfolio(name);
    }
}
