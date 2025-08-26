package teamrocket.portfolio_manager;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import teamrocket.portfolio_manager.entity.Portfolio;
import teamrocket.portfolio_manager.entity.Stock;
import teamrocket.portfolio_manager.entity.StockTransaction;
import teamrocket.portfolio_manager.exception.PortfolioNotFoundException;
import teamrocket.portfolio_manager.exception.StockNotFoundException;
import teamrocket.portfolio_manager.model.Action;
import teamrocket.portfolio_manager.repository.PortfolioRepository;
import teamrocket.portfolio_manager.repository.StockRepository;
import teamrocket.portfolio_manager.repository.StockTransactionRepository;
import teamrocket.portfolio_manager.service.StockService;

import java.math.BigDecimal;
import java.util.Date;

@SpringBootApplication
public class PortfolioManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortfolioManagerApplication.class, args);
	}

	@Bean
	CommandLineRunner initPortfolioDatabase(PortfolioRepository portfolioRepository) {
		return args -> {
			if (portfolioRepository.findAll().isEmpty()) {
				portfolioRepository.save(new Portfolio("Your First Portfolio", new BigDecimal("1000000")));
			}
		};
	}

	@Bean
	CommandLineRunner initPortfolioTransactionsDatabase(
			StockTransactionRepository stockTransactionRepository,
			StockRepository stockRepository,
			PortfolioRepository portfolioRepository) {
		return args -> {
			Stock stock = stockRepository.findById(1).orElseThrow(() -> new StockNotFoundException(1));
			Portfolio portfolio = portfolioRepository.findById(1).orElseThrow(() -> new PortfolioNotFoundException(1));
			if (stockTransactionRepository.findAll().isEmpty()) {
				stockTransactionRepository.save(new StockTransaction(
						stock, portfolio, portfolio.getCurrentDate(), new BigDecimal(100),
						Action.BUYING, stock.getCurrentPrice()));
			}
		};
	}


}
