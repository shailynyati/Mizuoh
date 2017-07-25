package wasdev.sample.servlet;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import wasdev.sample.servlet.session.UserProfile;
import wasdev.sample.servlet.util.CloudantDBClass;


public class Login {
	//private static final String hlURL = "http://192.168.42.53:5000/registrar/";
	private static final int sessionTimeoutInSec = 15*60;
	
	private static final boolean DEBUG = true;
	
	public static boolean login(HttpServletRequest request, HttpServletResponse response, String data) throws Exception 
	{
		
		System.out.println("Processing Login...");
		
		if(DEBUG)
			System.out.println("\t" + data);
		
		if(data == null)
			throw new AuthenticationException(AuthenticationException.INVALID_REQUEST);

		String userID = null;
		String passcode = null;
		String userRole = null;

		if(data.startsWith("["))
		{
			//format ["userID=ABC_COMPANY","passcode=RMYVxSZCk370","userRole=Importer"]
			data=data.trim();
			data = data.substring(1);
			data = data.substring(0, data.length()-1);
			System.out.println(data);
			StringTokenizer st = new StringTokenizer(data, ",");
			while(st.hasMoreTokens())
			{
				String token = st.nextToken();
				System.out.println(token);
				if(token.startsWith("\"userID"))
				{
					userID = token.substring("\"userID=".length(), token.length()-1);
					System.out.println("userID=" + userID);
				}
				else if(token.startsWith("\"passcode"))
				{
					passcode = token.substring("\"passcode=".length(), token.length()-1);
					System.out.println("passcode=" + passcode);
				}
				else if(token.startsWith("\"userRole"))
				{
					userRole = token.substring("\"userRole=".length(), token.length()-1);
					System.out.println("userRole=" + userRole);
				}
			}
		}
		else
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
			Properties props = new Properties();
			props.load(bais);
			
			userID = props.getProperty("userID");
			passcode = props.getProperty("passcode");
			userRole = props.getProperty("userRole");
		}
		
		//userID = data[0];
		//passcode = data[1];
		
		System.out.println("userID:"+userID);
		System.out.println("passcode:"+passcode);
		
		
		if(userID == null || userID.equals("") || passcode == null || passcode.equals(""))
			throw new AuthenticationException(AuthenticationException.INVALID_REQUEST);
		
		//check if the user is already logged in
		HttpSession session = request.getSession(false);
		if(session != null && session.getAttribute("userID") != null && session.getAttribute("userID").toString().equals(userID) && session.getAttribute("userRole").toString().equals(userRole))
		{
			response.getWriter().write("User " + userID + "/" + userRole + " already logged in");
			return true;
		}
		
		if(session != null)
			session.invalidate();
		
		if(loginCheck(userID,passcode)){
			System.out.println("in login check");
			CloudantDBClass cloudantDBClassObj = new CloudantDBClass();
			cloudantDBClassObj.tryConnection(userID,passcode);
			createSession(request, userID,cloudantDBClassObj.getUsers().getRole_Name(),cloudantDBClassObj.getUsers().getCompany_Name());
				HttpSession userSession = request.getSession();
				System.out.println("Value in session:"+userSession.getAttribute("userID"));
				System.out.println("In if statement");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write("{ \"Status\":\"OK\",\"UserID\":\""
					+cloudantDBClassObj.getUsers().getUser_Id()+"\","
					+ "\"CompanyName\":\""+cloudantDBClassObj.getUsers().getCompany_Name()+"\","
					+ "\"RoleName\":\""+cloudantDBClassObj.getUsers().getRole_Name()+"\","
					+"\"RoleID\":\""+cloudantDBClassObj.getUsers().getRole_ID()+"\"}");
			System.out.println("Got data from db");
			}else{
		response.getWriter().write("{ \"Status\":\"Failed\"}");
	}
		
		
		/*if(TransactionManager.isENABLE_HL_LOGIN())
		{
			String loginRequestJSON = createHLLoginRequest(userID, passcode);
			
			URL url = new URL(TransactionManager.getHL_LOGIN_URL());
			HttpURLConnection hlConnection = (HttpURLConnection) url.openConnection();
			hlConnection.setRequestMethod("POST");
			hlConnection.setRequestProperty("Content-Type", "application/json");
			hlConnection.setDoOutput(true);
			hlConnection.connect();
	        
			OutputStream os = new BufferedOutputStream(hlConnection.getOutputStream());
	        os.write(loginRequestJSON.getBytes());
	        os.write(("\r\n").getBytes());
	        os.flush();
			os.close();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(hlConnection.getInputStream()));
			String hlLoginResponse = "";
			while(true)
			{
				String line = br.readLine();
				if(line == null) break;
				if(!hlLoginResponse.isEmpty()) hlLoginResponse += "\n";
				hlLoginResponse += line;
			}
			br.close();
			hlConnection.disconnect();
	
			System.out.println("Login Response:\n\t" + hlLoginResponse);
			
			boolean loginSuccessful = hlLoginResponse.toLowerCase().indexOf("Login successful".toLowerCase()) > -1 || hlLoginResponse.toLowerCase().indexOf("already logged in".toLowerCase()) > -1?true:false;
			
			if(loginSuccessful)
			{
				createSession(request, userID, userRole);
				response.getWriter().write("{\"OK\": \"Login successful for user " + userID + ".\"}");
			}
			else
				throw new AuthenticationException(AuthenticationException.LOGIN_FAILED);
		}
		else
		{
			createSession(request, userID, userRole);
			response.getWriter().write("{\"OK\": \"Login successful for user " + userID + ".\"}");
		}*/
	return true;
	}
	
	private static boolean loginCheck(String userID, String passcode ) throws IOException{
		System.out.println("Coming here in login check");
		boolean isLogin = false;
		CloudantDBClass cloudantDBClassObj = new CloudantDBClass();
		if(cloudantDBClassObj.tryConnection(userID,passcode)){
			System.out.println("here");
			if(createLoginRegister(userID,passcode)){
				isLogin = true;
			}
			
		}
		return isLogin;
	}
	
	private static boolean createLoginRegister(String userID, String passcode) throws IOException{
		
		String loginRequestJSON = createHLLoginRequest(userID, passcode);
		URL url = new URL(TransactionManager.getHL_LOGIN_URL());
		HttpURLConnection hlConnection = (HttpURLConnection) url.openConnection();
		hlConnection.setRequestMethod("POST");
		hlConnection.setRequestProperty("Content-Type", "application/json");
		hlConnection.setDoOutput(true);
		hlConnection.connect();

		OutputStream os = new BufferedOutputStream(hlConnection.getOutputStream());
		os.write(loginRequestJSON.getBytes());
		os.write(("\r\n").getBytes());
		os.flush();
		os.close();

		BufferedReader br = new BufferedReader(new InputStreamReader(hlConnection.getInputStream()));
		String hlLoginResponse = "";
		while(true)
		{
			String line = br.readLine();
			if(line == null) break;
			if(!hlLoginResponse.isEmpty()) hlLoginResponse += "\n";
			hlLoginResponse += line;
		}
		br.close();
		hlConnection.disconnect();

		System.out.println("Login Response:\n\t" + hlLoginResponse);

		boolean loginSuccessful = hlLoginResponse.toLowerCase().indexOf("Login successful".toLowerCase()) > -1 || hlLoginResponse.toLowerCase().indexOf("already logged in".toLowerCase()) > -1?true:false;
		return loginSuccessful;
	}
	
	
	private static String createHLLoginRequest(String userID, String passcode)
	{
		String loginRequest = 
			"{" +
					"\"enrollId\": \"" + userID + "\","+
					"\"enrollSecret\": \"" + passcode + "\""+
			"}";
		
		System.out.println("Login req:"+loginRequest);
		return loginRequest;
	}
	
	private static void createSession(HttpServletRequest request, String userID, String roleName,String companyName)
	{
		
		System.out.println("In session");
		HttpSession userSession = request.getSession();
		userSession.setAttribute("userID", userID);
		userSession.setAttribute("userRole", roleName);
		
		userSession.setMaxInactiveInterval(sessionTimeoutInSec);
		// Map IP to role for notification session authentication
		TransactionManager.clientIPRoleMap.put(request.getRemoteAddr(), new UserProfile(userID, roleName));
		System.out.println("Added " + request.getRemoteAddr() + ", " + userID + ", " + roleName + " to the IP role map");
		
	}
}