package bowling.exception;

public class UserException extends RuntimeException {
	public UserException(UserExceptionMesssage message) {
		super(message.getMessage());
	}
}
