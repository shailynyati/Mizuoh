package wasdev.sample.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import wasdev.sample.servlet.session.SessionManager;
import wasdev.sample.servlet.session.Transaction;
import wasdev.sample.servlet.util.TradeFinanceApplicationConstants;

/**
 * Servlet implementation class EventManager
 */
@WebServlet("/EventManager")
public class EventManager extends HttpServlet implements Runnable {
	private static final long serialVersionUID = 1L;
	static ArrayList<String> whitelistedSources = new ArrayList<String>();
	static
	{
        whitelistedSources.add("localhost");
        whitelistedSources.add("127.0.0.1");
	}
	
	// Transaction info and status for push notification; add more fields later as appropriate
	class TransactionResult
	{
		String uuid;
		boolean status;
		String errorMessage;
		
		public TransactionResult(String u, boolean s, String errm)
		{
			uuid = u;
			status = s;
			errorMessage = errm;
		}
	}
	
	private Vector<String> transactionPendingVerificationQueue;
	private Vector<Transaction> pushNotificationQueue;
	String latestPushNotificationUUID;
	
	Thread transactionVerifier;
	boolean stopTransactionVerifier;
	boolean transactionVerifierStopped;
       
    /*
     * @see HttpServlet#HttpServlet()
     */
        public EventManager() {
        super();
        transactionPendingVerificationQueue = new Vector<String>(0, 1);
        pushNotificationQueue = new Vector<Transaction>(0, 1);
        latestPushNotificationUUID = "";
        transactionVerifier = new Thread(this);
        transactionVerifier.setPriority(Thread.MIN_PRIORITY);
        stopTransactionVerifier = false;
        transactionVerifierStopped = false;
        transactionVerifier.start();
        System.out.println("Started transaction verification and push notification thread");
    }
    
    private boolean authenticateAddress(String address)
    {
    	if(TradeFinanceApplicationConstants.eventManagerTestMode)
    	{
    		return true;
    	}
    	for(int i = 0 ; i < whitelistedSources.size() ; i++)
    	{
    		if(address.equals(whitelistedSources.get(i)))
    		{
    			return true;
    		}
    	}
    	return false;
    }
    
	 /*For now, we assume that a notification implies successful execution of a transaction
	   If the HL bug isn't fixed, we need to lookup the query method and call it
*/	
    private TransactionResult verifyTransactionCompleted(String uuid)
    {
    	return new TransactionResult(uuid, true, "");
    }
    
    /* We may get multiple notifications for a particular UUID from event trackers running on multiple peers
       But we need to handle (verify) a particular UUID only once, so we ignore the duplicate notifications.
       An unknown UUID and a duplicate UUID are handled the same way: by dropping the requests.*/
    
    private Transaction checkAndQueuePushNotification(String uuid, boolean status, String errorMessage)
    {
		// Check if we have a transaction corresponding to this UUID stored
		// If we don't, just ignore the event
		Transaction storedTx = SessionManager.getSessionData(uuid, true);
		if(storedTx == null)
		{
			System.out.println("No record of UUID " + uuid + " found. Ignoring event notification");
			if(TradeFinanceApplicationConstants.eventManagerTestMode)
			{
				latestPushNotificationUUID = uuid;
			}
		}
		else
		{
			if(storedTx.isInvoke())
			{
				storedTx.setStatus(status);
				storedTx.setErrorMessage(errorMessage);
				synchronized(this)
				{
					pushNotificationQueue.addElement(storedTx);
					latestPushNotificationUUID = uuid;
				}
				System.out.println("Added invoke transaction '" + uuid + "' to notification queue");
			}
			if(storedTx.isDeploy())
			{
				//storedTx.updateChaincodeStates(status, errorMessage);
				System.out.println("Processed deploy transaction '" + uuid + "'");
			}
		}
		return storedTx;
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sourceAddress = request.getRemoteAddr();
		System.out.println("Received GET request from " + sourceAddress);
		if(!authenticateAddress(sourceAddress))
		{
			System.out.println("Unauthorized address: " + sourceAddress);
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.getWriter().print("<h2>Access Forbidden</h2>");
			return;
		}
		String retVal = "<h2>You have reached the Trade Finance Application Server Event Manager from " + 
				sourceAddress + "</h2>";
		String queryString = request.getQueryString();
		if(queryString != null && queryString.equals("getLatestPushNotificationUUID"))
		{
			if(!TradeFinanceApplicationConstants.eventManagerTestMode)
			{
				System.out.println("'getLatestPushNotificationUUID' only available in test mode.");
				response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
				response.getWriter().print("<h2>Function only available in test mode</h2>");
				return;
			}
			retVal = latestPushNotificationUUID;
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().print(retVal);
	}

	/**
	 @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	*/
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sourceAddress = request.getRemoteAddr();
		System.out.println("Received POST request from " + sourceAddress);
		if(!authenticateAddress(sourceAddress))
		{
			System.out.println("Unauthorized address: " + sourceAddress);
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.getWriter().print("<h2>Access Forbidden</h2>");
			return;
		}
		// Check MIME type of content
		if(!request.getContentType().equals("application/json"))
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print("<h2>Invalid content type: " + request.getContentType() + "</h2>");
			return;
		}
		StringBuffer postContent = new StringBuffer();
		BufferedReader br = request.getReader();
		while(true)
		{
			String next = br.readLine();
			if(next == null)
			{
				break;
			}
			if(postContent.length() > 0)
			{
				postContent.append("\n");
			}
			postContent.append(next);
		}
		// Parse JSON content to get the list of UUIDs and transaction statuses
		// Lack of a status field indicates <unknown status>
		try {
			JsonArray transactions = (new JsonParser()).parse(postContent.toString()).getAsJsonArray();
			Iterator<JsonElement> transactionsIter = transactions.iterator();
			while(transactionsIter.hasNext())
			{
				JsonElement transactionInfo = transactionsIter.next();
				if(!(transactionInfo instanceof JsonObject))
				{
					throw new JsonParseException("Unable to parse transaction object");
				}
				// Get 'uuid' and 'status' (if available)
				String uuid = "";
				String errorMessage = "";
				boolean status = false;
				boolean statusFound = false;
				Set<Entry<String, JsonElement>> transactionAttributeSet = transactionInfo.getAsJsonObject().entrySet();
				Iterator<Entry<String, JsonElement>> transactionAttributeIterator = transactionAttributeSet.iterator();
				while(transactionAttributeIterator.hasNext())
				{
					Entry<String, JsonElement> transactionAttribute = transactionAttributeIterator.next();
					if(transactionAttribute.getKey().equals("uuid"))
					{
						uuid = transactionAttribute.getValue().getAsString();
					}
					if(transactionAttribute.getKey().equals("status"))
					{
						status = transactionAttribute.getValue().getAsBoolean();
						statusFound = true;
					}
					if(transactionAttribute.getKey().equals("error"))
					{
						errorMessage = transactionAttribute.getValue().getAsString();
					}
				}
				if(statusFound)
				{
					checkAndQueuePushNotification(uuid, status, errorMessage);
				}
				else
				{
					synchronized(this)
					{
						transactionPendingVerificationQueue.addElement(uuid);
					}
					System.out.println("Added transaction '" + uuid + "' to verification queue");
				}
			}
		} catch(JsonParseException jpe)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print("Badly formatted content: " + jpe.getMessage());
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		//response.getWriter().print("Hello there. Received: " + postContent);
		response.getWriter().print("OK: " + postContent);
	}

	@Override
	public void run() {
		//int loopCount = 0;
		long timestamp = System.currentTimeMillis();
		while(true)
		{
			if(stopTransactionVerifier)
			{
				break;
			}
			try
			{
				// Verify transactions whose completion state we don't know yet
				int maxVerificationCount = 5;	// Maximum pending transactions to process before we send push notifications
				int verifiedCount = 0;
				while(!transactionPendingVerificationQueue.isEmpty())
				{
					if(stopTransactionVerifier)
					{
						break;
					}
					String uuid = "";
					synchronized(this)
					{
						uuid = transactionPendingVerificationQueue.remove(0);
					}
					if(uuid == null || uuid.isEmpty())
					{
						throw new NullPointerException("Empty UUID in pending verification queue");
					}
					TransactionResult result = verifyTransactionCompleted(uuid);
					if(checkAndQueuePushNotification(uuid, result.status, result.errorMessage) != null)
					{
						System.out.println("Moved transaction '" + uuid + "' from verification queue to notification queue");
					}
					verifiedCount++;
					if(verifiedCount == maxVerificationCount)
					{
						break;
					}
				}
				if(stopTransactionVerifier)
				{
					break;
				}
				
				// Send push notifications to the (mobile) client applications
				int maxNotificationCount = 5;	// Maximum push notifications to send before we examine the verification queue
				int notifiedCount = 0;
				while(!pushNotificationQueue.isEmpty())
				{
					if(stopTransactionVerifier)
					{
						break;
					}
					Transaction transaction = null;
					synchronized(this)
					{
						transaction = pushNotificationQueue.remove(0);
					}
					if(transaction == null)
					{
						throw new NullPointerException("Null object in push notification queue");
					}
					// Send push notification and register notification in blockchain
					WorkflowEvents.handleEvent(transaction);
					System.out.println("Transaction UUID " + transaction.getUUID() + " processed");
					notifiedCount++;
					if(notifiedCount == maxNotificationCount)
					{
						break;
					}
				}
				// Sleep only if there's no activity happening right now
				if(verifiedCount == 0 && notifiedCount == 0)
				{
					//if(loopCount % 50 == 0)
					long currTimestamp = System.currentTimeMillis();
					if(currTimestamp - timestamp >= 300000)		// Log heartbeat message after every 5 minutes
					{
						timestamp = currTimestamp;
						System.out.println("HEARTBEAT: Verifier and Notifier thread active");
					}
					Thread.sleep(2000);
				}
				//loopCount++;
			} catch(Exception e)
			{
				e.printStackTrace();
				break;
			}
		}
		transactionVerifierStopped = true;
	}
	
	@Override
	public void destroy()
	{
		stopTransactionVerifier = true;
		System.out.println("Going to stop thread");
		while(!transactionVerifierStopped)
		{
			try
			{
				Thread.sleep(5000);
			} catch(InterruptedException ie)
			{
				System.out.println("Exception contents: " + ie.getMessage());
				ie.printStackTrace();
			}
		}
		System.out.println("Stopped thread");
	}
}
