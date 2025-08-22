package teamrocket.portfolio_manager.exception;

public class PortfolioNotFoundException extends RuntimeException {
    public PortfolioNotFoundException(Integer portfolioId) {
        super("Portfolio with id " + portfolioId + " was not found");
    }
}
