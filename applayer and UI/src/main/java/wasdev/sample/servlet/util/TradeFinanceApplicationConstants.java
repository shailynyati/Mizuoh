package wasdev.sample.servlet.util;

import java.util.HashMap;

public class TradeFinanceApplicationConstants {
	
	// Test parameters START
	public static boolean transactionManagerTestMode = false;
	public static boolean eventManagerTestMode = false;
	public static final String testContractID = "contract1";
	public static final String[] testTxMethod = { "submitLC", "acceptLC", "rejectLC", "submitED", "acceptED", "rejectED", 
		"acceptToPay", "paymentReceived", "defaultedOnPayment" };
	public static final int[] testTxMethodNotificationCount = {1, 1, 1, 1, 1, 1, 1, 1, 1};
	public static boolean eventManagerTestStarted = false;
	public static int testMethodCounter = 0;
	public static int testMethodNotificationCounter = 0;
	public static HashMap<String, Boolean> testUuidRecords = new HashMap<String, Boolean>();
	public static boolean testIgnoreNextUUID = false;
	// TEST parameters END
	
}
