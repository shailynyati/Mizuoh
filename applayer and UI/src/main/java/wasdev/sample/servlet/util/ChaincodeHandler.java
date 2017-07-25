package wasdev.sample.servlet.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream; 
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Iterator;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import wasdev.sample.servlet.TransactionManager;

public class ChaincodeHandler {
	// Only for local machine 'dev' mode deployment
	// For remote 'net' mode deployment to the peers
	//private static final String chaincodeURL = "http://192.168.42.53:5000/chaincode/";
	/*private static final String pushNotificationChaincodeName = "df0b2214515349a35cf1f9152bade9b1022b4bc515c445d1a5cfbc696f215eaac" +
												"269a15b2093b3d8d352e9be2a2c9db56044bc7868564a3380f5e44f30bb2929";*/
	
	/*public static void recordPushNotificationSent(String contractID, String pushNotificationSent, String secureContext) throws Exception
	{
		System.out.println("Going to invoke push notification recording chaincode for " + pushNotificationSent);
		String request = createInvokeRequest(TransactionManager.pushNotificationChaincodeID, "recordPushNotificationSent", 
				new String[]{contractID, pushNotificationSent}, secureContext);
		String response = submitRequest(request);
		System.out.println("Response from push notification recording chaincode: " + response);
		String responseUUID = checkChaincodeResponse(response, "Push notification attestation of " + pushNotificationSent, false);
		if(responseUUID != null)
		{
			System.out.println("Push notification attestation UUID: " + responseUUID);
		}
		else
		{
			System.out.println("Error recording push notification in the ledger");
		}
	}*/
	
	// 'isQuery' is true for query requests, and false for deploy or invoke requests
	public static String checkChaincodeResponse(String response, String logStr, boolean isQuery)
	{
		/* Parse the response to catch errors (if any)
		 */
		System.out.println("Chaincode response: " + response);
		try
		{
			JsonObject responseObj = (new JsonParser()).parse(response).getAsJsonObject();
			Set<Entry<String, JsonElement>> responseFields = responseObj.entrySet();
			Iterator<Entry<String, JsonElement>> fieldsIterator = responseFields.iterator();
			while(fieldsIterator.hasNext())
			{
				Entry<String, JsonElement> field = fieldsIterator.next();
				if(field.getKey().equals("result"))
				{
					// Good response; verify that 'status' is 'OK'
					JsonElement fieldValue = field.getValue();
					if(fieldValue instanceof JsonObject)
					{
						JsonObject resultObj = fieldValue.getAsJsonObject();
						Set<Entry<String, JsonElement>> resultFields = resultObj.entrySet();
						Iterator<Entry<String, JsonElement>> resultFieldsIter = resultFields.iterator();
						boolean status = false;
						String message = null;
						while(resultFieldsIter.hasNext())
						{
							Entry<String, JsonElement> resultEntry = resultFieldsIter.next();
							if(resultEntry.getKey().equals("status"))
							{
								status = resultEntry.getValue().getAsString().equals("OK");
							}
							if(resultEntry.getKey().equals("message"))
							{
								message = resultEntry.getValue().getAsString();
							}
						}
						if(status)
						{
							if(message == null)
							{
								message = "";		// 'message' field may be missing even in a valid response
							}
							if(isQuery)
							{
								System.out.println("Query for " + logStr + " from ledger succeeded");
							}
							else
							{
								System.out.println(logStr + " on ledger succeeded");
							}
							return message;
						}
					}
				}
				if(field.getKey().equals("error"))
				{
					// Something bad happened; parse and print 'message'; raise workflow exception
					JsonElement fieldValue = field.getValue();
					String errorStr = fieldValue.toString();
					if(isQuery)
					{
						throw new Exception("Query for " + logStr + " from ledger failed with error: " + errorStr);
					}
					else
					{
						throw new Exception(logStr + " on ledger failed with error: " + errorStr);
					}
				}
			}
		} catch(Exception e)
		{
			e.printStackTrace();
			// TODO Crash and/or notify Mobile App?
		}
		System.out.println("Unable to parse response for: " + logStr);
		return null;
	}
	
	public static String submitRequest(String request) throws Exception
	{
		HttpURLConnection con = getChaincodeConnection(request.length());
		DataOutputStream dos = new DataOutputStream(con.getOutputStream());
		// Test code (if test flag is set): disable event tracking for the transaction we are invoking below
		if(TradeFinanceApplicationConstants.eventManagerTestMode)
		{
			TradeFinanceApplicationConstants.testIgnoreNextUUID = true;
		}
		dos.write(request.getBytes());
		dos.write(("\r\n").getBytes());
		dos.flush();
		dos.close();
		
		//Get response
		InputStream is;
		if(con.getResponseCode() == 200)
		{
			is = con.getInputStream();
		}
		else
		{
			is = con.getErrorStream();
		}
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	    String line;
	    StringBuffer response = new StringBuffer(); 
		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}
		rd.close();
		is.close();
		
		con.disconnect();
		con = null;
		
		return response.toString();
	}
	
	public static String createDeployRequest(String chaincodePath, String function, String[] args, String secureContext)
	{
		return createRequest("deploy", 1, 1, chaincodePath, function, args, secureContext, null);
	}
	
	public static String createInvokeRequest(String chaincodeName, String function, String[] args, String secureContext)
	{
		return createRequest("invoke", 1, 3, chaincodeName, function, args, secureContext, null);
	}
	
	public static String createInvokeRequestWithParticipants(String chaincodeName, String function, String[] args, String secureContext, 
			String[] participants)
	{
		return createRequest("invoke", 1, 3, chaincodeName, function, args, secureContext, participants);
	}
	
	public static String createQueryRequest(String chaincodeName, String function, String[] args, String secureContext)
	{
		return createRequest("query", 1, 5, chaincodeName, function, args, secureContext, null);
	}
	
	private static String createRequest(String method, int type, int id, String chaincodeName, String function, String[] args, String secureContext, 
			String[] participants)
	{
		JsonObject requestObj = new JsonObject();
		requestObj.addProperty("jsonrpc", "2.0");
		requestObj.addProperty("method", method);
		JsonObject paramsObj = new JsonObject();
		paramsObj.addProperty("type", type);
		JsonObject chaincodeObj = new JsonObject();
		if(method.equals("deploy"))
		{
			chaincodeObj.addProperty("path", chaincodeName);
		}
		else
		{
			chaincodeObj.addProperty("name", chaincodeName);
		}
		paramsObj.add("chaincodeID", chaincodeObj);
		JsonObject ctorObj = new JsonObject();
		//ctorObj.addProperty("function", function);
		
		JsonArray argsList = new JsonArray();
		argsList.add(new JsonPrimitive(function));
		for(int i = 0 ; i < args.length ; i++)
		{
			argsList.add(new JsonPrimitive(args[i]));
		}
		ctorObj.add("args", argsList);
		paramsObj.add("ctorMsg", ctorObj);

		//add secureContext to request JSON
		//if(!method.equals("deploy") && TransactionManager.isENABLE_HL_LOGIN() && secureContext != null)
		if(TransactionManager.isENABLE_HL_LOGIN() && secureContext != null)
			paramsObj.addProperty("secureContext", secureContext);
		
		requestObj.add("params", paramsObj);

		// Participants' certificates need to be extracted by the security middleware
		if(participants != null)
		{
			JsonArray participantsList = new JsonArray();
			for(int i = 0 ; i < participants.length ; i++)
			{
				participantsList.add(new JsonPrimitive(participants[i]));
			}
			requestObj.add("participants", participantsList);
		}
		
		requestObj.addProperty("id", id);
		
		// Convert JSON object to String
		String requestJson = requestObj.toString();
		
		System.out.println("Query JSON: " + requestJson);
		return requestJson;
	}
	
	private static HttpURLConnection getChaincodeConnection(int requestLength) throws Exception
	{
		URL chaincode = null;
		if(TradeFinanceApplicationConstants.transactionManagerTestMode)
		{
			chaincode = new URL("https://0.0.0.0:9443/TradeFinanceApplicationServer/TestTransactionManager");
		}
		else
		{
			chaincode = new URL(TransactionManager.getHL_URL());
		}
		HttpURLConnection con = (HttpURLConnection)chaincode.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Content-Length", "" + requestLength);
		con.setUseCaches(false);
	    con.setDoInput(true);
	    con.setDoOutput(true);
	    con.setConnectTimeout(60000);
	    
		return con;
	}
}
