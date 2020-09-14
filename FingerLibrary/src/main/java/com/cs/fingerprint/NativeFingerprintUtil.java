package com.cs.fingerprint;

public class NativeFingerprintUtil {

    private static NativeFingerprintUtil m_Instance;

    private FingerprintCallback mCallback;

    public void registerCallback(FingerprintCallback callback) {
        mCallback = callback;
    }

    public void unRegisterCallback() {
        mCallback = null;
    }

    public static NativeFingerprintUtil getInstance() {
        synchronized (NativeFingerprintUtil.class) {
            if (m_Instance == null) {
                m_Instance = new NativeFingerprintUtil();
            }
        }
        return m_Instance;
    }

    private NativeFingerprintUtil() {
    }
    //2017/07/21
    //public int CsFingerprintManager_initSpi()
    //{
//		return initNative();
//	}

    public int CsFingerprintManager_initSpi(String path) {
        try {
            return initNative(path);
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }
    //2017/07/21

    public int CsFingerprintManager_closeSpi() {
        return closeSpiNative();
    }

    public int CsFingerprintManager_BeginEnroll() {
        return BeginEnrollNative();
    }

    public int CsFingerprintManager_EndEnroll() {
        return EndEnrollNative();
    }

    public int CsFingerprintManager_DeleteFingerprint(int fingerId) {
        return DeleteFingerprintNative(fingerId);
    }

    //2017/07/21
    //public int CsFingerprintManager_BeginAuth()
    //{
    //	return BeginAuthNative();
//	}
    public int CsFingerprintManager_BeginAuth(String path) {
        return BeginAuthNative(path);
    }

    //2017/07/21
    public int CsFingerprintManager_cancel() {
        return FingercancelNative();
    }

    private void onEnrollStateChanged(int fingerId, int progress) {
        if (mCallback != null) mCallback.onEnrollStateChanged(fingerId, progress);
    }

    private void onAuthStateChanged(int fingerId) {
        if (mCallback != null) mCallback.onAuthStateChanged(fingerId);
    }

    public interface FingerprintCallback {
        void onEnrollStateChanged(int fingerId, int progress);

        void onAuthStateChanged(int fingerId);

    }

    //2017/07/21
    //private native int initNative();
    private native int initNative(String path);

    //2017/07/21
    private native int closeSpiNative();

    private native int BeginEnrollNative();

    //2017/07/21
//	private native int BeginAuthNative();
    private native int BeginAuthNative(String path);
//2017/07/21

    private native int EndEnrollNative();

    private native int FingercancelNative();

    private native int DeleteFingerprintNative(int fingerId);

    static {
        System.loadLibrary("CSAlgDll");
        System.loadLibrary("csfingerprint_jni");
    }
}
