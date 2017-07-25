package wasdev.sample.servlet.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import wasdev.sample.servlet.LCIssuanceWorkflowAPI;
import wasdev.sample.servlet.NotificationServer;
import wasdev.sample.servlet.TransactionManager;

public class PushNotificationHandler {
	
	// Generate push notification payload
	static String generatePushNotificationPayload(String notificationType, String contractID, String recipient, 
			boolean status, String message)
	{
		JsonObject payloadObj = new JsonObject();
		payloadObj.addProperty("type", notificationType + "_" + recipient);
		payloadObj.addProperty("contractID", contractID);
		payloadObj.addProperty("transactionStatus", status);
		payloadObj.addProperty("message", message);
		return payloadObj.toString();
	}
	
	public static String abbreviateRole(String role)
	{
		if(role.equals("Importer"))
		{
			return "I";
		}
		else if(role.equals("ImporterBank"))
		{
			return "IB";
		}
		else if(role.equals("ExporterBank"))
		{
			return "EB";
		}
		else if(role.equals("Exporter"))
		{
			return "E";
		}
		if(role.equals("I") || role.equals("E") || role.equals("IB") || role.equals("EB"))
		{
			return role;
		}
		return null;
	}
	
	public static String mapRecipientToRole(String recipient)
	{
		if(recipient.equals("I"))
		{
			return "Importer";
		}
		else if(recipient.equals("IB"))
		{
			return "ImporterBank";
		}
		else if(recipient.equals("EB"))
		{
			return "ExporterBank";
		}
		else if(recipient.equals("E"))
		{
			return "Exporter";
		}
		if(recipient.equals("Importer") || recipient.equals("Exporter") || recipient.equals("ImporterBank") || recipient.equals("ExporterBank"))
		{
			return recipient;
		}
		return null;
	}
	
	// Build notification payload as a JSON dictionary and publish it to the web applications
	public static boolean pushNotification(String notificationType, String contractID, String userID, String recipient, 
			boolean status, String message)
	{
		String role = TradeFinanceApplicationConstants.eventManagerTestMode ? "*" : mapRecipientToRole(recipient);
		if(role == null)
		{
			System.out.println("Could not map recipient '" + recipient + "' to a role");
			return false;
		}
		String payload = generatePushNotificationPayload(notificationType, contractID, recipient, status, message);
		System.out.println("Push notification payload: " + payload);
		// Get the contract participants by querying chaincode
		String contractParticipants = null;
		try
		{
			if(TransactionManager.isENABLE_HL_LOGIN())
			{
				contractParticipants = LCIssuanceWorkflowAPI.queryContractChaincode(TransactionManager.workflowManagementChaincodeID, 
						"getContractParticipants", userID, contractID, "Retrieving Contract Participants List");
			}
			else
			{
				contractParticipants = LCIssuanceWorkflowAPI.queryContractChaincode(TransactionManager.workflowManagementChaincodeID, 
						"getContractParticipants", null, contractID, "Retrieving Contract Participants List");
			}
		} catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		if(contractParticipants == null)
		{
			return false;
		}
		JsonElement cpElement = (new JsonParser()).parse(contractParticipants);
		if(!cpElement.isJsonArray())
		{
			System.out.println("Not an array: " + cpElement.toString());
			return false;
		}
		// Get the user ID corresponding to this role from the participant array returned by the chaincode
		String recipientUserID = null;
		JsonArray cpArr = cpElement.getAsJsonArray();
		for(int i = 0 ; i < cpArr.size() ; i++)
		{
			JsonElement cpElem = cpArr.get(i);
			if(!cpElem.isJsonObject())
			{
				continue;
			}
			JsonObject cpElemObj = cpElem.getAsJsonObject();
			if(cpElemObj.has("id") && cpElemObj.has("role"))
			{
				JsonElement idElem = cpElemObj.get("id");
				if(!idElem.isJsonPrimitive())
				{
					continue;
				}
				JsonElement roleElem = cpElemObj.get("role");
				if(!roleElem.isJsonPrimitive())
				{
					continue;
				}
				String pid = idElem.getAsString();
				String prole = roleElem.getAsString();
				if(role.equals(prole))
				{
					recipientUserID = pid;
					break;
				}
			}
		}
		if(recipientUserID == null)
		{
			System.out.println("No participant in role '" + role + "' found for contract '" + contractID + "'");
			return false;
		}
		NotificationServer.sendMessage(payload, recipientUserID, role);
		System.out.println("Emitted event notification " + notificationType + " to " + role);
		return true;
	}
}
