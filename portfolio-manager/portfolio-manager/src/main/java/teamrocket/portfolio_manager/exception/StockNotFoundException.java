package teamrocket.portfolio_manager.exception;

public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException(Integer id) {
        super("Stock with id " + id+" was not found");
    }

    public StockNotFoundException(String symbol) {
        super("Stock with symbol " + symbol+" was not found");
    }
}
