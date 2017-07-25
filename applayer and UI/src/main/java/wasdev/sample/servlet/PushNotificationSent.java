/*package wasdev.sample.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import wasdev.sample.servlet.util.ChaincodeHandler;

public class PushNotificationSent {
	private static final boolean DEBUG = true;
	
	public static void pushNotificationSent(HttpServletRequest request, HttpServletResponse response, String data) throws Exception 
	{
		System.out.println("Fetching Push Notification Sent...");
		
		String contractID = request.getParameter("contractID");
		
		if(DEBUG)
			System.out.println("\t" + data);
	
		String secureContext = null;
		HttpSession session = request.getSession(false);
		if(session != null)
			secureContext = session.getAttribute("userID").toString();

		String ccResponse = queryTFChaincode(
				TransactionManager.pushNotificationChaincodeID, 
				"lastPushNotificationSent", 
				new String[]{contractID}, secureContext,
				"Last Push Notification Sent");
			
		System.out.println("\tParsing Response...");
		String parsedResponse = ChaincodeHandler.checkChaincodeResponse(ccResponse, "Last Push Notification Sent", true);
		System.out.println("\t" + parsedResponse);

		if(parsedResponse == null)
			throw new Exception("Error retrieving last push notification sent:\n" + ccResponse);
		else
			response.getWriter().write(parsedResponse);
	}
	
	private static String queryTFChaincode(String chaincodeID, String chaincodeFunction, String[] args, String secureContext, String logStr) 
			throws Exception
	{
		System.out.println("Query CC: ID: " + chaincodeID + " Method: " + chaincodeFunction);
		String requestJSON = ChaincodeHandler.createQueryRequest(chaincodeID, chaincodeFunction, args, secureContext);
		System.out.println("\tRequest: \n\t" + requestJSON);
		String responseJSON = ChaincodeHandler.submitRequest(requestJSON);
		System.out.println("\tResponse: \n\t" + responseJSON);
		
		return responseJSON;
	}
}*/