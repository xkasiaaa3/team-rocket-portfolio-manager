package teamrocket.portfolio_manager.exception;

public class NotEnoughBalanceException extends RuntimeException {
    public NotEnoughBalanceException() {
        super("You don't have enough balance");
    }
}
