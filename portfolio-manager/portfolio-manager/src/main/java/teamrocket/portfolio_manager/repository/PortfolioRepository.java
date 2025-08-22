package teamrocket.portfolio_manager.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import teamrocket.portfolio_manager.entity.Portfolio;
import teamrocket.portfolio_manager.entity.Stock;

import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {
}
