package teamrocket.portfolio_manager;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import teamrocket.portfolio_manager.entity.Stock;
import teamrocket.portfolio_manager.entity.StockTransaction;
import teamrocket.portfolio_manager.model.Action;
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
	CommandLineRunner initStockDatabase(StockRepository stockRepository) {
		return args -> {
			if (stockRepository.findAll().isEmpty()) {
				stockRepository.save(new Stock("A", "AOIWDJN", "USD", new BigDecimal("142.35")));
				stockRepository.save(new Stock("B", "Unjais", "USD", new BigDecimal("173.35")));
				stockRepository.save(new Stock("C", "Maiuea", "USD", new BigDecimal("163.35")));
				stockRepository.save(new Stock("D", "Amavns", "USD", new BigDecimal("172.35")));
			}
		};
	}

	@Bean
	CommandLineRunner initStockTransactionDatabase(StockTransactionRepository stockTransactionRepository) {
		return args -> {
			if (stockTransactionRepository.findAll().isEmpty()) {
				stockTransactionRepository.save(new StockTransaction(
						1, new Date(), new BigDecimal("15"), Action.BUYING, new BigDecimal("142.35")));
				stockTransactionRepository.save(new StockTransaction(
						1, new Date(), new BigDecimal("18"), Action.BUYING, new BigDecimal("135.35")));
				stockTransactionRepository.save(new StockTransaction(
						2, new Date(), new BigDecimal("14"), Action.BUYING, new BigDecimal("173.35")));
			}
		};
	}
}
