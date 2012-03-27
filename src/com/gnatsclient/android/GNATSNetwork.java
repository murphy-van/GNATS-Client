package com.gnatsclient.android;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class GNATSNetwork {
	/* Internal */
	private GNATSUtility u;
	private GNATSConfig config;
	
	GNATSNetwork(GNATSConfig c) {
		u = new GNATSUtility();
		config = c;
	}
	
	public void clearRef() {
		u = null;
		config = null;
	}
	
	/* GNATS Server Connection Utilities */
	private boolean bConnected = false;
	private Socket s;
	private BufferedWriter bwOutput;
	private BufferedReader brInput;
	
	/* Socket Utilities */
	public boolean isConnected() {
		return bConnected;
	}
	protected int socketConnect() {
		InetAddress iaServer;
	
		u.d(u.DEBUG_NETWORK, "Socket Connecting...");
		if (bConnected == true) {
			u.d(u.DEBUG_NETWORK, "Connected Already.");
			return u.GNATS_OK;
		}
		
		try {
			iaServer = InetAddress.getByName(config.sServer);
			u.d(u.DEBUG_NETWORK, "Server " + config.sServer + " is resolved to IP: " + 
					iaServer.getHostAddress());
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			u.setErr(u.ERROR, 1001, e1.getMessage() + "\r\nServer " + config.sServer + " cannot be resolved");
			return u.GNATS_ERROR;
		}
		
		try {
			s = new Socket();
			SocketAddress sa = new InetSocketAddress(iaServer.getHostAddress(), config.iPort);
			s.connect(sa, u.DEFAULT_WAIT_TIME*1000);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			u.setErr(u.ERROR, 1002, e.getMessage() + "\r\nHost:" + config.sServer + "(" + 
					iaServer.getHostAddress() + "):" + config.sPort);
			return u.GNATS_ERROR;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			u.setErr(u.ERROR, 1003, e.getMessage() + "\r\nHost:" + config.sServer + "(" + 
					iaServer.getHostAddress() + "):" + config.sPort);
			return u.GNATS_ERROR;
		}
		
		bConnected = true;
		
		try {
			bwOutput = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			brInput = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			u.setErr(u.ERROR, 1004, e.getMessage());
			return socketDisconnect();
		}
		
		u.d(u.DEBUG_NETWORK, "Socket Connected!");
		
		return u.GNATS_OK;
	}
	protected int socketWrite(String o) {
		if (bConnected == false) {
			u.d(u.DEBUG_NETWORK, "Not Connected Yet.");
			return u.GNATS_ERROR;
		}
		try {
			bwOutput.write(o);
			bwOutput.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			u.setErr(u.ERROR, 1005, e.getMessage() + "\r\nOutput: " + o);
			return u.GNATS_ERROR;
		}
		
		u.d(u.DEBUG_NETWORK, "Socket Write (" + o.length() + " Bytes): " + o);
		
		return u.GNATS_OK;
	}
	protected String socketRead(char[] buf, int iSize, int wait_seconds) {
		int iRead = 0;
		int iOffset = 0;
		boolean bMore = false;
		String sRead;
		
		u.d(u.DEBUG_NETWORK, "Socket Read.");
		if (bConnected == false) {
			u.d(u.DEBUG_NETWORK, "Not Connected Yet.");
			return null;
		}
		
		u.startTimer(wait_seconds);
		do {
			try {
				if (brInput.ready()) {
					iRead = brInput.read(buf, iOffset, iSize);
					iOffset += iRead;
					if (iOffset >= iSize) {
						u.d(u.DEBUG_NETWORK, "Buffer Fullfilled!");
						break;
					}
					if (brInput.ready()) {
						bMore = true;
					} else {
						bMore = false;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				u.setErr(u.ERROR, 1006, e.getMessage() + "\r\nInput: " + iRead);
				u.clearTimer();
				return null;
			} catch (IndexOutOfBoundsException e2) {
				e2.printStackTrace();
				u.d(u.DEBUG_NETWORK, "Buffer Fullfilled!");
				iOffset = iSize;
				break;
			}
			
			if (u.isProgressCancelled()) {
				u.d(u.DEBUG_NETWORK, "Socket Read Cancelled.");
				break;
			}
			/* if there are more to read, loop back */
			/* or if nothing read and timer is not out yet, loop back */
		} while ((bMore == true) || ((!u.isTimeout()) && (iRead == 0)));
		
		u.clearTimer();
		
		u.d(u.DEBUG_NETWORK, "Socket Read Done (" + iOffset + " Bytes).");
		if (iOffset == 0) {
			sRead = "";
		} else {
			sRead = String.copyValueOf(buf, 0, iOffset);
		}
		
		return sRead;
	}
	protected int socketDisconnect() {
		u.d(u.DEBUG_NETWORK, "Socket Disconnecting...");
		if (bConnected == false) {
			u.d(u.DEBUG_NETWORK, "Not Connected Yet.");
			return u.GNATS_ERROR;
		}
		bConnected = false;
		try {
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			u.setErr(u.ERROR, 1008, e.getMessage());
			return u.GNATS_ERROR;
		}
		u.d(u.DEBUG_NETWORK, "Disconnected!");
		return u.GNATS_OK;
	}
}
