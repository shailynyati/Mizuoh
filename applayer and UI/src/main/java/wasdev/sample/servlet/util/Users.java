package wasdev.sample.servlet.util;

public class Users {
 private String User_Id;  //these need to be the same in cloudant key
 private String Company_Name;
 private String Role_ID;
 private String Role_Name;
public String getUser_Id() {
	return User_Id;
}
public void setUser_Id(String user_Id) {
	User_Id = user_Id;
}
public String getCompany_Name() {
	return Company_Name;
}
public void setCompany_Name(String company_Name) {
	Company_Name = company_Name;
}
public String getRole_ID() {
	return Role_ID;
}
public void setRole_ID(String role_ID) {
	Role_ID = role_ID;
}
public String getRole_Name() {
	return Role_Name;
}
public void setRole_Name(String role_Name) {
	Role_Name = role_Name;
}

 
}
