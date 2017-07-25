//Transaction Manager servlet
package wasdev.sample.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;
import wasdev.sample.servlet.session.SessionManager;
import wasdev.sample.servlet.session.Transaction;
import wasdev.sample.servlet.session.UserProfile;
import wasdev.sample.servlet.util.ChaincodeHandler;

/**
 * Servlet implementation class TransactionManager
 
@WebServlet(description = "Handles API requests from clients", urlPatterns = { "/TransactionManager" })
*/
public class TransactionManager extends HttpServlet {
	private static final long serialVersionUID = 3849893824268870046L;
	
	/*private static final String workflowManagementChaincodePath = 
			"github.com/hyperledger/fabric/examples/chaincode/go/TFMVP/chaincode/tradeFinance";*/
	/*private static final String pushNotificationsChaincodePath = 
			"github.com/hyperledger/fabric/examples/chaincode/go/TFMVP/chaincode/push_notifications";*/
	
	
	//private static final String chaincodeIDFile = "/opt/IBM/chaincodes.txt";

	public static String workflowManagementChaincodeID = "5fb63bcfa5d578fc12cffd46e52fd3a7d2ad8f7895efc3d6981eb7ed129942399ead4faef8c878bcb2c10b6d49b2d104b2603577d7e3f0aee81d4367152e0f43"; //softlayer
	//public static String workflowManagementChaincodeID = "2231bbbab422d1d1e3e0fca8c4a0cc2d39823ad2fc4c0c15b5d25951cf54ca3a085e1b826e16d2126ca64bc1d95ae2c587cc2e0138ec34675411988c24cb14cb"; //bluemix
	
	//public static String pushNotificationChaincodeID = "666";
	//public static int chaincodeDeploymentsRunning = 0;
	private ArrayList<String> lcIssuanceFunctions = LCIssuanceWorkflowAPI.getLCIssuanceFunctions();
	private ArrayList<String> exportDocumentsPresentationFunctions = ExportDocumentsPresentationWorkflow.getExportDocumentsPresentationFunctions();
	
	//default value
	private static boolean ENABLE_LOGIN = true;
	private static boolean ENABLE_HL_LOGIN = true;
	
	//public static String HL_URL_NO_SEC = "http://50.97.87.98:8050/chaincode";
	//private static String HL_LOGIN_URL = "http://50.97.87.98:8050/registrar";
    public static String HL_URL_NO_SEC = "https://17654bb1a5dc43efb388d083e23d2ac5-vp0.us.blockchain.ibm.com:5003/chaincode";
	private static String HL_LOGIN_URL = "https://17654bb1a5dc43efb388d083e23d2ac5-vp0.us.blockchain.ibm.com:5003/registrar";
	private static String HL_URL_WITH_SEC = "https://17654bb1a5dc43efb388d083e23d2ac5-vp0.us.blockchain.ibm.com:5003/chaincode";
	public static String WAS_URL_PREFIX = "http://localhost:9080/mizuho";
	
	public static Hashtable<String, UserProfile> clientIPRoleMap = new Hashtable<String, UserProfile>();
	
/*	class ChaincodeDeployer extends Thread
	{
		String secureContext = null;
		
		public ChaincodeDeployer(String sc)
		{
			secureContext = sc;
		}
		
		public void run()
		{
			// Reset the chaincode IDs
			workflowManagementChaincodeID = "";
			pushNotificationChaincodeID = "";
			
			try
			{
				// TEST -- 'bank' code until the actual workflow chaincode is ready
				chaincodeDeploymentsRunning++;
		        String requestJSON = isENABLE_HL_LOGIN() ? 
			        	ChaincodeHandler.createDeployRequest(workflowManagementChaincodePath, "init", new String[]{}, secureContext) : 
	    				ChaincodeHandler.createDeployRequest(workflowManagementChaincodePath, "init", new String[]{}, null);
				String responseJSON = ChaincodeHandler.submitRequest(requestJSON);
				String responseUUID0 = ChaincodeHandler.checkChaincodeResponse(responseJSON, 
						"Deployment of Workflow Management Chaincode", false);
				if(responseUUID0 == null)
				{
					throw new Exception("Workflow Management chaincode deployment failed; response: " + responseJSON);
				}
				else
				{
					System.out.println("Workflow Management chaincode deployment submitted with id: " + responseUUID0);
					SessionManager.putSessionData(new Transaction("", Transaction.deployType, "", 
							responseUUID0, false, Transaction.workflowManagementChaincode));
				}
				// END TEST
				
		        // Push Notifications
				chaincodeDeploymentsRunning++;
				requestJSON = ChaincodeHandler.createDeployRequest(pushNotificationsChaincodePath, "init", new String[]{}, secureContext);
				responseJSON = ChaincodeHandler.submitRequest(requestJSON);
				String responseUUID1 = ChaincodeHandler.checkChaincodeResponse(responseJSON, 
						"Deployment of Push Notifications Chaincode", false);
				if(responseUUID1 == null)
				{
					throw new Exception("Push Notifications chaincode deployment failed; response: " + responseJSON);
				}
				else
				{
					System.out.println("Push Notifications chaincode deployment submitted with id: " + responseUUID1);
					SessionManager.putSessionData(new Transaction("", Transaction.deployType, "", 
							responseUUID1, false, Transaction.pushNotificationsChaincode));
				}
			} catch(Exception e)
			{
				e.printStackTrace();
				chaincodeDeploymentsRunning--;
			}
		}
	}*/
	
	/**
     * @see HttpServlet#HttpServlet()
     */
    public TransactionManager() throws Exception {
        super();
        
        System.out.println("Loading chaincode handles...");
       /* File ccHandlesFile = new File(getChaincodeIDLocalFile());
        if(ccHandlesFile.exists())
        {
	        FileInputStream fis = new FileInputStream(ccHandlesFile);
	    	Properties props = new Properties();
	    	props.load(fis);
	    	workflowManagementChaincodeID = props.getProperty("workflowManagementChaincodeID", "");
	    	pushNotificationChaincodeID = props.getProperty("pushNotificationChaincodeID", "");
	    	
	    	fis.close();
	    	fis = null;
	    	props = null;
        }*/
    }
  
    
    public void init() throws ServletException {
    	System.out.println("Fetching parameter names...");
    	Enumeration<String> paramNames = getInitParameterNames();
    	if(paramNames != null)
    	{
    		while(paramNames.hasMoreElements())
    		{
    			String paramName=paramNames.nextElement();
    			System.out.println(paramName + ": " + getInitParameter(paramName));
    		}
    	}

    	//check Login configuration
        System.out.println("Reading ENABLE_LOGIN...");
        String enableLoginConfig = getInitParameter("ENABLE_LOGIN");
        System.out.println("\t" + enableLoginConfig);
        if(enableLoginConfig != null && enableLoginConfig.length() > 0)
        {
        	if(enableLoginConfig.equalsIgnoreCase("true"))
        		ENABLE_LOGIN = true;
        	else
        		ENABLE_LOGIN = false;
        }
        
    	//check HL Security configuration
        System.out.println("Reading ENABLE_HL_LOGIN...");
        String enableHLLoginConfig = getInitParameter("ENABLE_HL_LOGIN");
        System.out.println("\t" + enableHLLoginConfig);
        if(enableHLLoginConfig != null && enableHLLoginConfig.length() > 0)
        {
        	if(enableHLLoginConfig.equalsIgnoreCase("true"))
        		ENABLE_HL_LOGIN = true;
        	else
        		ENABLE_HL_LOGIN = false;
        }

    	//check HL login URL
    	System.out.println("Reading HL_LOGIN_URL...");
    	String hlLoginURLConfig = getInitParameter("HL_LOGIN_URL");
    	System.out.println("\t" + hlLoginURLConfig);
    	if(hlLoginURLConfig != null && hlLoginURLConfig.length() > 0)
    		HL_LOGIN_URL = hlLoginURLConfig;
    	
    	System.out.println("Reading HL_URL_NO_SEC...");
    	String hlURLNoSecConfig= getInitParameter("HL_URL_NO_SEC");
    	System.out.println("\t" + hlURLNoSecConfig);
    	if(hlURLNoSecConfig != null && hlURLNoSecConfig.length() > 0)
    		HL_URL_NO_SEC = hlURLNoSecConfig;

    	System.out.println("Reading HL_URL_WITH_SEC...");
    	String hlURLWithSecConfig = getInitParameter("HL_URL_WITH_SEC");
    	System.out.println("\t" + hlURLWithSecConfig);
    	if(hlURLWithSecConfig != null && hlURLWithSecConfig.length() > 0)
    		HL_URL_WITH_SEC = hlURLWithSecConfig;

    	//get list of IP addresses allowed to hit the Event Manager
    	System.out.println("Reading EVENT_MANAGER_SOURCE_WHITELIST...");
    	String eventManagerSourceWhitelist = getInitParameter("EVENT_MANAGER_SOURCE_WHITELIST");
    	System.out.println("\t" + eventManagerSourceWhitelist);
    	if(eventManagerSourceWhitelist != null && eventManagerSourceWhitelist.length() > 0)
    	{
    		StringTokenizer st = new StringTokenizer(eventManagerSourceWhitelist, ",");
    		while(st.hasMoreTokens())
    		{
    			EventManager.whitelistedSources.add(st.nextToken());
    		}
    		System.out.println("Event Manager whitelist: " + EventManager.whitelistedSources);
    	}

    	//get WAS URL prefix (protocol, IP address, port)
    	System.out.println("Reading WAS_URL_PREFIX...");
    	String wasURLPrefix = getInitParameter("WAS_URL_PREFIX");
    	System.out.println("\t" + wasURLPrefix);
    	if(wasURLPrefix != null && wasURLPrefix.length() > 0)
    		WAS_URL_PREFIX = wasURLPrefix;
    }

   /* private void deployChaincodes(HttpServletRequest request)
    {
        // Launch deployer thread
		String secureContext = null;
		HttpSession session = request.getSession(false);
		if(session != null)
		{
			System.out.println("Session NOT NULL");
			secureContext = session.getAttribute("userID").toString();
		}
		System.out.println("deployChaincodes(): Secure context: " + secureContext);
        (new ChaincodeDeployer(secureContext)).start();
        
        // Give deployer thread time to start
        try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }*/

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Received GET: " + request.getRequestURL());
		doPost(request, response);		// Handle requests in a unified way
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String method = request.getParameter("method");
		//String[] userDetails = new String[2];
		if(method == null)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(getErrorJSON("No 'method' specified"));
			return;
		}
		
		/*if (method.equalsIgnoreCase("login")) {
			StringBuilder sb = new StringBuilder();
	        BufferedReader br = request.getReader();
	        String str = null;
	        String userID = null,passcode = null;
	        while ((str = br.readLine()) != null) {
	            sb.append(str);
	        }
	        System.out.println("String data:"+str);
	        JSONObject jObj;
			try {
				
				jObj = new JSONObject(sb.toString());
				userID = jObj.getString("userID");
				passcode = jObj.getString("passcode");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
			userDetails[0]=userID;
			userDetails[1]=passcode;
			}*/

		// If we are in the process of deploying chaincodes, tell client to back off and retry later
		/*if(chaincodeDeploymentsRunning > 0)
		{
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			response.getWriter().print(getErrorJSON("Attempting to deploy chaincodes. Please try again after 15 minutes."));
			return;
		}*/
		
		/*if(method.equals("deployChaincodes"))
		{
			deployChaincodes(request);
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().print("Started chaincode deployments. This might take 15 minutes or longer!");
			return;
		}*/
	
		// Check if chaincodes were successfully deployed; exceptions for login and logout, for which we don't need chaincodes
		if(!(method.equalsIgnoreCase("login") || method.equalsIgnoreCase("logout")) && 
				(workflowManagementChaincodeID.isEmpty() )) //|| pushNotificationChaincodeID.isEmpty()))
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(getErrorJSON("One or more chaincodes have not been deployed yet."));
			return;
		}
		
		String query = request.getQueryString();
		if(query == null)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(getErrorJSON("Bad Request"));
			return;
		}
		
		// Get POST content
		StringBuffer postContent = new StringBuffer();
		BufferedReader br = request.getReader();
		if(br != null)
		{
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
		}
		
		
		// Assume 'handleRequest' will take care of the response to the client; only handle exceptions here
		try
		{
			
			if(method.equalsIgnoreCase("login"))
			{
				System.out.println("postcontent in login of tm:"+postContent.toString());
				if(ENABLE_LOGIN)
				 Login.login(request, response, postContent.toString());
				else
					response.getWriter().write("{\"OK\": \"Login disabled. Running without login.\"}");
				return;
			}
			
			 if(method.equalsIgnoreCase("logout"))
			{
				HttpSession session = request.getSession(false);
				if(session != null)
					session.invalidate();
				response.getWriter().write("{\"OK\": \"Logout successful.\"}");
				
				// Remove mapping of IP to role
				TransactionManager.clientIPRoleMap.remove(request.getRemoteAddr());
				return;
			}
			
			//the user must be logged in to proceed beyond this point
			if(ENABLE_LOGIN)
			{
				HttpSession session = request.getSession(false);
				if(session == null || session.getAttribute("userID") == null || session.getAttribute("userRole") == null)
					throw new AuthenticationException(AuthenticationException.LOGIN);
			}
			
			
				 	
			 if(lcIssuanceFunctions.contains(method))
			{
	
				LCIssuanceWorkflowAPI.handleRequest(request, response, method, postContent.toString());
			}
			else if(exportDocumentsPresentationFunctions.contains(method))
			{
				ExportDocumentsPresentationWorkflow.handleRequest(request, response, method, postContent.toString());
			}
			/*else if(method.equalsIgnoreCase("pushNotificationSent"))
			{
				PushNotificationSent.pushNotificationSent(request, response, null);
			}*/
			else
			{
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.getWriter().print(getErrorJSON("Function not supported"));
			}
			 
		} catch(Exception e)
		{
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(getErrorJSON(e.getMessage()));
			return;
		}
	}
	
	// Generate JSON string for any error message to return to the client
	static String getErrorJSON(String errorMessage)
	{
		JsonObject errorObj = new JsonObject();
		errorObj.addProperty("error", errorMessage);
		return errorObj.toString();
	}

	public static boolean isENABLE_LOGIN() {
		return ENABLE_LOGIN;
	}

	public static boolean isENABLE_HL_LOGIN() {
		return ENABLE_HL_LOGIN;
	}

	public static String getHL_URL() {
		if(ENABLE_HL_LOGIN)
			return HL_URL_WITH_SEC;
		else
			return HL_URL_NO_SEC;
	}

	public static String getHL_LOGIN_URL() {
		return HL_LOGIN_URL;
	}

	/*public static String getChaincodeIDLocalFile() {
		return chaincodeIDFile;
	}*/
}
