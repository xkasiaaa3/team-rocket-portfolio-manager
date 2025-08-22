package teamrocket.portfolio_manager.exception;

public class NotEnoughStocksException extends RuntimeException {
    public NotEnoughStocksException(Integer stockId) {
        super("You don't have enough of stock with id " +stockId);
    }
}
