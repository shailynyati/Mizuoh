package wasdev.sample.servlet.session;

import java.util.Hashtable;

import wasdev.sample.servlet.util.TradeFinanceApplicationConstants;

public class SessionManager {
	private static final Hashtable<String, Transaction> sessionData = new Hashtable<String, Transaction>();
		
	public static void putSessionData(Transaction tx)
	{
		sessionData.put(tx.getUUID(), tx);
	}
	
	// For transaction lookup, set 'remove' to 'false'
	// For transaction lookup and extraction, set 'remove' to 'true'
	public static Transaction getSessionData(String uuid, boolean remove)
	{
		// Testing the following in the absence of a Transaction Manager to create a session record
		if(TradeFinanceApplicationConstants.eventManagerTestMode)
		{
			if(!TradeFinanceApplicationConstants.eventManagerTestStarted)
			{	// First transaction is a 'deploy'; drop it in test mode
				TradeFinanceApplicationConstants.eventManagerTestStarted = true;
				return null;
			}
			// If we already stored this UUID, ignore notifications received from trackers on other peers as duplicates
			synchronized(TradeFinanceApplicationConstants.testUuidRecords)
			{
				if(TradeFinanceApplicationConstants.testUuidRecords.get(uuid) != null)
				{
					return null;
				}
				TradeFinanceApplicationConstants.testUuidRecords.put(uuid, true);
			}
			// If the ignore UUID flag is set, reset it and return without updating transaction state
			if(TradeFinanceApplicationConstants.testIgnoreNextUUID)
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
				return null;
			}
			
			Transaction tx = new Transaction(TradeFinanceApplicationConstants.testContractID, 
						TradeFinanceApplicationConstants.testTxMethod[TradeFinanceApplicationConstants.testMethodCounter], 
						uuid, null, null, false);
			TradeFinanceApplicationConstants.testMethodNotificationCounter = TradeFinanceApplicationConstants.
										testTxMethodNotificationCount[TradeFinanceApplicationConstants.testMethodCounter];
			TradeFinanceApplicationConstants.testMethodCounter++;
			return tx;
		}
		
		if(sessionData.containsKey(uuid))
		{
			if(remove)
				return sessionData.remove(uuid);
			else
				return sessionData.get(uuid);
		}
		else
			return null;
	}
}
