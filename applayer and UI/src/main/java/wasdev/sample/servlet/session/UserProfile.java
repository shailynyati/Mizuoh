package wasdev.sample.servlet.session;

public class UserProfile
{
	private String id;
	private String role;
	
	public UserProfile(String uid, String urole)
	{
		id = uid;
		role = urole;
	}
	
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getRole()
	{
		return role;
	}

	public void setRole(String role)
	{
		this.role = role;
	}
	
	public int hashCode()
	{
		return (id + ":" + role).hashCode();
	}
	
	public boolean equals(Object oup)
	{
		UserProfile up = (UserProfile) oup;
		if(id == null || role == null || up.id == null || up.role == null)
		{
			return false;
		}
		return id.equals(up.id) && role.equals(up.role);
	}
}
