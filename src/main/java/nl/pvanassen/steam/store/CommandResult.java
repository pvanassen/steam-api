package nl.pvanassen.steam.store;

/**
 * Result of a command to steam
 * 
 * @author Paul van Assen
 */
public class CommandResult {
    private final boolean success;
    private final Exception error;
    private final String message;

    private CommandResult(boolean success, Exception error, String message) {
        super();
        this.success = success;
        this.error = error;
        this.message = message;
    }

    /**
     * All good
     * 
     * @return Returns a command result with no error and no message
     */
    public static CommandResult success() {
        return success(null);
    }

    /**
     * All good
     * 
     * @param message Friendly message explaining why everything was ok
     * @return Returns a command result with no error and a message
     */
    public static CommandResult success(String message) {
        return new CommandResult(true, null, message);
    }

    /**
     * Some error occurred, message with error is supplied
     * 
     * @param message Message of the error
     * @return Command result indicating an error, with a message included
     */
    public static CommandResult error(String message) {
        return error(null, message);
    }

    /**
     * Some error occurred, exception is supplied. Message will be set to the exception message
     * 
     * @param error Exception causing the error
     * @return Command result indicating an error, with an exception included
     */
    public static CommandResult error(Exception error) {
        return error(error, error.getMessage());
    }

    /**
     * Some error occurred, exception is supplied. Message will be set to the exception message
     * 
     * @param error Exception causing the error
     * @param message Message to include
     * @return Command result indicating an error, with an exception included
     */
    public static CommandResult error(Exception error, String message) {
        return new CommandResult(false, error, message);
    }

    /**
     * @return Is successful?
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return Exception if available
     */
    public Exception getError() {
        return error;
    }

    /**
     * @return Message if available
     */
    public String getMessage() {
        return message;
    }
}
