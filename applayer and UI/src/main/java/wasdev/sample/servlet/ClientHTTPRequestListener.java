package wasdev.sample.servlet;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ClientHTTPRequestListener implements ServletRequestListener
{

	@Override
	public void requestDestroyed(ServletRequestEvent reqEvent)
	{
		System.out.println("Request destroyed: " + reqEvent.getServletRequest().getRemoteAddr());
	}

	@Override
	public void requestInitialized(ServletRequestEvent reqEvent)
	{
		String remoteIP = reqEvent.getServletRequest().getRemoteAddr();
		System.out.println("Request initialized: " + remoteIP);
		HttpServletRequest request = (HttpServletRequest) reqEvent.getServletRequest();
		HttpSession session = request.getSession(false);
		if(session != null)
		{
			session.setAttribute("remoteIP", remoteIP);
		}
	}

}
