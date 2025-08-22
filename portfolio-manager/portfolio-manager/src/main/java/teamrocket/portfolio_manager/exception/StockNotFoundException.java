package teamrocket.portfolio_manager.exception;

public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException(Integer id) {
        super("Stock with id " + id+" was not found");
    }
}
