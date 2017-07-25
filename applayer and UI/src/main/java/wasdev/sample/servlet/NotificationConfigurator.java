package wasdev.sample.servlet;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Configurator;

import javax.servlet.http.HttpSession;

public class NotificationConfigurator extends Configurator
{
	@Override
	public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response)
	{
		HttpSession session = (HttpSession) request.getHttpSession();
		Object rip = session.getAttribute("remoteIP");
		if(rip != null)
		{
			sec.getUserProperties().put("remoteIP", rip);
		}
	}
}
