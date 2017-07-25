package wasdev.sample.servlet.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.List;

import org.omg.CORBA.portable.ApplicationException;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonParser;
/*import com.ibm.gsk.ikeyman.command.Constants;
import com.ibm.java.diagnostics.collector.Util;*/

public class CloudantDBClass {
	private CloudantClient client;
	private Users users;

	public Users getUsers() {
		return users;
	}
	public void setUsers(Users users) {
		this.users = users;
	}
	public boolean tryConnection(String UID, String PWD){
		System.out.println("try connection");
		boolean isAuthenticate = false;
		try {
			
			client = ClientBuilder.url(new URL("https://c573f2c7-266e-4208-aeb7-044bb59eb15b-bluemix:e37ee3d9ac761fab448a71bd69d5b1e2750b38a7c773697a25551a2f29ed94ad@c573f2c7-266e-4208-aeb7-044bb59eb15b-bluemix.cloudant.com"))
					.username("c573f2c7-266e-4208-aeb7-044bb59eb15b-bluemix")
					.password("e37ee3d9ac761fab448a71bd69d5b1e2750b38a7c773697a25551a2f29ed94ad")
					.build();
			List testList = client.getAllDbs();
			for (Iterator iterator = testList.iterator(); iterator.hasNext();) {
				Object object = (Object) iterator.next();
				System.out.println(object.toString());
				Database testDB = database(client);
				String selectorJson = "{\"selector\":{\"User_Id\": \""+UID+"\",\"Password\":\""+PWD+"\"}}";

				List<Users> classOfT = null ;
				classOfT = testDB.findByIndex(selectorJson, Users.class);

				if(classOfT.size()==0){
					isAuthenticate = false;
				}else{
					isAuthenticate = true;
					for (Iterator iterator2 = classOfT.iterator(); iterator2.hasNext();) {
						users = (Users) iterator2.next();
						setUsers(users);

						break;
					}
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isAuthenticate;
	}
	public Database  database(CloudantClient client) {

		return (client.database("mizuho_user_management", false));

	}

}
