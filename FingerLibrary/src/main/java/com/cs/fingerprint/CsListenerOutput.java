package com.cs.fingerprint;

public class CsListenerOutput {
	//
	public static final int ENROLL_START = 0;
	public static final int ENROLL_OK = 1;
	public static final int ENROLL_ERROR = 2;
	public static final int ENROLL_FINISH = 3;
	public static final int ENROLL_EXIST = 17;
	public static final int MATCH_START = 4;
	public static final int MATCH_OK = 5;
	public static final int MATCH_ERROR = 6;
	public static final int MATCH_FINISH = 7;
	public static final int MATCH_END = 18;
	public static final int MATCH_NO_ENROLL_FINGER = 19;
	public static final int NAVIGATION_UP = 8;
	public static final int NAVIGATION_DOWN = 9;
	public static final int NAVIGATION_LEFT = 10;
	public static final int NAVIGATION_RIGHT = 11;
	public static final int NAVIGATION_LEFTUP = 12;
	public static final int NAVIGATION_LEFTDOWN = 13;
	public static final int NAVIGATION_RIGHTUP = 14;
	public static final int NAVIGATION_RIGHTDOWN = 15;
	public static final int DOUBLE_CLICKED = 16;
	
	
	//
	public static final int ERROR_TOO_MANY_FINGERS = 0;
	public static final int ERROR_TOO_LITTLE_FEATURES = 1;
	public static final int ERROR_UNDEFINE = 2;
	public static final int ERROR_CAN_NOT_MEAGE = 3;
	public static final int ERROR_NOT_AVAILABLE = 4;
	public static final int ERROR_FINGER_ENROLLED = 5;
	//
	private int m_enrollCount = 0;
	private int m_enrollFingerId = 0;
	private long m_lastFingerTouchTime = 0;
	private int m_errorCode = 0;
	
	
	
	public void setEnrollCount(int count){
		m_enrollCount = count; 
	}
	
	public int getEnrollCount(){
		return m_enrollCount; 
	}
	
	public void setCurEnrollFingerId(int id){
		m_enrollFingerId = id; 
	}
	
	public int getCurEnrollFingerId(){
		return m_enrollFingerId; 
	}	
	
	public void setLastFingerTouchTime(long time){
		m_lastFingerTouchTime = time; 
	}
	
	public long getLastFingerTouchTime(){
		return m_lastFingerTouchTime; 
	}	
	
	public void setErrorCode(int errorcode){
		m_errorCode = errorcode; 
	}
	
	public int getErrorCode(){
		return m_errorCode; 
	}		
}
