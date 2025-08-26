package teamrocket.portfolio_manager.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import teamrocket.portfolio_manager.entity.Portfolio;
import teamrocket.portfolio_manager.entity.PortfolioHistory;

import javax.sound.sampled.Port;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioHistoryRepository extends JpaRepository<PortfolioHistory, Integer> {
    List<PortfolioHistory> findByPortfolioId(Integer portfolioId);
    PortfolioHistory findByPortfolioIdAndDate(Integer portfolioId, Date date);
    List<PortfolioHistory> findAllByPortfolioIdAndDateBetween(Integer portfolioId, Date startDate, Date endDate);
    Optional<PortfolioHistory> findFirstByPortfolioIdAndDateLessThanEqualOrderByDateDesc(Integer portfolioId, Date date);

}
