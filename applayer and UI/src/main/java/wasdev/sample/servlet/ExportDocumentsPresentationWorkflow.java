package wasdev.sample.servlet;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import wasdev.sample.servlet.session.SessionManager;
import wasdev.sample.servlet.session.Transaction;
import wasdev.sample.servlet.util.ChaincodeHandler;

public class ExportDocumentsPresentationWorkflow {
	private static HashMap<String, String> m2mMapping = new HashMap<String, String>();
	static
	{
		m2mMapping.put("validateED", "validateED");
		m2mMapping.put("submitED", "submitED");
		m2mMapping.put("acceptED", "acceptED");
		m2mMapping.put("acceptToPay", "acceptToPay");	
		m2mMapping.put("rejectED", "rejectED");		
		m2mMapping.put("paymentReceived", "paymentReceived");
		m2mMapping.put("defaultedOnPayment", "defaultedOnPayment");
		m2mMapping.put("getED", "getED");
		m2mMapping.put("getEDStatus", "getEDStatus");
		m2mMapping.put("listEDsByStatus", "listEDsByStatus");
		m2mMapping.put("reSubmitED", "reSubmitED");
	
	}	

	static void handleRequest(HttpServletRequest request, HttpServletResponse response, String method, String data) throws Exception {
		if(data.length() <= 1024)
			System.out.println("Processing method: '" + method + "' ; data: '" + data + "'");
		else
			System.out.println("Processing method: '" + method + "' ; data: '<longer than 1 KB>'");
		
		if(method.equalsIgnoreCase("validateED"))
			validateED(request, response, data);
		else if(method.equalsIgnoreCase("submitED"))
			submitED(request, response, data);
		else if(method.equalsIgnoreCase("acceptED"))
			acceptED(request, response, data);
		else if(method.equalsIgnoreCase("acceptToPay"))
			acceptToPay(request, response, data);
		else if(method.equalsIgnoreCase("rejectED"))
			rejectED(request, response, data);
		else if(method.equalsIgnoreCase("paymentReceived"))
			paymentReceived(request, response, data);
		else if(method.equalsIgnoreCase("defaultedOnPayment"))
			defaultedOnPayment(request, response, data);
		else if(method.equalsIgnoreCase("getED"))
			getED(request, response, data);	
		else if(method.equalsIgnoreCase("getEDStatus"))
			getEDStatus(request, response, data);
		else if(method.equalsIgnoreCase("listEDsByStatus"))
			listEDsByStatus(request, response, data);	
		else if(method.equalsIgnoreCase("reSubmitED"))
			reSubmitED(request, response, data);
	}
	
	public static void validateED(HttpServletRequest request, HttpServletResponse response, String data) throws Exception 
	{
		String contractID = request.getParameter("contractID");
		if(contractID == null)
			throw new Exception("Invalid request - ContractID missing");
		
		String secureContext = null;
		HttpSession session = request.getSession(false);
		if(session != null)
			secureContext = session.getAttribute("userID").toString();
		
		String invoice = null;
		String billOfLading = null;
		String packagingList = null;
		
		JsonObject dataJSONObj = (new JsonParser()).parse(data).getAsJsonObject();
		if(dataJSONObj.has("Invoice"))
		{
			JsonElement invoiceElem = dataJSONObj.get("Invoice");
			if(invoiceElem.isJsonObject())
			{
				invoice = invoiceElem.getAsJsonObject().toString();
			}
			else
			{
				throw new Exception("Invalid request. 'Invoice' not formatted as a JSON object");
			}
		}
		else
		{
			throw new Exception("Invalid request. Missing or malformed 'Invoice Data'");
		}
		
		if(dataJSONObj.has("BillOfLading"))
		{
			JsonElement billOfLadingElem = dataJSONObj.get("BillOfLading");
			if(billOfLadingElem.isJsonObject())
			{
				billOfLading = billOfLadingElem.getAsJsonObject().toString();
			}
			else
			{
				throw new Exception("Invalid request. 'BillOfLading' not formatted as a JSON object");
			}
		}
		else
		{
			throw new Exception("Invalid request. Missing or malformed 'Bill of Lading'");
		}
		
		if(dataJSONObj.has("PackagingList"))
		{
			JsonElement packagingListElem = dataJSONObj.get("PackagingList");
			if(packagingListElem.isJsonObject())
			{
				packagingList = packagingListElem.getAsJsonObject().toString();
			}
			else
			{
				throw new Exception("Invalid request. 'PackagingList' not formatted as a JSON object");
			}
		}
		else
		{
			throw new Exception("Invalid request. Missing or malformed 'Packaging List'");
		}
		
		/*System.out.println("\tInvoice:\n\t" + invoice);
		System.out.println("\tBill of Lading:\n\t" + billOfLading);
		System.out.println("\tPackaging List:\n\t" + packagingList);*/
		
		String ccResponse = queryTFChaincode(
			TransactionManager.workflowManagementChaincodeID, 
			"validateED", 
			new String[]{contractID, billOfLading, invoice, packagingList}, secureContext, 
			"validateED");
		
		System.out.println("\tParsing Response...");
		String parsedResponse = ChaincodeHandler.checkChaincodeResponse(ccResponse, "validateED", true);
		System.out.println("\t" + parsedResponse);

		if(parsedResponse == null)
			throw new Exception("Error validating Export Document:\n" + ccResponse);
		else
			response.getWriter().write(parsedResponse);
	}
	
	public static void submitED(HttpServletRequest request, HttpServletResponse response, String data) throws Exception {	
		String contractID = request.getParameter("contractID");
		if(contractID == null)
			throw new Exception("Invalid request - ContractID missing");
		
		String secureContext = null;
		String userRole = null;
		HttpSession session = request.getSession(false);
		if(session != null)
		{
			secureContext = session.getAttribute("userID").toString();
			userRole = session.getAttribute("userRole").toString();
		}

		String invoice = null;
		String invoicePDF = null;
		String billOfLading = null;
		String billOfLadingPDF = null;
		String packagingList = null;
		String packagingListPDF = null;
		String shippingCompany = null;
		String insuranceCompany = null;
		
		//System.out.println("data of submit ED:"+data);
		
		JsonObject dataJSONObj = (new JsonParser()).parse(data).getAsJsonObject();
		if(dataJSONObj.has("Invoice"))
		{
			JsonElement invoiceElem = dataJSONObj.get("Invoice");
			if(invoiceElem.isJsonObject())
			{
				invoice = invoiceElem.getAsJsonObject().toString();
			}
			else
			{
				throw new Exception("Invalid request. 'Invoice' not formatted as a JSON object");
			}
		}

		if(dataJSONObj.has("InvoicePDF"))
			invoicePDF = dataJSONObj.get("InvoicePDF").getAsString();
		else
			throw new Exception("Invalid request. Missing 'InvoicePDF Data'");

		if(dataJSONObj.has("BillOfLading"))
		{
			JsonElement billOfLadingElem = dataJSONObj.get("BillOfLading");
			if(billOfLadingElem.isJsonObject())
			{
				billOfLading = billOfLadingElem.getAsJsonObject().toString();
			}
			else
			{
				throw new Exception("Invalid request. 'BillOfLading' not formatted as a JSON object");
			}
		}
		
		if(dataJSONObj.has("BillOfLadingPDF"))
			billOfLadingPDF = dataJSONObj.get("BillOfLadingPDF").getAsString();
		else
			throw new Exception("Invalid request. Missing 'BillOfLadingPDF'");
		
		if(dataJSONObj.has("PackagingList"))
		{
			JsonElement packagingListElem = dataJSONObj.get("PackagingList");
			if(packagingListElem.isJsonObject())
			{
				packagingList = packagingListElem.getAsJsonObject().toString();
			}
			else
			{
				throw new Exception("Invalid request. 'PackagingList' not formatted as a JSON object");
			}
		}

		if(dataJSONObj.has("PackagingListPDF"))
			packagingListPDF = dataJSONObj.get("PackagingListPDF").getAsString();
		else
			throw new Exception("Invalid request. Missing 'PackagingListPDF'");
		
		if(dataJSONObj.has("ShippingCompany"))
			shippingCompany = dataJSONObj.get("ShippingCompany").getAsString();
		
		if(dataJSONObj.has("InsuranceConpany"))
			insuranceCompany = dataJSONObj.get("InsuranceConpany").getAsString();
		
		System.out.println("InsuranceConpany:"+insuranceCompany);
		System.out.println("ShippingCompany:"+shippingCompany);

		System.out.println("\tInvoice:\n\t" + invoice);
		//System.out.println("\tInvoice PDF:\n\t" + invoicePDF);
		System.out.println("\tBill of Lading:\n\t" + billOfLading);
	//	System.out.println("\tBill of Lading PDF:\n\t" + billOfLadingPDF);
		System.out.println("\tPackaging List:\n\t" + packagingList);
	//	System.out.println("\tPackaging List PDF:\n\t" + packagingListPDF);
		
		String ccResponse = invokeTFChaincode(TransactionManager.workflowManagementChaincodeID,"submitED", new String[]{contractID, billOfLadingPDF, invoicePDF, packagingListPDF, billOfLading, invoice, packagingList,shippingCompany,insuranceCompany }, secureContext,"submitED");

		System.out.println("\tParsing Response...");
		String txUUID = ChaincodeHandler.checkChaincodeResponse(ccResponse, "submitED", false);
		System.out.println("\t" + txUUID);

		if(txUUID == null)
			throw new Exception("Error submitting Export Application:\n" + ccResponse);

		SessionManager.putSessionData(new Transaction(contractID, "submitED", txUUID, secureContext, userRole, false));

		response.getWriter().write(createOKStatusJSON());
	}
	
	public static void getED(HttpServletRequest request, HttpServletResponse response, String data) throws Exception {		
		String contractID = request.getParameter("contractID");
		if(contractID == null)
			throw new Exception("Invalid request - ContractID missing");

		String docType = request.getParameter("docType");
		if(docType == null)
			throw new Exception("Invalid request - Document Type missing");

		String docFormat = request.getParameter("docFormat");
		if(docFormat == null)
			throw new Exception("Invalid request - Document Format missing");

		String secureContext = null;
		HttpSession session = request.getSession(false);
		if(session != null)
			secureContext = session.getAttribute("userID").toString();
		
		String ccResponse = queryTFChaincode(
			TransactionManager.workflowManagementChaincodeID, 
			"getED", 
			new String[]{contractID, docType, docFormat}, secureContext, 
			"getED");
		
		System.out.println("\tParsing Response...");
		String parsedResponse = ChaincodeHandler.checkChaincodeResponse(ccResponse, "getED", true);
		System.out.println("\t" + parsedResponse);

		if(parsedResponse == null)
			throw new Exception("Error retrieving Export Document  [Contract ID: " + contractID + "]:\n" + ccResponse);
		else
			response.getWriter().write(parsedResponse);
	}
	
	public static void getEDStatus(HttpServletRequest request, HttpServletResponse response, String data) throws Exception {		
		String contractID = request.getParameter("contractID");
		if(contractID == null)
			throw new Exception("Invalid request - ContractID missing");
		
		String secureContext = null;
		HttpSession session = request.getSession(false);
		if(session != null)
			secureContext = session.getAttribute("userID").toString();
		
		String ccResponse = queryTFChaincode(
			TransactionManager.workflowManagementChaincodeID, 
			"getEDStatus", 
			new String[]{contractID}, secureContext, 
			"getEDStatus");
		
		System.out.println("\tParsing Response...");
		String parsedResponse = ChaincodeHandler.checkChaincodeResponse(ccResponse, "getEDStatus", true);
		System.out.println("\t" + parsedResponse);

		if(parsedResponse == null)
			throw new Exception("Error retrieving Export Document Status [Contract ID: " + contractID + "]:\n" + ccResponse);
		else
			response.getWriter().write(parsedResponse);
	}
	
	public static void listEDsByStatus(HttpServletRequest request, HttpServletResponse response, String data) throws Exception {		
		String status = request.getParameter("status");

		if(status == null)
			throw new Exception("Invalid request - ED status missing");

		String[] ccRequest = null;

		/*
		if(TransactionManager.isENABLE_HL_LOGIN())
		{
			HttpSession session = request.getSession(false);
			String userID = session.getAttribute("userID").toString();
			String userRole = session.getAttribute("userRole").toString();

			ccRequest = new String[]{userID, userRole, status};
		}
		else
			ccRequest = new String[]{status};
*/
		ccRequest = new String[]{status};
		
		String secureContext = null;
		HttpSession session = request.getSession(false);
		if(session != null)
			secureContext = session.getAttribute("userID").toString();


		String ccResponse = queryTFChaincode(
			TransactionManager.workflowManagementChaincodeID, 
			"listEDsByStatus", 
			ccRequest, secureContext,
			"listEDsByStatus");

		System.out.println("\tParsing Response...");
		String parsedResponse = ChaincodeHandler.checkChaincodeResponse(ccResponse, "listEDsByStatus", true);
		System.out.println("\t" + parsedResponse);

		if(parsedResponse == null)
			throw new Exception("Error retrieving Export Documents by Status:\n" + ccResponse);
		else
			response.getWriter().write(parsedResponse);
	} 
	
	public static void acceptED(HttpServletRequest request, HttpServletResponse response, String data) throws Exception {
		String contractID = request.getParameter("contractID");
		if(contractID == null)
			throw new Exception("Invalid request - ContractID missing");
		
		String secureContext = null;
		String userRole = null;
		HttpSession session = request.getSession(false);
		if(session != null)
		{
			secureContext = session.getAttribute("userID").toString();
			userRole = session.getAttribute("userRole").toString();
		}
		
		String ccResponse = invokeTFChaincode(
			TransactionManager.workflowManagementChaincodeID, 
			"acceptED", 
			new String[]{contractID}, "admin",
			"acceptED");

		System.out.println("\tParsing Response...");
		String txUUID = ChaincodeHandler.checkChaincodeResponse(ccResponse, "acceptED", false);
		System.out.println("\t" + txUUID);

		if(txUUID == null)
			throw new Exception("Error accepting ED: " + ccResponse);

		SessionManager.putSessionData(new Transaction(contractID, "acceptED", txUUID, secureContext, userRole, false));
		
		response.getWriter().write(createOKStatusJSON());
	}
	
	public static void rejectED(HttpServletRequest request, HttpServletResponse response, String data) throws Exception {		
		String contractID = request.getParameter("contractID");
		if(contractID == null)
			throw new Exception("Invalid request - ContractID missing");
		
		String secureContext = null;
		String userRole = null;
		HttpSession session = request.getSession(false);
		if(session != null)
		{
			secureContext = session.getAttribute("userID").toString();
			userRole = session.getAttribute("userRole").toString();
		}
				
		    String ccResponse = invokeTFChaincode(
			TransactionManager.workflowManagementChaincodeID, 
			"rejectED", 
			new String[]{contractID}, "admin",
			"rejectED");

		System.out.println("\tParsing Response...");
		String txUUID = ChaincodeHandler.checkChaincodeResponse(ccResponse, "rejectED", false);
		System.out.println("\t" + txUUID);

		if(txUUID == null)
			throw new Exception("Error rejecting ED: " + ccResponse);

		SessionManager.putSessionData(new Transaction(contractID, "rejectED", txUUID, secureContext, userRole, false));
		
		response.getWriter().write(createOKStatusJSON());
	}
			
	public static void acceptToPay(HttpServletRequest request, HttpServletResponse response, String data) throws Exception {		
		String contractID = request.getParameter("contractID");
		if(contractID == null)
			throw new Exception("Invalid request - ContractID missing");
		
		String secureContext = null;
		String userRole = null;
		HttpSession session = request.getSession(false);
		if(session != null)
		{
			secureContext = session.getAttribute("userID").toString();
			userRole = session.getAttribute("userRole").toString();
		}

		String ccResponse = invokeTFChaincode(
			TransactionManager.workflowManagementChaincodeID, 
			"acceptToPay", 
			new String[]{contractID}, secureContext,
			"acceptToPay");

		System.out.println("\tParsing Response...");
		String txUUID = ChaincodeHandler.checkChaincodeResponse(ccResponse, "acceptToPay", false);
		System.out.println("\t" + txUUID);

		if(txUUID == null)
			throw new Exception("Error executing chaincode - 'acceptToPay' method [Contract ID: " + contractID + "]\n" + ccResponse);

		SessionManager.putSessionData(new Transaction(contractID, "acceptToPay", txUUID, secureContext, userRole, false));
		
		response.getWriter().write(createOKStatusJSON());
	}
	
	public static void paymentReceived(HttpServletRequest request, HttpServletResponse response, String data) throws Exception {		
		String contractID = request.getParameter("contractID");
		if(contractID == null)
			throw new Exception("Invalid request - ContractID missing");
		
		String secureContext = null;
		String userRole = null;
		HttpSession session = request.getSession(false);
		if(session != null)
		{
			secureContext = session.getAttribute("userID").toString();
			userRole = session.getAttribute("userRole").toString();
		}

		String ccResponse = invokeTFChaincode(
			TransactionManager.workflowManagementChaincodeID, 
			"paymentReceived", 
			new String[]{contractID}, secureContext,
			"paymentReceived");

		System.out.println("\tParsing Response...");
		String txUUID = ChaincodeHandler.checkChaincodeResponse(ccResponse, "paymentReceived", false);
		System.out.println("\t" + txUUID);

		if(txUUID == null)
			throw new Exception("Error executing chaincode - 'paymentReceived' method [Contract ID: " + contractID + "]\n" + ccResponse);

		SessionManager.putSessionData(new Transaction(contractID, "paymentReceived", txUUID, secureContext, userRole, false));
		
		response.getWriter().write(createOKStatusJSON());
	}
	
	public static void defaultedOnPayment(HttpServletRequest request, HttpServletResponse response, String data) throws Exception {		
		String contractID = request.getParameter("contractID");
		if(contractID == null)
			throw new Exception("Invalid request - ContractID missing");
		
		String secureContext = null;
		String userRole = null;
		HttpSession session = request.getSession(false);
		if(session != null)
		{
			secureContext = session.getAttribute("userID").toString();
			userRole = session.getAttribute("userRole").toString();
		}

		String ccResponse = invokeTFChaincode(
			TransactionManager.workflowManagementChaincodeID, 
			"defaultedOnPayment", 
			new String[]{contractID}, secureContext,
			"defaultedOnPayment");

		System.out.println("\tParsing Response...");
		String txUUID = ChaincodeHandler.checkChaincodeResponse(ccResponse, "defaultedOnPayment", false);
		System.out.println("\t" + txUUID);

		if(txUUID == null)
			throw new Exception("Error executing chaincode - 'defaultedOnPayment' method [Contract ID: " + contractID + "]\n" + ccResponse);

		SessionManager.putSessionData(new Transaction(contractID, "defaultedOnPayment", txUUID, secureContext, userRole, false));
		
		response.getWriter().write(createOKStatusJSON());
	}
	
	private static String invokeTFChaincode(String chaincodeID, String chaincodeFunction, String[] args, String secureContext, String logStr) throws Exception
	{
		//System.out.println("Query CC: ID: " + chaincodeID + " Method: " + chaincodeFunction);
		String requestJSON = ChaincodeHandler.createInvokeRequest(chaincodeID, chaincodeFunction, args, secureContext);
		System.out.println("\tRequest: \n\t" + requestJSON);
		String responseJSON = ChaincodeHandler.submitRequest(requestJSON);
		//System.out.println("\tResponse: \n\t" + responseJSON);
		
		return responseJSON;		
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
	
	private static String createOKStatusJSON()
	{
		JsonObject contractObj = new JsonObject();
		contractObj.addProperty("status", "OK");
		return contractObj.toString();
	}
	
	public static ArrayList<String> getExportDocumentsPresentationFunctions()
	{
		return new ArrayList<String>(m2mMapping.keySet());
	}
	
	
	
	public static void reSubmitED(HttpServletRequest request, HttpServletResponse response, String data) throws Exception {	
		
		String contractID = request.getParameter("contractID");
		if(contractID == null)
			throw new Exception("Invalid request - ContractID missing");
		
		String secureContext = null;
		String userRole = null;
		HttpSession session = request.getSession(false);
		if(session != null)
		{
			secureContext = session.getAttribute("userID").toString();
			userRole = session.getAttribute("userRole").toString();
		}

		String invoice = null;
		String invoicePDF = null;
		String billOfLading = null;
		String billOfLadingPDF = null;
		String packagingList = null;
		String packagingListPDF = null;
		String reSubmitReason = null;
		
		JsonObject dataJSONObj = (new JsonParser()).parse(data).getAsJsonObject();
		if(dataJSONObj.has("Invoice"))
		{
			JsonElement invoiceElem = dataJSONObj.get("Invoice");
			if(invoiceElem.isJsonObject())
			{
				invoice = invoiceElem.getAsJsonObject().toString();
			}
			else
			{
				throw new Exception("Invalid request. 'Invoice' not formatted as a JSON object");
			}
		}

		if(dataJSONObj.has("InvoicePDF"))
			invoicePDF = dataJSONObj.get("InvoicePDF").getAsString();
		else
			throw new Exception("Invalid request. Missing 'InvoicePDF Data'");

		if(dataJSONObj.has("BillOfLading"))
		{
			JsonElement billOfLadingElem = dataJSONObj.get("BillOfLading");
			if(billOfLadingElem.isJsonObject())
			{
				billOfLading = billOfLadingElem.getAsJsonObject().toString();
			}
			else
			{
				throw new Exception("Invalid request. 'BillOfLading' not formatted as a JSON object");
			}
		}
		
		if(dataJSONObj.has("BillOfLadingPDF"))
			billOfLadingPDF = dataJSONObj.get("BillOfLadingPDF").getAsString();
		else
			throw new Exception("Invalid request. Missing 'BillOfLadingPDF'");
		
		if(dataJSONObj.has("PackagingList"))
		{
			JsonElement packagingListElem = dataJSONObj.get("PackagingList");
			if(packagingListElem.isJsonObject())
			{
				packagingList = packagingListElem.getAsJsonObject().toString();
			}
			else
			{
				throw new Exception("Invalid request. 'PackagingList' not formatted as a JSON object");
			}
		}

		if(dataJSONObj.has("PackagingListPDF"))
			packagingListPDF = dataJSONObj.get("PackagingListPDF").getAsString();
		else
			throw new Exception("Invalid request. Missing 'PackagingListPDF'");
		
		// validate for reSubmitReason
		
		if(dataJSONObj.has("reSubmitReason"))
			billOfLadingPDF = dataJSONObj.get("reSubmitReason").getAsString();
		else
			throw new Exception("Invalid request. Missing 'reSubmitReason'");
		
		System.out.println("\tInvoice:\n\t" + invoice);
		System.out.println("\tInvoice PDF:\n\t" + invoicePDF);
		System.out.println("\tBill of Lading:\n\t" + billOfLading);
		System.out.println("\tBill of Lading PDF:\n\t" + billOfLadingPDF);
		System.out.println("\tPackaging List:\n\t" + packagingList);
		System.out.println("\tPackaging List PDF:\n\t" + packagingListPDF);
		System.out.println("\tPackaging List PDF:\n\t" + reSubmitReason);
		
		String ccResponse = invokeTFChaincode(
			TransactionManager.workflowManagementChaincodeID, 
			"resubmitED", 
			new String[]{contractID, billOfLadingPDF, invoicePDF, packagingListPDF, billOfLading, invoice, packagingList, reSubmitReason}, secureContext, 
			"resubmitED");

		System.out.println("\tParsing Response...");
		String txUUID = ChaincodeHandler.checkChaincodeResponse(ccResponse, "submitED", false);
		System.out.println("\t" + txUUID);

		if(txUUID == null)
			throw new Exception("Error submitting Export Application:\n" + ccResponse);

		SessionManager.putSessionData(new Transaction(contractID, "submitED", txUUID, secureContext, userRole, false));

		response.getWriter().write(createOKStatusJSON());
	}
	
}