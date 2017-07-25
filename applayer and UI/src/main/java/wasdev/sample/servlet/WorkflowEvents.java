package wasdev.sample.servlet;

import java.util.HashMap;

import wasdev.sample.servlet.session.Transaction;
import wasdev.sample.servlet.util.TradeFinanceApplicationConstants;
import wasdev.sample.servlet.util.ChaincodeHandler;
import wasdev.sample.servlet.util.PushNotificationHandler;

public class WorkflowEvents {
	
	private static HashMap<String, String[]> txMethods_PushNotifications = new HashMap<String, String[]>();
	private static HashMap<String, String> txMethods_AlertMessages = new HashMap<String, String>();
	
	//initialize the HashMap with the list of LC Fulfillment Tx Methods and Push Notifications
	static
	{
		// L/C Issuance Events
		txMethods_PushNotifications.put("submitLC",  	// New L/C creation, validation, and submission by importer's bank
				new String[]{"NewLCCreated_I", "NewLCCreated_E", "NewLCCreated_IB", "ReviewNewLC_EB"});
		txMethods_PushNotifications.put("acceptLC", 	// L/C accepted by exporter's bank
				new String[]{"LCAccepted_I", "LCAccepted_E", "LCAccepted_IB", "LCAccepted_EB"});
		txMethods_PushNotifications.put("rejectLC", 	// L/C rejected by exporter's bank
				new String[]{"LCRejected_I", "LCRejected_E", "LCRejected_IB", "LCRejected_EB"});
		
		// L/C Issuance Alert Messages
		txMethods_AlertMessages.put("NewLCCreated_I", "L/C Created");
		txMethods_AlertMessages.put("NewLCCreated_E", "L/C Created");
		txMethods_AlertMessages.put("NewLCCreated_IB", "L/C Created");
		txMethods_AlertMessages.put("ReviewNewLC_EB", "Review L/C");
		txMethods_AlertMessages.put("LCAccepted_I", "L/C accepted by Exporter's Bank");
		txMethods_AlertMessages.put("LCAccepted_E", "L/C accepted by Exporter's Bank");
		txMethods_AlertMessages.put("LCAccepted_IB", "L/C accepted by Exporter's Bank");
		txMethods_AlertMessages.put("LCAccepted_EB", "L/C accepted by Exporter's Bank");
		txMethods_AlertMessages.put("LCRejected_I", "L/C rejected by Exporter's Bank");
		txMethods_AlertMessages.put("LCRejected_E", "L/C rejected by Exporter's Bank");
		txMethods_AlertMessages.put("LCRejected_IB", "L/C rejected by Exporter's Bank");
		txMethods_AlertMessages.put("LCRejected_EB", "L/C rejected by Exporter's Bank");

		// L/C Fulfillment Events
		txMethods_PushNotifications.put("submitED", 			// Export documents creation, validation, and submission by exporter's bank
				new String[]{"NewEDCreated_I", "NewEDCreated_E", "NewEDCreated_EB", "ReviewNewED_IB"});
		txMethods_PushNotifications.put("acceptED", 			// Documents accepted by importer's bank
				new String[]{"EDAccepted_I", "EDAccepted_E", "EDAccepted_IB", "EDAccepted_EB"});
		txMethods_PushNotifications.put("rejectED", 			// Documents rejected by importer's bank
				new String[]{"EDRejected_I", "EDRejected_E", "EDRejected_IB", "EDRejected_EB"});
		txMethods_PushNotifications.put("acceptToPay", 			// Importer's bank agreed to make payment
				new String[]{"AgreedToPay_I", "AgreedToPay_E", "AgreedToPay_IB", "AgreedToPay_EB"});
		txMethods_PushNotifications.put("paymentReceived", 		// Importer's bank made payment
				new String[]{"PaymentReceived_I", "PaymentReceived_E", "PaymentReceived_IB", "PaymentReceived_EB"});					
		txMethods_PushNotifications.put("defaultedOnPayment", 	// Importer's bank defaulted on payment
				new String[]{"PaymentDefaultDetected_I", "PaymentDefaultDetected_E", "PaymentDefaultDetected_IB", "PaymentDefaultDetected_EB"});

		// L/C Fulfillment Alert Messages
		txMethods_AlertMessages.put("NewEDCreated_I", "Export Documents Created");
		txMethods_AlertMessages.put("NewEDCreated_E", "Export Documents Created");
		txMethods_AlertMessages.put("NewEDCreated_EB", "Export Documents Created");
		txMethods_AlertMessages.put("ReviewNewED_IB", "Review Export Documents");
		txMethods_AlertMessages.put("EDAccepted_I", "Export Documents accepted by Importer's Bank");
		txMethods_AlertMessages.put("EDAccepted_E", "Export Documents accepted by Importer's Bank");
		txMethods_AlertMessages.put("EDAccepted_IB", "Export Documents accepted by Importer's Bank");
		txMethods_AlertMessages.put("EDAccepted_EB", "Export Documents accepted by Importer's Bank");
		txMethods_AlertMessages.put("EDRejected_I", "Export Documents rejected by Importer's Bank");
		txMethods_AlertMessages.put("EDRejected_E", "Export Documents rejected by Importer's Bank");
		txMethods_AlertMessages.put("EDRejected_IB", "Export Documents rejected by Importer's Bank");
		txMethods_AlertMessages.put("EDRejected_EB", "Export Documents rejected by Importer's Bank");
		txMethods_AlertMessages.put("AgreedToPay_I", "Importer's Bank agreed to make payment");
		txMethods_AlertMessages.put("AgreedToPay_E", "Importer's Bank agreed to make payment");
		txMethods_AlertMessages.put("AgreedToPay_IB", "Importer's Bank agreed to make payment");
		txMethods_AlertMessages.put("AgreedToPay_EB", "Importer's Bank agreed to make payment");
		txMethods_AlertMessages.put("PaymentReceived_I", "Payment received by Exporter's Bank");
		txMethods_AlertMessages.put("PaymentReceived_E", "Payment received by Exporter's Bank");
		txMethods_AlertMessages.put("PaymentReceived_IB", "Payment received by Exporter's Bank");
		txMethods_AlertMessages.put("PaymentReceived_EB", "Payment received by Exporter's Bank");
		txMethods_AlertMessages.put("PaymentDefaultDetected_I", "Exporter�s bank detected a default in payment");
		txMethods_AlertMessages.put("PaymentDefaultDetected_E", "Exporter�s bank detected a default in payment");
		txMethods_AlertMessages.put("PaymentDefaultDetected_IB", "Exporter�s bank detected a default in payment");
		txMethods_AlertMessages.put("PaymentDefaultDetected_EB", "Exporter�s bank detected a default in payment");
 	}

	public static void handleEvent(Transaction tx)
	{
		if(txMethods_PushNotifications.containsKey(tx.getTxMethod()))
		{
			System.out.println("Need to send push notifications for transaction " + tx.getTxMethod());
			String[] pushNotifications = txMethods_PushNotifications.get(tx.getTxMethod());
			if(pushNotifications.length > 0)
			{
				// Send Push Notifications in sequence of the array items
				for(int i = 0; i < pushNotifications.length; i++)
				{
					String pushNotification = pushNotifications[i];
					int separator = pushNotification.indexOf("_");
					if(separator >= 0)
					{
						String notificationType = pushNotification.substring(0, separator);		// Event type
						String recipient = pushNotification.substring(separator + 1);			// Recipient
						String alertMessage = "";
						String errorMessage = tx.getErrorMessage();
						if(tx.getStatus())
						{
							alertMessage = txMethods_AlertMessages.get(pushNotification);	// Alert Message for User
						}
						else
						{
							if(errorMessage.isEmpty())
							{
								errorMessage = "<unknown chaincode error>";
							}
							alertMessage = errorMessage;
							recipient = PushNotificationHandler.abbreviateRole(tx.getUserRole());		// Send failure notification to transaction submitter, not other party
						}
						System.out.println("Need to send push notification " + pushNotification + " with alert '" + alertMessage + "'");
						// Send push notification
						boolean pushSucceeded = PushNotificationHandler.pushNotification(
								notificationType, tx.getContractID(), tx.getUserID(), recipient, tx.getStatus(), alertMessage);
						if(pushSucceeded)
						{
							if(TradeFinanceApplicationConstants.eventManagerTestMode)
							{
								System.out.println("Not recording notifications on ledger as we are running in test mode");
							}
							else
							{
								// Record push notifications on the blockchain
								try
								{
									//ChaincodeHandler.recordPushNotificationSent(tx.getContractID(), pushNotification, tx.getUserID());
								}
								catch(Exception error)
								{
									error.printStackTrace();
									//TODO Notify Mobile App?
								}
							}
						}
						else
						{
							// TODO Crash and/or notify Mobile App?
						}
						
						// Update counters if we are running in test mode
						if(TradeFinanceApplicationConstants.eventManagerTestMode)
						{
							TradeFinanceApplicationConstants.testMethodNotificationCounter --;
							if(TradeFinanceApplicationConstants.testMethodNotificationCounter == 0)
							{
								TradeFinanceApplicationConstants.testIgnoreNextUUID = false;
								if(TradeFinanceApplicationConstants.testMethodCounter == TradeFinanceApplicationConstants.testTxMethod.length)
								{	// Reset test parameter values for the next test run
									TradeFinanceApplicationConstants.testMethodCounter = 0;
									TradeFinanceApplicationConstants.eventManagerTestStarted = false;
								}
							}
						}
					}
				}
			}
		}
	}
}
