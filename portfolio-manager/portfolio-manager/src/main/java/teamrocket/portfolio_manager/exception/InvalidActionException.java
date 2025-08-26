package teamrocket.portfolio_manager.exception;

public class InvalidActionException extends RuntimeException {
    public InvalidActionException() {
        super("Invalid action");
    }
}
