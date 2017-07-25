package wasdev.sample.servlet.session;

import java.io.File;
import java.io.FileWriter;

import wasdev.sample.servlet.TransactionManager;

public class Transaction {
	public static final String workflowManagementChaincode = "WORKFLOW_MANAGEMENT_CHAINCODE";
	public static final String pushNotificationsChaincode = "PUSH_NOTIFICATIONS_CHAINCODE";
	
	public static final int deployType = 1;
	public static final int invokeType = 2;
	
	private static final String duplicateDeploymentErrorSubstring = "deploy attempted but a chaincode with same name running";
	
	private String contractID;
	private int operationType;	// 1 = deploy, 2 = invoke (default)
	private String submitterID;
	private String submitterRole;
	private String txMethod;
	private String UUID;
	private boolean status;
	private String errorMessage;
	private String chaincodeName;
	
	public Transaction(String cid, String txm, String uuid, String user, String role, boolean st)
	{
		contractID = cid;
		operationType = invokeType;
		txMethod = txm;
		UUID = uuid;
		submitterID = user;
		submitterRole = role;
		status = st;
		errorMessage = "";
		chaincodeName = "";
	}
	
	public Transaction(String cid, int type, String txm, String uuid, boolean st, String cname)
	{
		contractID = cid;
		operationType = type;
		txMethod = txm;
		UUID = uuid;
		submitterID = null;
		submitterRole = null;
		status = st;
		errorMessage = "";
		chaincodeName = cname;
	}
	
	public Transaction(String cid, String txm, String uuid, boolean st, String errm)
	{
		contractID = cid;
		operationType = invokeType;
		txMethod = txm;
		UUID = uuid;
		submitterID = null;
		submitterRole = null;
		status = st;
		errorMessage = errm;
		chaincodeName = "";
	}
	
	public Transaction(String cid, int type, String txm, String uuid, boolean st, String errm, String cname)
	{
		contractID = cid;
		operationType = type;
		txMethod = txm;
		UUID = uuid;
		submitterID = null;
		submitterRole = null;
		status = st;
		errorMessage = errm;
		chaincodeName = cname;
	}
	
	public Transaction(String cid, int type, String txm, String uuid, String user, String role, boolean st, String errm, String cname)
	{
		contractID = cid;
		operationType = type;
		txMethod = txm;
		UUID = uuid;
		submitterID = user;
		submitterRole = role;
		status = st;
		errorMessage = errm;
		chaincodeName = cname;
	}
	
	public String getContractID() {
		return contractID;
	}
	
	public void setContractID(String contractID) {
		this.contractID = contractID;
	}
	
	public String getTxMethod() {
		return txMethod;
	}
	
	public String getUserID() {
		return submitterID;
	}
	
	public String getUserRole() {
		return submitterRole;
	}

	public void setTxMethod(String txMethod) {
		this.txMethod = txMethod;
	}
	
	public String getUUID() {
		return UUID;
	}
	
	public void setUUID(String uUID) {
		UUID = uUID;
	}
	
	public boolean getStatus() {
		return status;
	}
	
	public void setStatus(boolean st) {
		status = st;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errm) {
		this.errorMessage = errm;
	}
	
	public boolean isDeploy()
	{
		return operationType == deployType;
	}
	
	public boolean isInvoke()
	{
		return operationType == invokeType;
	}
	
	/*public void updateChaincodeStates(boolean st, String errm)
	{
		if(isInvoke())
		{
			status = st;
			errorMessage = errm;
			return;
		}
		if(isDeploy())
		{
			boolean duplicateDeploymentError = (errm.contains(duplicateDeploymentErrorSubstring));
			status = st || duplicateDeploymentError;
			errorMessage = errm;
			if(chaincodeName.equals(workflowManagementChaincode))
			{
				if(status)
				{
					TransactionManager.workflowManagementChaincodeID = UUID;
					System.out.println("Workflow Management Chaincode deployed");
				}
				else
				{
					System.out.println("Workflow Management deployment failed with error: " + errm);
				}
			}
			else if(chaincodeName.equals(pushNotificationsChaincode))
			{
				if(status)
				{
					TransactionManager.pushNotificationChaincodeID = UUID;
					System.out.println("Push Notification Chaincode deployed");
				}
				else
				{
					System.out.println("Push Notification Chaincode deployment failed with error: " + errm);
				}
			}
			TransactionManager.chaincodeDeploymentsRunning--;
			if(TransactionManager.chaincodeDeploymentsRunning == 0)
			{
				System.out.println("All chaincode deployments done");
				
				// Update chaincode handles
				try
				{
					System.out.println("Updating chaincode handles file...");
					File ccHandlesFile = new File(TransactionManager.getChaincodeIDLocalFile());
					FileWriter fw = new FileWriter(ccHandlesFile);
					fw.write("workflowManagementChaincodeID=" + TransactionManager.workflowManagementChaincodeID);
					fw.write("\r\n");
					fw.write("pushNotificationChaincodeID=" + TransactionManager.pushNotificationChaincodeID);
					fw.write("\r\n");
					
					fw.flush();
					fw.close();
					fw = null;
					
					System.out.println("Chaincode handles file updated.");
				}
				catch(Throwable error)
				{
					System.out.println("Error updating chaincode handles file: ");
					error.printStackTrace();
				}
			}
			else
			{
				System.out.println("" + TransactionManager.chaincodeDeploymentsRunning + " chaincode deployments pending");
			}
		}
	}*/
}
