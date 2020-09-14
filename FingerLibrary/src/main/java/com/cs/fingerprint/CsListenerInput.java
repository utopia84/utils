package com.cs.fingerprint;

public class CsListenerInput {
	public static final short LISTEN_MODE_ENROLL = 0;
	public static final short LISTEN_MODE_MATCH = 1;
	public static final short LISTEN_NAVIGATION = 2;
	
	private short m_listenMode = LISTEN_MODE_ENROLL; 
	
	public void setListenMode(short mode){
		m_listenMode = mode; 
	}
	
	public short getListenMode(){
		return m_listenMode; 
	}
}
