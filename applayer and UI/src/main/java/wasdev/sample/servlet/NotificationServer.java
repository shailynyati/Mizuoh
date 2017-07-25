package wasdev.sample.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import wasdev.sample.servlet.session.UserProfile;

@ServerEndpoint(value = "/Notifications", configurator = wasdev.sample.servlet.NotificationConfigurator.class)
public class NotificationServer
//public class NotificationServer implements Runnable
{
	private static Vector<Session> currentSessions = new Vector<Session>(0, 1);
	private final String roleStr = "/Role";
	private final String roleRespStr = "Role:";
	//private static Thread heartbeatLooper;
	private static Hashtable<UserProfile, ArrayList<String>> undeliveredEvents = new Hashtable<UserProfile, ArrayList<String>>();
	
	public NotificationServer()
	{
		/*if(heartbeatLooper != null && heartbeatLooper.isAlive())
		{
			System.out.println("Heartbeat thread already running");
		}
		else
		{
			heartbeatLooper = new Thread(this);
			heartbeatLooper.start();
			System.out.println("Heartbeat thread started");
		}*/
	}
	
	@OnOpen
	public void onOpen(Session session, EndpointConfig ec)
	{
		// Store the WebSocket session for later use.
		System.out.println("Establishing session: " + session.getId());
		currentSessions.add(session);
		String remoteIP = ec.getUserProperties().get("remoteIP").toString();
		System.out.println("Remote Client IP Address: " + remoteIP);
		UserProfile profile = TransactionManager.clientIPRoleMap.get(remoteIP);
		if(profile == null)
		{
			System.out.println("Caller doesn't seem to be logged into the App Server; no IP-Role mapping found");
		}
		else
		{
			System.out.println("User '" + profile.getId() + "' (" + profile.getRole() + ") logged in from " + remoteIP);
			session.getUserProperties().put("userProfile", profile);
			// Clear backlog of events
			sendMessage(null, profile.getId(), profile.getRole());
		}
	}

	@OnMessage
	public void receiveMessage(Session session, String message)
	{
		// Do nothing. Silently drop the message, because we are simply acting as a message emitter, not a consumer
		if(message.equals(roleStr))
		{
			Object oprof = session.getUserProperties().get("userProfile");
			try
			{
				if(oprof == null)
				{
					session.getBasicRemote().sendText(roleRespStr + "UNKNOWN");
				}
				else
				{
					session.getBasicRemote().sendText(roleRespStr + ((UserProfile) oprof).getRole().toString());
				}
			} catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
	}

	/**
	 * Send a message to all clients in the same room as this client.
	 * @param message
	 */
	public static void sendMessage(String message, String userID, String role)
	{
		System.out.println("Sending message " + message + " to " + userID + " (" + role + ")");
		synchronized(currentSessions)
		{
			boolean allIDs = userID.equals("*");
			boolean allRoles = role.equals("*");
			UserProfile profile = new UserProfile(userID, role);
			ArrayList<String> pendingEvents = undeliveredEvents.get(profile);
			if(!allIDs && !allRoles)
			{
				if(pendingEvents == null)
				{
					pendingEvents = new ArrayList<String>(20);		// We don't have more than 20 events per workflow right now
				}
			}
			boolean emitted = false;
			for (Session session: currentSessions)
			{
				try {
					if(allIDs || allRoles)		// Send the event regardless
					{
						if(session.isOpen())
						{
							if(message != null)
							{
								session.getBasicRemote().sendText(message);
							}
						}
						continue;
					}
					Object oprof = session.getUserProperties().get("userProfile");
					if(oprof == null)
					{
						continue;
					}
					UserProfile uprofile = (UserProfile) oprof;
					if(session.isOpen() && uprofile.getId().equals(userID) && uprofile.getRole().equals(role))
					{
						// Clear pending events
						for(int i = 0 ; i < pendingEvents.size() ; i++)
						{
							session.getBasicRemote().sendText(pendingEvents.get(i));
						}
						// Send current event
						if(message != null)
						{
							session.getBasicRemote().sendText(message);
						}
						undeliveredEvents.remove(profile);
						emitted = true;
						break;
					}
				} catch (IOException ioe)
				{
					ioe.printStackTrace();
				}
			}
			// Store events for later attempts if this attempt failed
			if(!allIDs && !allRoles && !emitted && message != null)
			{
				pendingEvents.add(message);
				undeliveredEvents.put(profile, pendingEvents);
			}
		}
	}

	@OnClose
	public void onClose(Session session, CloseReason reason)
	{
		try {
			session.close();
			System.out.println("Session " + session.getId() + "' closed");
			currentSessions.remove(session);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@OnError
	public void onError(Throwable t)
	{
		t.printStackTrace();
	}

	/*@Override
	public void run()
	{
		int time = 0;
		while(true)
		{
			NotificationServer.sendMessage("Heartbeat at " + time + " seconds", (time % 2 == 0 ? "IB" : "EB"));
			time++;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				sendMessage("Heartbeat thread interrupted", "*");
				break;
			}
		}
	}*/
}
