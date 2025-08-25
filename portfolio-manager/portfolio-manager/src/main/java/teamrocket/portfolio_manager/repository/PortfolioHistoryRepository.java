package teamrocket.portfolio_manager.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import teamrocket.portfolio_manager.entity.Portfolio;
import teamrocket.portfolio_manager.entity.PortfolioHistory;

import java.util.List;

@Repository
public interface PortfolioHistoryRepository extends JpaRepository<PortfolioHistory, Integer> {
    List<PortfolioHistory> findByPortfolioId(Integer portfolioId);
}
