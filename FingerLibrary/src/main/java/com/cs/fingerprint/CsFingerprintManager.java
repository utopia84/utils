package com.cs.fingerprint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;


public class CsFingerprintManager implements NativeFingerprintUtil.FingerprintCallback {
    private final String TAG = CsFingerprintManager.class.getSimpleName();
    private volatile static CsFingerprintManager m_Instance;
    private static final Object m_InstanceSync = new Object();
    private final String ENROLLPATH = FingerUtils.FINGER_PATH + "/fingerprint.bin";  //2017/07/21
    private final int FEATURE_SIZE = 28000;
    private final int ENROLL_CNT = 8;//14 ;//10;
    private final int MAX_FINGER_NUM = 5;

    private short IMAGE_W = 112;
    private short IMAGE_H = 88;


    private static final int MODE_IDLE = 1;
    private static final int MODE_SLEEP = 2;
    private static final int MODE_NORMAL = 3;
    private static final int MODE_DEEPSLEEP = 4;
    private static final int FINGERPRINT_TEMPLATE_ENROLLING = 1;
    private static final int FINGERPRINT_AUTHENTICATED = 2;
    private CsListenerInput m_listenerInput;
    private CsListenerOutput m_listenerOutput;
    private int m_currentFingerNum = 0;
    private boolean m_bListenerDone = true;
    private boolean m_bStopListen = false;

    private Context mContext;
    private FingerprintListener m_Listenr;
    private Handler mHandler;

    private static FingerprintListener m_ServiceListener;
    private boolean bIsNavRegister = false;
    private int screen_statu = 0;

    private NativeFingerprintUtil m_NativeInstance;

    private int mtype = -1;
    public int mresult = -100;
    public int mfingerid = 0;
    public static int enrollcount = 0;
    private int minit = 0;

    private WakeLock wakeLock;

    public void setScreenStatu(int statu) {
        screen_statu = statu;
    }

    public int getScreenStatu() {
        return screen_statu;
    }

    public void setServiceListener(FingerprintListener listener) {
        m_ServiceListener = listener;
    }

    public static CsFingerprintManager getInstance(Context context,String mid) {
        if (m_Instance == null) {
            synchronized (m_InstanceSync) {
                if (m_Instance == null) {
                    m_Instance = new CsFingerprintManager(context.getApplicationContext(),mid);
                }
            }
        }
        return m_Instance;
    }

    public void releaseInstance() {
        synchronized (m_InstanceSync) {
            if (m_Instance != null) {
                m_Instance = null;
            }
            if (m_NativeInstance != null) {
                try {
                    m_NativeInstance.CsFingerprintManager_closeSpi();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                m_NativeInstance = null;
            }
        }
    }

    private boolean isSupportFinger(String mid){
        try {
            File file = new File("/private/SN.txt");
            if (file.exists() && !TextUtils.isEmpty(mid)) {
                return Pattern.matches("m1701p?[0-9]+[a-z]*", mid.toLowerCase());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @SuppressLint("HandlerLeak")
    private CsFingerprintManager(Context context,String mid) {
        if (!isSupportFinger(mid)){//不支持指纹
            Log.e("test","不支持指纹");
            m_NativeInstance = null;
            return;
        }
        Log.e("test","支持指纹");
        mContext = context;
        try {
            m_NativeInstance = NativeFingerprintUtil.getInstance();
            //	minit=m_NativeInstance.CsFingerprintManager_initSpi();
            minit = m_NativeInstance.CsFingerprintManager_initSpi(ENROLLPATH);//2017/07/21
        } catch (Exception e) {
            m_NativeInstance = null;
            e.printStackTrace();
        }

        Log.e("test","minit-"+minit);
        if (minit == -1) {
            m_NativeInstance = null;
        }

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CsListenerOutput.ENROLL_START:
                    case CsListenerOutput.MATCH_START:
                        if (m_Listenr != null) {
                            m_Listenr.onFingerprintEnroll(msg.what,
                                    m_listenerOutput);
                        }
                        break;
                    case CsListenerOutput.ENROLL_OK:
                    case CsListenerOutput.ENROLL_ERROR:
                    case CsListenerOutput.ENROLL_FINISH:
                    case CsListenerOutput.MATCH_OK:
                    case CsListenerOutput.MATCH_ERROR:
                    case CsListenerOutput.MATCH_NO_ENROLL_FINGER:
                    case CsListenerOutput.MATCH_FINISH:
                    case CsListenerOutput.NAVIGATION_DOWN:
                    case CsListenerOutput.NAVIGATION_UP:
                        Log.d("csjava", "m_Listenr=" + m_Listenr);
                        if (m_Listenr != null) {
                            m_Listenr.onFingerprintEnroll(msg.what,
                                    m_listenerOutput);
                        }
                        m_bListenerDone = true;
                        break;
                }
            }
        };
    }

    public interface FingerprintListener {
        boolean onFingerprintEnroll(int result, CsListenerOutput listenerOuput);
    }

    public boolean canUseFinger() {
        return m_NativeInstance != null;
    }

    public void registerListener(CsListenerInput param,
                                 FingerprintListener listener) {
        if (param == null || listener == null) {
            return;
        }
        synchronized (m_InstanceSync) {
            if (m_NativeInstance != null) {
                m_NativeInstance.registerCallback(this);
            }
            m_listenerInput = param;
            m_Listenr = listener;
            m_listenerOutput = new CsListenerOutput();
            m_bStopListen = false;
        }
    }

    public void unregisterListener(FingerprintListener listener) {
        synchronized (m_InstanceSync) {
            if (listener == m_Listenr) {
                m_bStopListen = true;
                m_Listenr = null;
                if (m_NativeInstance != null) {
                    m_NativeInstance.unRegisterCallback();
                }
            }
        }
    }


    public void BegainAtun(String path) {
        if (m_NativeInstance == null) {
            return;
        }
        try {
            m_NativeInstance.CsFingerprintManager_BeginAuth(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //2017/07/21
    public void Fingercancel() {
        if (m_NativeInstance == null) {
            return;
        }
        try {
            m_NativeInstance.CsFingerprintManager_cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int deleteFinger(int fingerid) {
        // Log.d("csjava ","deleteFinger="+fingerid);
        if (fingerid <= 0)
            return -1;
        int ret = -1;
        if (m_NativeInstance == null) {
            return -1;
        }
        try {
            ret = m_NativeInstance.CsFingerprintManager_DeleteFingerprint(fingerid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;

    }

    public void CopyFile(String src, String dest) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {

            File destFile = new File(dest);// Source file or directory
            File srcFile = new File(src);// Compressed file path

            //Log.d("csjava","CopyFile destFile="+destFile);
            //Log.d("csjava", "CopyFile srcFile="+srcFile);
            //Log.d("csjava", "CopyFile srcFile.isFile()="+srcFile.isFile());
            if (srcFile.isFile()) {
                fis = new FileInputStream(srcFile);
                fos = new FileOutputStream(destFile);

                int read;
                int readnum = 0;
                byte b[] = new byte[FEATURE_SIZE];
                long a = srcFile.length();
                read = fis.read(b);
                //Log.d("csjava","CopyFile a="+a);
                while (read != -1) {
                    readnum += read;
                    //Log.d("csjava","CopyFile readnum="+readnum);
                    fos.write(b, 0, read);
                    read = fis.read(b);
                    //read=fis.read();
                }
                fis.close();
                fos.close();

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void DeleteFile(String str) {
        File f = new File(str);  //
        if (f.exists())
            f.delete();
    }

    public void startEnroll() {
        if (m_NativeInstance == null) {
            return;
        }
        try {
            m_NativeInstance.CsFingerprintManager_BeginEnroll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sleepTime(int time) {

        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onEnrollStateChanged(int fingerId, int progress) {
        //Log.i(TAG, " -----------  onEnrollStateChanged fingerId :  "
        //			+ fingerId + " , progress : "+progress);
        //Log.d("csjava","onEnrollStateChanged fingerId="+fingerId+ " , progress : "+progress);
        //GLQ 2016/04/10
        PowerManager mPM = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        wakeLock = mPM.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "csfingeprint_wakelock");
        wakeLock.acquire();
        wakeLock.release();
        //GLQ 2016/04/10


        if (progress > 0) {
            enrollcount++;
            m_listenerOutput.setEnrollCount(progress);
            //Log.d("csjava","enrollcount="+enrollcount);
            //Log.d("csjava","progress="+progress);
            mHandler.sendEmptyMessage(CsListenerOutput.ENROLL_OK);
            if (progress == ENROLL_CNT) {
                //Log.d("csjava","fingerId="+fingerId);
                mfingerid = fingerId;
                endEnroll();
                enrollcount = 0;
                mHandler.sendEmptyMessage(CsListenerOutput.ENROLL_FINISH);

            }
        } else if (progress == -5) {
            m_listenerOutput
                    .setErrorCode(CsListenerOutput.ERROR_CAN_NOT_MEAGE);
            mHandler.sendEmptyMessage(CsListenerOutput.ENROLL_ERROR);
        } else if (progress == -3) {
            m_listenerOutput
                    .setErrorCode(CsListenerOutput.ERROR_NOT_AVAILABLE);
            mHandler.sendEmptyMessage(CsListenerOutput.ENROLL_ERROR);
        } else if (progress == -4) {
            m_listenerOutput
                    .setErrorCode(CsListenerOutput.ERROR_FINGER_ENROLLED);
            mHandler.sendEmptyMessage(CsListenerOutput.ENROLL_ERROR);
        } else {
            Log.e("test", "progress-"+progress);
            m_listenerOutput
                    .setErrorCode(CsListenerOutput.ERROR_TOO_LITTLE_FEATURES);
            mHandler.sendEmptyMessage(CsListenerOutput.ENROLL_ERROR);
        }
    }

    @Override
    public void onAuthStateChanged(int fingerId) {
        //Log.d("csjava","onAuthStateChanged fingerId="+fingerId);
        m_bListenerDone = false;
        //GLQ 2016/04/10
        PowerManager mPM = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        wakeLock = mPM.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "csfingeprint_wakelock");
        wakeLock.acquire();
        wakeLock.release();
        //GLQ 2016/04/10
        if (fingerId == -1) {
            //Log.d("csjava","onAuthStateChanged MATCH_ERROR");
            mHandler.sendEmptyMessage(CsListenerOutput.MATCH_ERROR);
        } else if (fingerId == -2) {
            mHandler.sendEmptyMessage(CsListenerOutput.MATCH_NO_ENROLL_FINGER);

        } else {
            mHandler.sendEmptyMessage(CsListenerOutput.MATCH_OK);
            mHandler.sendEmptyMessage(CsListenerOutput.MATCH_FINISH);
        }
    }


    private int endEnroll() {
        if (m_NativeInstance == null) {
            return -1;
        }
        int ret = -1;
        try {
            ret = m_NativeInstance.CsFingerprintManager_EndEnroll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}


