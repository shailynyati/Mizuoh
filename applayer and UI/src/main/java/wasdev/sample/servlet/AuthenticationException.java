package wasdev.sample.servlet;

public class AuthenticationException extends Exception {
	public static final String INVALID_REQUEST = "Invalid Request. User ID, Passcode or Role missing";
	public static final String LOGIN_FAILED = "Login Failed. User ID or Passcode incorrect";
	public static final String LOGIN = "Login required to access APIs";
	
	public AuthenticationException(String message)
	{
		super(message);
	}
}
