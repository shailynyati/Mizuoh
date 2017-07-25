package wasdev.sample.servlet;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import wasdev.sample.servlet.session.SessionManager;
import wasdev.sample.servlet.session.Transaction;
import wasdev.sample.servlet.util.ChaincodeHandler;

public class LCIssuanceWorkflowAPI {
	//private static Object lock = new Object();
	
	public static int getNumContractsCreated(String userID) throws Exception
	{
		String numContracts = null;
		numContracts = queryChaincode(TransactionManager.workflowManagementChaincodeID, "getNumContracts", 
				new String[] {}, userID, "Retrieving Number of Contracts");
		if(numContracts == null)
		{
			return -1;
		}
		// Parse list to get the count
		JsonObject numContractsObj = (new JsonParser()).parse(numContracts).getAsJsonObject();
		if(!numContractsObj.has("NumContracts"))
		{
			return -1;
		}
		JsonElement numContractsElem = numContractsObj.get("NumContracts");
		if(!numContractsElem.isJsonPrimitive())
		{
			return -1;
		}
		String numContractsStr = numContractsElem.getAsString();
		int numContractsVal = -1;
		try
		{
			numContractsVal = Integer.parseInt(numContractsStr);
		} catch(NumberFormatException nfe)
		{
			return -1;
		}
		return numContractsVal;
	}
	
	// Generate a unique 4-digit number by incrementing; if we hit the max limit, go back to 0 and restart
	public static String generateUniqueContractID(int num) throws Exception
	{
		int idnum = num % 1000000;
		if(idnum == 0)
		{
			idnum = 1;		// Contract ID can only be a positive integer
		}
		String retVal = Integer.toString(idnum);
		int numZeroes = 6 - retVal.length();
		String prefix = "";
		for(int i = 0 ; i < numZeroes ; i++)
		{
			prefix += "0";
		}
		return prefix + retVal;
	}
	
	private static String generateNewContractID(String userID) throws Exception
	{
		/*synchronized (lock) 
		{
			String refID = "Contract-" + System.currentTimeMillis();
			
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				// ignore - not critical
			}
			
			return refID;
		}*/
		
		int numContracts = getNumContractsCreated(userID);
		if(numContracts < 0)
		{
			return null;
		}
		String contractID = generateUniqueContractID(numContracts + 1);		// We are creating a new contract
		if(contractID == null)
		{
			return null;
		}
		return "Contract-" + contractID;
	}
	
	private static void invokeContractChaincode(String method, String chaincodeID, String chaincodeFunction, String[] args, String secureContext,
			String userRole, String contractID, String logStr) throws Exception
	{
		String requestJSON = ChaincodeHandler.createInvokeRequest(chaincodeID, chaincodeFunction, args, secureContext);
		String responseJSON = ChaincodeHandler.submitRequest(requestJSON);
		String responseUUID = ChaincodeHandler.checkChaincodeResponse(responseJSON, logStr, false);
		if(responseUUID == null)
		{
			throw new Exception("'" + chaincodeFunction + "' chaincode invoke failed; chaincode response: " + responseJSON);
		}
		System.out.println("'" + chaincodeFunction + "' UUID = " + responseUUID);
		SessionManager.putSessionData(new Transaction(contractID, method, responseUUID, secureContext, userRole, false));
	}
	
	private static void invokeContractChaincodeWithParticipants(String method, String chaincodeID, String chaincodeFunction, String[] args, 
			String secureContext, String userRole, String[] participants, String contractID, String logStr) throws Exception
	{
		String requestJSON = ChaincodeHandler.createInvokeRequestWithParticipants(chaincodeID, chaincodeFunction, args, secureContext, participants);
		String responseJSON = ChaincodeHandler.submitRequest(requestJSON);
		String responseUUID = ChaincodeHandler.checkChaincodeResponse(responseJSON, logStr, false);
		if(responseUUID == null)
		{
			throw new Exception("'" + chaincodeFunction + "' chaincode invoke failed; chaincode response: " + responseJSON);
		}
		System.out.println("'" + chaincodeFunction + "' UUID = " + responseUUID);
		SessionManager.putSessionData(new Transaction(contractID, method, responseUUID, secureContext, userRole, false));
	}
	
	public static String queryContractChaincode(String chaincodeID, String chaincodeFunction, String secureContext, String contractID, String logStr) 
			throws Exception
	{
		String requestJSON = ChaincodeHandler.createQueryRequest(chaincodeID, chaincodeFunction, 
				new String[]{contractID}, secureContext);
		String responseJSON = ChaincodeHandler.submitRequest(requestJSON);
		String retVal = ChaincodeHandler.checkChaincodeResponse(responseJSON, logStr, true);
		if(retVal == null)
		{
			throw new Exception("Chaincode returned error or invalid response: " + responseJSON);
		}
		return retVal;
	}
	
	private static String queryChaincode(String chaincodeID, String chaincodeFunction, String[] args, String secureContext, String logStr) 
			throws Exception
	{
		System.out.println("securecontext in fn:"+secureContext);
		String requestJSON = ChaincodeHandler.createQueryRequest(chaincodeID, chaincodeFunction, args, secureContext);
		String responseJSON = ChaincodeHandler.submitRequest(requestJSON);
		String retVal = ChaincodeHandler.checkChaincodeResponse(responseJSON, logStr, true);
		if(retVal == null)
		{
			throw new Exception("Chaincode returned error or invalid response: " + responseJSON);
		}
		return retVal;
	}

	static String getRequestedContractID(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String contractID = request.getParameter("contractID");
		if(contractID == null)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(TransactionManager.getErrorJSON("Contract ID not specified"));
			return null;
		}
		return contractID;
	}
	
	static String createOKStatusObject()
	{
		JsonObject contractObj = new JsonObject();
		contractObj.addProperty("status", "OK");
		return contractObj.toString();
	}
	
	static void handleRequest(HttpServletRequest request, HttpServletResponse response, String method, String data) throws Exception {
		if(data.length() <= 1024)
		{
			System.out.println("Processing method: '" + method + "' ; data: '" + data + "'");
		}
		else
		{
			System.out.println("Processing method: '" + method + "' ; data: '<longer than 1 KB>'");
		}
		String contractID = "";
		String retVal = "";
		System.out.println("Pointer coming into handlereq of lc");
		
		String secureContext = null;
		String userRole = null;
		HttpSession session = request.getSession(false);
		//System.out.println("Session:"+session);
		if(session != null)
		{
			secureContext = session.getAttribute("userID").toString();
			userRole = session.getAttribute("userRole").toString();
		}
		else if(TransactionManager.isENABLE_HL_LOGIN())		// Make sure we have a session when user login is enabled
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(TransactionManager.getErrorJSON("User session not found"));
			return;
		}
		
		

		// Handle each function
		
		// POST requests triggering chaincode invokes
		if(method.equals("submitLC"))
		{
			contractID = generateNewContractID(secureContext);
			if(contractID == null)
			{
				throw new Exception("Unable to create unique contract ID");
			}
			
			if(TransactionManager.isENABLE_HL_LOGIN())
			{
				if(!userRole.equals("Importer Bank"))
				{
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					response.getWriter().print(TransactionManager.getErrorJSON("User '" + secureContext + "' is not an 'ImporterBank' role in this session"));
					return;
				}
				String ebID = request.getParameter("ebID");
				if(ebID == null)
				{
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().print(TransactionManager.getErrorJSON("Exporter's Bank ID not specified"));
					return;
				}
				String iID = request.getParameter("iID");
				if(iID == null)
				{
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().print(TransactionManager.getErrorJSON("Importer's ID not specified"));
					return;
				}
				String eID = request.getParameter("eID");
				if(eID == null)
				{
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().print(TransactionManager.getErrorJSON("Exporter's ID not specified"));
					return;
				}
				if(secureContext.equals(ebID))
				{
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().print(TransactionManager.getErrorJSON("Importer's Bank cannot be the same as Exporter's Bank"));
					return;
				}
				if(secureContext.equals(iID))
				{
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().print(TransactionManager.getErrorJSON("Importer's Bank cannot be the same as Importer"));
					return;
				}
				if(secureContext.equals(eID))
				{
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().print(TransactionManager.getErrorJSON("Importer's Bank cannot be the same as Exporter"));
					return;
				}
				if(ebID.equals(iID))
				{
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().print(TransactionManager.getErrorJSON("Exporter's Bank cannot be the same as Importer"));
					return;
				}
				if(ebID.equals(eID))
				{
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().print(TransactionManager.getErrorJSON("Exporter's Bank cannot be the same as Exporter"));
					return;
				}
				if(iID.equals(eID))
				{
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().print(TransactionManager.getErrorJSON("Importer cannot be the same as Exporter"));
					return;
				}
				String[] chaincodeArgs = new String[] {"", data, iID, eID, secureContext, ebID};//, "1st cert","2nd cert","3rd cert","4th cert"};
				String[] participants = new String[] {iID, eID, secureContext, ebID};
				System.out.println("'submitLC' chaincode Args: " + chaincodeArgs);
				System.out.println("'submitLC' participants: " + participants);
				invokeContractChaincodeWithParticipants(method, TransactionManager.workflowManagementChaincodeID, "submitLC", 
						chaincodeArgs, secureContext, userRole, participants, contractID, "LC Submission");
			}
			else
			{
				invokeContractChaincode(method, TransactionManager.workflowManagementChaincodeID, "submitLC", 
						new String[] {"", data}, secureContext, userRole, contractID, "L/C Submission");
			}
			JsonObject contractObj = new JsonObject();
			contractObj.addProperty("Status", "OK");
			retVal = contractObj.toString();
		}
		else if(method.equals("acceptLC"))
		{
			contractID = getRequestedContractID(request, response);
			if(contractID == null)
			{
				return;
			}
			invokeContractChaincode(method, TransactionManager.workflowManagementChaincodeID, "acceptLC", 
					new String[]{contractID, "comment"}, secureContext, userRole, contractID, "L/C Acceptance");
			retVal = createOKStatusObject();
			if(retVal == null)
			{
				throw new Exception("Unable to create response message");
			}
		}
		else if(method.equals("rejectLC"))
		{
			contractID = getRequestedContractID(request, response);
			if(contractID == null)
			{
				return;
			}
			
			//parse data as json and validate

			/*String rejectionReason = null;
			JsonObject dataJSONObj = (new JsonParser()).parse(data).getAsJsonObject();
			
			if(dataJSONObj.has("rejectionReason"))
			{
				JsonElement rejectionReasonElement = dataJSONObj.get("rejectionReason");
				if(rejectionReasonElement.isJsonObject())
				{
					rejectionReason = rejectionReasonElement.getAsJsonObject().toString();
				}
				else
				{
					throw new Exception("Invalid request. 'rejectionReason' not formatted as a JSON object");
				}
			}*/
			
			
			invokeContractChaincode(method, TransactionManager.workflowManagementChaincodeID, "rejectLC", 
					new String[]{contractID,"comment"}, secureContext, userRole, contractID, "L/C Rejection");
			retVal = createOKStatusObject();
			if(retVal == null)
			{
				throw new Exception("Unable to create response message");
			}
		}
		else if(method.equals("reSubmitLC"))
		{
			//re submit against existing contract Id
			contractID = request.getParameter("contractID");
			
			if(contractID == null)
			{
				return;
			}
			String ebID = request.getParameter("ebID");
			String iID = request.getParameter("iID");
			String eID = request.getParameter("eID");

			boolean validRequest = false; 
			validRequest = validateSubmitLC(request, response, userRole, secureContext, ebID, iID,eID);
			if(validRequest==false){
				return;
			}
			
			//parse data as json and validate
			String mt700 = null;
			String reSubmitReason = null;
			JsonObject dataJSONObj = (new JsonParser()).parse(data).getAsJsonObject();
			if(dataJSONObj.has("MT700"))
			{
				JsonElement mt700Element = dataJSONObj.get("MT700");
				if(mt700Element.isJsonObject())
				{
					mt700 = mt700Element.getAsJsonObject().toString();
				}
				else
				{
					throw new Exception("Invalid request. 'MT700' not formatted as a JSON object");
				}
			}
			if(dataJSONObj.has("reSubmitReason"))
			{
				JsonElement reSubmitReasonElement = dataJSONObj.get("reSubmitReason");
				if(reSubmitReasonElement.isJsonObject())
				{
					reSubmitReason = reSubmitReasonElement.getAsJsonObject().toString();
				}
				else
				{
					throw new Exception("Invalid request. 'reSubmitReason' not formatted as a JSON object");
				}
			}
			
			//TODO - data has to converted to mt700 and resubmissionReason
				String[] chaincodeArgs = new String[] {contractID, data, iID, eID, secureContext, ebID, iID, eID};
				String[] participants = new String[] {iID, eID, secureContext, ebID};
				System.out.println("'reSubmitLC' chaincode Args: " + chaincodeArgs);
				System.out.println("'reSubmitLC' participants: " + participants);
				invokeContractChaincodeWithParticipants(method, TransactionManager.workflowManagementChaincodeID, "reSubmitLC", 
						chaincodeArgs, secureContext, userRole, participants, contractID, " LC Re-Submission");
		}
		
		// GET requests triggering chaincode queries
		else if(method.equals("validateLC"))
		{
			retVal = queryChaincode(TransactionManager.workflowManagementChaincodeID, "validateLC", 
					new String[] {data}, secureContext, "Validate L/C Contents");
		}
		else if(method.equals("getLC"))
		{
			contractID = getRequestedContractID(request, response);
			if(contractID == null)
			{
				return;
			}
			retVal = queryContractChaincode(TransactionManager.workflowManagementChaincodeID, "getLC", secureContext,
					contractID, "Retrieving L/C Contents");
		}
		else if(method.equals("getLCStatus"))
		{
			contractID = getRequestedContractID(request, response);
			if(contractID == null)
			{
				return;
			}
			retVal = queryContractChaincode(TransactionManager.workflowManagementChaincodeID, "getLCStatus", secureContext, 
					contractID, "Retrieving L/C State");
		}
		else if(method.equals("listContracts"))
		{
			System.out.println("Secure Context:"+secureContext);
			retVal = queryChaincode(TransactionManager.workflowManagementChaincodeID, "listContracts", 
					new String[] {}, secureContext, "Retrieving List of Contracts");
		}
		else if(method.equals("listContractsByRole"))
		{
			String role = request.getParameter("role");
			if(role == null)
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().print(TransactionManager.getErrorJSON("No role specified"));
				return;
			}
			retVal = queryChaincode(TransactionManager.workflowManagementChaincodeID, "listContractsByRole", 
					new String[] {role}, secureContext, "Retrieving List of Contracts by Role");
		}
		else if(method.equals("listContractsByRoleName"))
		{
			String role = request.getParameter("role");
			String roleid = request.getParameter("roleID");
			System.out.println("role:"+role);
			System.out.println("roleid:"+roleid);
			if(role == null)
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().print(TransactionManager.getErrorJSON("No role specified"));
				return;
			}
			retVal = queryChaincode(TransactionManager.workflowManagementChaincodeID, "listContractsByRoleName", 
					new String[] {role,roleid}, secureContext, "Retrieving List of Contracts by Role");
		}
		else if(method.equals("listLCsByStatus"))
		{
			String status = request.getParameter("status");
			if(status == null)
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().print(TransactionManager.getErrorJSON("No L/C status specified"));
				return;
			}
			retVal = queryChaincode(TransactionManager.workflowManagementChaincodeID, "listLCsByStatus", 
					new String[] {status}, secureContext, "Retrieving List of L/Cs by Status");
		}
		else	// Requested function does not match any known API call
		{
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().print(TransactionManager.getErrorJSON("Function not supported"));
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().print(retVal);
	}
	
    // Add function names to the appropriate arrays
	public static ArrayList<String> getLCIssuanceFunctions()
	{
		ArrayList<String> functions = new ArrayList<String>();
		
        // Add invocation functions
		functions.add("submitLC");
		functions.add("acceptLC");
		functions.add("rejectLC");
		functions.add("reSubmitLC");
		
        
        // Add query functions
		functions.add("validateLC");
		functions.add("getLC");
		functions.add("getLCStatus");
		functions.add("listContracts");
		functions.add("listContractsByRole");
		functions.add("listContractsByRoleName");
		functions.add("listLCsByStatus");
        
		return functions;
	}
	
	private static boolean validateSubmitLC(HttpServletRequest request, HttpServletResponse response, String userRole, String secureContext, String ebID, String iID, String eID) throws Exception{
		if(TransactionManager.isENABLE_HL_LOGIN())
		{
			if(!userRole.equals("ImporterBank"))
			{
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.getWriter().print(TransactionManager.getErrorJSON("User '" + secureContext + "' is not an 'ImporterBank' role in this session"));
				return false;
			}
			if(ebID == null)
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().print(TransactionManager.getErrorJSON("Exporter's Bank ID not specified"));
				return false;
			}

			if(iID == null)
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().print(TransactionManager.getErrorJSON("Importer's ID not specified"));
				return false;
			}

			if(eID == null)
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().print(TransactionManager.getErrorJSON("Exporter's ID not specified"));
				return false;
			}
			if(secureContext.equals(ebID))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().print(TransactionManager.getErrorJSON("Importer's Bank cannot be the same as Exporter's Bank"));
				return false;
			}
			if(secureContext.equals(iID))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().print(TransactionManager.getErrorJSON("Importer's Bank cannot be the same as Importer"));
				return false;
			}
			if(secureContext.equals(eID))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().print(TransactionManager.getErrorJSON("Importer's Bank cannot be the same as Exporter"));
				return false;
			}
			if(ebID.equals(iID))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().print(TransactionManager.getErrorJSON("Exporter's Bank cannot be the same as Importer"));
				return false;
			}
			if(ebID.equals(eID))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().print(TransactionManager.getErrorJSON("Exporter's Bank cannot be the same as Exporter"));
				return false;
			}
			if(iID.equals(eID))
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().print(TransactionManager.getErrorJSON("Importer cannot be the same as Exporter"));
				return false;
			}

	}
		return true;
	}
	
}
