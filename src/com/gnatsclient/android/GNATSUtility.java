package com.gnatsclient.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class GNATSUtility {
	/************** Common Const ******************/
	public final int GNATS_OK =			0;
	public final int GNATS_ERROR =		-1;
	public final int GNATS_TIMEOUT =	-2;
	public final int GNATS_OVERFLOW =	-3;

	/************** PR Fields Name*****************/
	public final String FIELD_NUMBER =		"Number:";
	public final String FIELD_ORIGINATOR =	"Originator:";
	public final String FIELD_SYNOPSIS =	"Synopsis:";
	
	public final int FIELD_MASK1_STANDARD =						0x0003FFFF;
	public final int FIELD_MASK2_STANDARD =						0x00000000;
	public final int FIELD_MASK3_STANDARD =						0x00000000;
	public final int FIELD_MASK4_STANDARD =						0x00000000;

	public final int FIELD_MASK1_LIST =							0x00000007;
	public final int FIELD_MASK2_LIST =							0x00000000;
	public final int FIELD_MASK3_LIST =							0x00000000;
	public final int FIELD_MASK4_LIST =							0x00000000;
	
	public final int FIELD_MASK1_DETAIL =						0xFFFFFFFF;
	public final int FIELD_MASK2_DETAIL =						0xFFFFFFFF;
	public final int FIELD_MASK3_DETAIL =						0xFFFFFFFF;
	public final int FIELD_MASK4_DETAIL =						0x00003EFF;
	
	public final int FIELD_MASK1_NUMBER =						0x00000001;
	public final int FIELD_MASK1_SYNOPSIS =						0x00000002;
	public final int FIELD_MASK1_ORIGINATOR =					0x00000004;
	public final int FIELD_MASK1_ARRIVAL_DATE =					0x00000008;
	public final int FIELD_MASK1_CATEGORY =						0x00000010;
	public final int FIELD_MASK1_CLASS =						0x00000020;
	public final int FIELD_MASK1_CONFIDENTIAL =					0x00000040;
	public final int FIELD_MASK1_CUSTOMER_RISK =				0x00000080;
	public final int FIELD_MASK1_FUNCTIONAL_AREA =				0x00000100;
	public final int FIELD_MASK1_KEYWORDS =						0x00000200;
	public final int FIELD_MASK1_LAST_MODIFIED =				0x00000400;
	public final int FIELD_MASK1_PLANNED_RELEASE =				0x00000800;
	public final int FIELD_MASK1_PROBLEM_LEVEL =				0x00001000;
	public final int FIELD_MASK1_REPORTED_IN =					0x00002000;
	public final int FIELD_MASK1_RESPONSIBLE =					0x00004000;
	public final int FIELD_MASK1_DEV_OWNER =					0x00008000;
	public final int FIELD_MASK1_STATE =						0x00010000;
	public final int FIELD_MASK1_SUBMITTER_ID =					0x00020000;
	
	public final int FIELD_MASK1_ATTRIBUTE = 					0x00040000;
	public final int FIELD_MASK1_AUTHOR =						0x00080000;
	public final int FIELD_MASK1_BETA_CUSTOMERS =				0x00100000;
	public final int FIELD_MASK1_BETA_PRIORITY =				0x00200000;
	public final int FIELD_MASK1_BETA_PROGRAMS =				0x00400000;
	public final int FIELD_MASK1_BLOCKER =						0x00800000;
	public final int FIELD_MASK1_BLOCKING_HISTORY =				0x01000000;
	public final int FIELD_MASK1_BLOCKING_RELEASE =				0x02000000;
	public final int FIELD_MASK1_BRANCH =						0x04000000;
	public final int FIELD_MASK1_CLIENT_BROWSER =				0x08000000;
	public final int FIELD_MASK1_CLIENT_OS =					0x10000000;
	public final int FIELD_MASK1_CLOSED_DATE =					0x20000000;
	public final int FIELD_MASK1_COMMIT_HISTORY =				0x40000000;
	public final int FIELD_MASK1_COMMITTED_IN =					0x80000000;
	
	public final int FIELD_MASK2_COMMITTED_RELEASE =			0x00000001;
	public final int FIELD_MASK2_CONF_COMMITTED_IN =			0x00000002;
	public final int FIELD_MASK2_CONF_COMMITTED_RELEASE =		0x00000004;
	public final int FIELD_MASK2_CONFIGURATION =				0x00000008;
	public final int FIELD_MASK2_CREATED =						0x00000010;
	public final int FIELD_MASK2_CUST_VISIBLE_BEHAVIOR_CHANGED =0x00000020;
	public final int FIELD_MASK2_CUSTOMER =						0x00000040;
	public final int FIELD_MASK2_CUSTOMER_ESCALATION =			0x00000080;
	public final int FIELD_MASK2_CUSTOMER_ESCALATION_OWNER =	0x00000100;
	public final int FIELD_MASK2_CUSTOMER_SUPPORT_LEVEL =		0x00000200;
	public final int FIELD_MASK2_CVBC_BACKWARD_COMPATIBLE =		0x00000400;
	public final int FIELD_MASK2_CVBC_CUSTODIAN =				0x00000800;
	public final int FIELD_MASK2_CVBC_DOC_IMPACT =				0x00001000;
	public final int FIELD_MASK2_CVBC_DOCUMENTED_IN =			0x00002000;
	public final int FIELD_MASK2_CVBC_RESPONSE =				0x00004000;
	public final int FIELD_MASK2_CVE_ID =						0x00008000;
	public final int FIELD_MASK2_CVSS_BASE_SCORE =				0x00010000;
	public final int FIELD_MASK2_DESCRIPTION =					0x00020000;
	public final int FIELD_MASK2_DEV_AREA_TO_IMPROVE =			0x00040000;
	public final int FIELD_MASK2_DEV_INTRODUCED_RLI_OR_PR =		0x00080000;
	public final int FIELD_MASK2_DEV_PHASE_TO_IMPROVE =			0x00100000;
	public final int FIELD_MASK2_DUPLICATED_PRS =				0x00200000;
	public final int FIELD_MASK2_ENVIRONMENT =					0x00400000;
	public final int FIELD_MASK2_ENTERMAL_DESCRIPTION =			0x00800000;
	public final int FIELD_MASK2_EXTERNAL_ID =					0x01000000;
	public final int FIELD_MASK2_EXTERNAL_TITLE =				0x02000000;
	public final int FIELD_MASK2_FEEDBACK_DATE =				0x04000000;
	public final int FIELD_MASK2_FIX =							0x08000000;
	public final int FIELD_MASK2_FIX_ETA =						0x10000000;
	public final int FIELD_MASK2_FIX_RISK_LEVEL =				0x20000000;
	public final int FIELD_MASK2_FOUND_DURING =					0x40000000;
	public final int FIELD_MASK2_HOW_TO_REPEAT =				0x80000000;
	
	public final int FIELD_MASK3_JTAC_CASE_ID =					0x00000001;
	public final int FIELD_MASK3_JTAC_SLA =						0x00000002;
	public final int FIELD_MASK3_LAST_KNOWN_WORKING_RELEASE =	0x00000004;
	public final int FIELD_MASK3_LATEST_SUMMARY_STATUS =		0x00000008;
	public final int FIELD_MASK3_NOTIFY_LIST =					0x00000010;
	public final int FIELD_MASK3_NPI_PROGRAM =					0x00000020;
	public final int FIELD_MASK3_PLATFORM =						0x00000040;
	public final int FIELD_MASK3_PR_IMPACT =					0x00000080;
	public final int FIELD_MASK3_PRODUCT =						0x00000100;
	public final int FIELD_MASK3_RELATED_PRS =					0x00000200;
	public final int FIELD_MASK3_RELEASE_BUILD_DATE =			0x00000400;
	public final int FIELD_MASK3_RELEASE_NOTE =					0x00000800;
	public final int FIELD_MASK3_RELATE_NOTE_REQUIRED =			0x00001000;
	public final int FIELD_MASK3_RELEASE_PLAN =					0x00002000;
	public final int FIELD_MASK3_RELEASE_IN_HISTORY =			0x00004000;
	public final int FIELD_MASK3_REPORTED_IN_HISTORY =			0x00008000;
	public final int FIELD_MASK3_RESOLUTION =					0x00010000;
	public final int FIELD_MASK3_RESOLVED_IN =					0x00020000;
	public final int FIELD_MASK3_REVIEW_STATUS =				0x00040000;
	public final int FIELD_MASK3_RLI =							0x00080000;
	public final int FIELD_MASK3_ROOT_CAUSE =					0x00100000;
	public final int FIELD_MASK3_ROOT_CAUSE_ANALYSIS =			0x00200000;
	public final int FIELD_MASK3_SDK_IMPACT =					0x00400000;
	public final int FIELD_MASK3_SHARE_WITH_CUSTOMERS =			0x00800000;
	public final int FIELD_MASK3_SOFTWARE_IMAGE =				0x01000000;
	public final int FIELD_MASK3_SUPPORT_NOTES =				0x02000000;
	public final int FIELD_MASK3_SUPPORTING_DEVICE_PLATFORM =	0x04000000;
	public final int FIELD_MASK3_SUPPORTING_DEVICE_PRODUCT =	0x08000000;
	public final int FIELD_MASK3_SUPPORTING_DEVICE_RELEASE =	0x10000000;
	public final int FIELD_MASK3_SUPPORTING_DEVICE_SW_IMAGE =	0x20000000;
	public final int FIELD_MASK3_SYSTEST_OWNER =				0x40000000;
	public final int FIELD_MASK3_TARGET =						0x80000000;

	public final int FIELD_MASK4_TECHPUBS_NOTES =				0x00000001;
	public final int FIELD_MASK4_TECHPUBS_OWNER =				0x00000002;
	public final int FIELD_MASK4_TEST_AREA_TO_IMPROVE =			0x00000004;
	public final int FIELD_MASK4_TEST_ESCAPE_CAUSE =			0x00000008;
	public final int FIELD_MASK4_TEST_ESCAPE_REASON =			0x00000010;
	public final int FIELD_MASK4_TEST_STATUS =					0x00000020;
	public final int FIELD_MASK4_TEST_TYPE =					0x00000040;
	public final int FIELD_MASK4_TESTCASE_ID =					0x00000080;
	public final int FIELD_MASK4_UNFORMATTED =					0x00000100;
	public final int FIELD_MASK4_UPDATED =						0x00000200;
	public final int FIELD_MASK4_UPDATED_BY_RESPONSIBLE =		0x00000400;
	public final int FIELD_MASK4_VERIFIED_IN =					0x00000800;
	public final int FIELD_MASK4_WORKAROUND =					0x00001000;
	public final int FIELD_MASK4_WORKAROUND_PROVIDED =			0x00002000;

	
	/************** Debug Related *****************/
	private static boolean bInDebug = false;
	private static int iDebugBitMap = 0;
	private static int iDebugSeq = 1;
	private static String sDebugLog = "";
	private static String sDebugFullPath = "", sDebugFileName = "";
	private static FileOutputStream sDebug;
	private static OutputStreamWriter wDebug;
	
	public final int DEBUG_BASIC =		0x00000001;
	public final int DEBUG_NETWORK =	0x00000002;
	public final int DEBUG_PR_PARSER =	0x00000004;
	public final int DEBUG_QUERY =		0x00000008;
	
	public final int DEBUG_DUMP =		0x00000010;
	public final int DEBUG_ACT =		0x00000020;
	public final int DEBUG_PROTO =		0x00000040;
	
	public final int DEBUGTO_LOGCAT =	0x80000000;
	public final int DEBUGTO_SDCARD =	0x40000000;
	
	/* Debug utilities, using bitmap to switch on/off */
	final protected boolean setDebug(boolean d, boolean bSD) {
		File fullPath;
		String sFullPath, sFileName;
		
		bInDebug = d;
		iDebugBitMap |= DEBUGTO_LOGCAT;
		if (!d) {
			bSD = false;
		}
		if (bSD) {
			if ((iDebugBitMap & DEBUGTO_SDCARD) != 0) {
				return true;
			}
			sFullPath = getMyFullPath();
			if (sFullPath == null) {
				iDebugBitMap &= ~DEBUGTO_SDCARD;
				return false;
			}
			sFileName =  "Debug-" + getNowDateTime("", "", "") + ".txt";
	    	try {
	    		fullPath = new File(sFullPath);
	    		if (fullPath.exists() == false) {
	    			if (fullPath.mkdirs() == false) {
	    				d(DEBUG_BASIC, "Directory " + fullPath.toString() + " Create Fail!");
	    	    		return false;
	    			}
	    		}
	    		d(DEBUG_BASIC, "Directory " + fullPath.toString() + " Created!");
	    		File fLog = new File(fullPath, sFileName);
	    		if (fLog.exists() == false) {
	    			if (fLog.createNewFile() == false) {
	    				d(DEBUG_BASIC, "File " + fLog.toString() + " Create Fail!");
	    	    		return false;
	    			}
	    		}
	    		d(DEBUG_BASIC, "File " + fLog.toString() + " Created!");
	    		sDebug = new FileOutputStream(fLog);
	    		wDebug = new OutputStreamWriter(sDebug, "gb2312");

	    		sDebugFullPath = fullPath.toString();
	    		sDebugFileName = sFileName;
	    		iDebugBitMap |= DEBUGTO_SDCARD;
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		d(DEBUG_BASIC, "Debug File Create/Open Fail with Exception:" + e.getMessage());
	    		return false;
	    	}
		} else {
			iDebugBitMap &= ~DEBUGTO_SDCARD;
			sDebugFullPath = "";
			sDebugFileName = "";
		} 
		return true; 
	}
	final protected void setDebugFlag(int iDebugFlag) {
		iDebugBitMap |= iDebugFlag;
	}
	final protected void clearDebugFlag(int iDebugFlag) {
		iDebugBitMap &= ~iDebugFlag;
	}
	final protected int getDebugFlag() {
		return iDebugBitMap;
	}
	final protected boolean isDebugFlagSet(int iDebugFlag) {
		return ((iDebugBitMap & iDebugFlag) == 0)?false:true;
	}
	final protected boolean isDebug() {
		return bInDebug;
	}
	final protected boolean isDebugToSDCard() {
		return (iDebugBitMap & DEBUGTO_SDCARD) != 0;
	}
	final protected String getDebugSDCardFileName() {
		return sDebugFileName;
	}
	final protected String getDebugSDCardFullName() {
		return sDebugFullPath + "/" + sDebugFileName;
	}
	final protected String getDebugLog() {
		return sDebugLog;
	}
	final protected boolean clearDebugLog() {
		sDebugLog = "";
		return true;
	}
	final protected void d(int iDebugFlag, String sDbg) {
		int iZero, iMethodNameLen, i;
		StackTraceElement[] steCall;
		String sMethodName;
		
		if ((bInDebug) && (iDebugFlag & iDebugBitMap) != 0) {
			if ((sDbg == null) || (sDbg.length() == 0)) {
				return;
			}
			iZero = sDbg.indexOf('\0');
			if (iZero != -1) {
				sDbg = sDbg.substring(0, iZero);
			}
			steCall = new Exception().getStackTrace();
			sMethodName = steCall[1].getMethodName();
			iMethodNameLen = sMethodName.length();
			if (iMethodNameLen < 8) {
				for (i=0;i<8-iMethodNameLen;i++) {
					sMethodName = " " + sMethodName;
				}
			} else if (iMethodNameLen > 8) {
				sMethodName = "." + sMethodName.substring(iMethodNameLen-7);
			}
			sDbg =  "<" + iDebugSeq + "-" + getNowTime(":") + "-" + ((steCall.length<10)?"0":"") + 
					steCall.length + "@" + sMethodName + "> " + sDbg;
			if ((iDebugBitMap & DEBUGTO_LOGCAT) != 0) {
				Log.d("GNATS Client Debug", sDbg);
			}
			if (sDbg.lastIndexOf("\r\n") != sDbg.length()-2) {
				sDbg = sDbg + "\r\n";
			}
			if ((iDebugBitMap & DEBUGTO_SDCARD) == 0) {
				if (sDebugLog.length() < 1024*64) {
					sDebugLog += sDbg;
				}
			} else if (wDebug != null) {
				try {
					if (sDebugLog.length() > 0) {
						wDebug.write(sDebugLog);
						sDebugLog = "";
					}
		    		wDebug.write(sDbg);
		    		wDebug.flush();
				} catch (Exception e) {
		    		e.printStackTrace();
		    		Log.d("GNATS Client Debug", "Debug Save Fail with Exception:" + e.getMessage());
				}
			}
			iDebugSeq++;
		}
	}

	/****************** Error Dialog *******************/
	private static int iLastErrCode = 0;
	private static String sLastError;
	public final int ERROR = 	1;
	public final int CAUTION =	2;
	public final int INFO =		3;
	
	/* Error utilities, will pop up dialog when it is not in debug mode */

	final protected boolean setErr(int iLevel, int iErrCode) {
		setErr(iLevel, iErrCode, "Unknown Error!");
		return true;
	}
	final protected boolean setErr(int iLevel, int iErrCode, String sErrMsg) {
		int iZero;
		String sMsg = "";

		if (sErrMsg == null) {
			return false;
		}
		
		iZero = sErrMsg.indexOf('\0');
		if (iZero != -1) {
			sErrMsg = sErrMsg.substring(0, iZero);
		}
		if (iLevel == ERROR) {
			sMsg = "ERROR: ";
		} else if (iLevel == CAUTION) {
			sMsg = "CAUTION: ";
		} else if (iLevel == INFO) {
			sMsg = "INFO: ";
		}
		sMsg += + iErrCode + "\r\n" + sErrMsg;
		d(DEBUG_BASIC, "setErr: " + sMsg);
		iLastErrCode = iErrCode;
		sLastError = sMsg;
		
		if (iLevel == ERROR) {
			if (isProgressOn()) {
				stopProgress();
			}
		}
		
		return true;
	}
	final protected int getLastErrCode() {
		return iLastErrCode;
	}
	final protected String getLastError() {
		return sLastError;
	}
	final protected boolean clearLastError() {
		iLastErrCode = 0;
		sLastError = null;
		return true;
	}
	final protected boolean showLastError(Context ctxCurrent) {
		if ((ctxCurrent == null)||(iLastErrCode == 0)) {
			return false;
		}
		
		AlertDialog.Builder b = new AlertDialog.Builder(ctxCurrent)
			.setTitle("GNATS Client Error")
			.setIcon(R.drawable.alert_dialog_icon)
			.setMessage((sLastError == null)?"N/A":sLastError)
			.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* TODO: add callback to stop the ongoing thing */
					dialog.cancel();
				}
			});
		AlertDialog alert = b.create();
		alert.show();
		clearLastError();
		return true;
	}
	final protected boolean hasErr() {
		if (iLastErrCode != 0) {
			return true;
		}
		return false;
	}
	/************ Progress Dialog ****************/
	/* Progress Dialog Related */
    protected static final int DEFAULT_MAX_PROGRESS = 1;

    private static ProgressDialog pdProgress;
    private static Handler hProgress;
    private static int iProgress = 0;
    private static int iMaxProgress = DEFAULT_MAX_PROGRESS;
    private static boolean bProgressCancelled;
    private static String sProgressTitle, sProgressSeconds;
	

	/* Progress Dialog Utilities */
	final protected boolean createProgress(Context ctxCurrent, String sTitle) {
		pdProgress = new ProgressDialog(ctxCurrent);
		sProgressTitle = sTitle;
		sProgressSeconds = "";
		
		iMaxProgress = DEFAULT_MAX_PROGRESS;
		pdProgress.setTitle(sTitle);
		pdProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pdProgress.setMax(iMaxProgress);
		pdProgress.setButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	/* TODO: add callback to stop the ongoing thing */
            	dialog.cancel();
            	bProgressCancelled = true;
            	d(DEBUG_BASIC, "Progress Cancel!");
            }
        });
		d(DEBUG_BASIC, "Progress dialog \"" + sTitle + "\" created.");
		return true;
	}
	final protected boolean setProgressTitle(String sTitle) {
		if (pdProgress == null) {
			return false;
		}
		sProgressTitle = sTitle;
		pdProgress.setTitle(sProgressTitle + sProgressSeconds);
		return true;
	}
	final protected boolean setProgressSeconds(String sSeconds) {
		if (pdProgress == null) {
			return false;
		}
		sProgressSeconds = sSeconds;
		pdProgress.setTitle(sProgressTitle + sProgressSeconds);
		return true;
	}
	final protected boolean startProgress() {
        iProgress = 0;
        pdProgress.setProgress(0);
        bProgressCancelled = false;
		pdProgress.show();
        
        hProgress = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (pdProgress == null) {
                	return;
                }
        		iProgress = msg.arg1;
                if (iProgress > iMaxProgress) {
                	iProgress = iMaxProgress;
                } else {
                	pdProgress.setProgress(iProgress);
                	d(DEBUG_BASIC, "Progress dialog imcreasing progress.");
                	if (iProgress == iMaxProgress) {
                		d(DEBUG_BASIC, "Progress Done.");
                	}
                }
            }
        };
        d(DEBUG_BASIC, "Progress dialog started.");
        return true;
	}
	final protected boolean setProgress(int iP) {
		Message msg = new Message();
		
		if ((iP < 0) || (iP > iMaxProgress) || (pdProgress == null)) {
			return false;
		}
		msg.arg1 = iP;
		hProgress.sendMessage(msg);
		d(DEBUG_BASIC, "Progress dialog set progress to " + iP);
		return true;
	}
	final protected boolean setMaxProgress(int iMax) {
		if ((iMax >= 0) &&(pdProgress != null)) {
			iMaxProgress = iMax;
			pdProgress.setMax(iMaxProgress);
			d(DEBUG_BASIC, "Progress dialog set max to " + iMax);
		}
		return true;
	}
	final protected boolean stopProgress() {
		if (pdProgress == null) {
			return false;
		}
		pdProgress.dismiss();
		iMaxProgress = DEFAULT_MAX_PROGRESS;
		iProgress = 0;
		sProgressTitle = "";
		sProgressSeconds = "";
		d(DEBUG_BASIC, "Progress Stop!");
		return true;
	}
	final protected boolean isProgressCancelled() {
		return bProgressCancelled;
	}
	final protected boolean isProgressOn() {
		if (pdProgress == null) {
			return false;
		}
		return pdProgress.isShowing();
	}
	final protected boolean setActProgressTitle(Handler h, String sTitle) {
		if (h == null) {
			return false;
		}
		d(DEBUG_BASIC, "Progress Title Set to: " + sTitle);
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("Title", sTitle);
		msg.setData(bundle);
		msg.arg1 = 2;
		h.sendMessage(msg);
		return true;
	}
	final protected boolean setActProgressSeconds(Handler h, String sSeconds) {
		if ((h == null) || (sSeconds == null)) {
			return false;
		}
		if (sSeconds.equals("")) {
			d(DEBUG_BASIC, "Progress Seconds Cleared!");
		} else {
			d(DEBUG_BASIC, "Progress Seconds Set to: " + sSeconds);
		}
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("Seconds", sSeconds);
		msg.setData(bundle);
		msg.arg1 = 3;
		h.sendMessage(msg);
		return true;
	}	
	/************** Timer Utilities *****************/
	private static Timer tmWait;
	private static int iWaitTimeout = 0;
	private static int iStartTimeout = 0;
	
	public final int DEFAULT_WAIT_TIME =	5; /* seconds */
	public final int LONG_WAIT_TIME =		120;	/* seconds */

	private static Handler hTimer;
	
	/* Timer Utilities */
	final protected boolean createTimer(Handler h) {
		/* Start a 0.5 second timer */
		tmWait = new Timer();
		tmWait.schedule(taskWait, 0, 500);
		hTimer = h;
		d(DEBUG_BASIC, "Timer Created!");
		return true;
	}
	final protected TimerTask taskWait = new TimerTask() {
		public void run () {
			if (iWaitTimeout > 0) {
				/* d(DEBUG_BASIC, "Timer Tick " + iWaitTimeout); */
				setActProgressSeconds(hTimer, " (" + (iWaitTimeout/2) + ")");
				iWaitTimeout--;
				if ((iWaitTimeout == 0) && (iStartTimeout != 0)) {
					d(DEBUG_BASIC, "Timer Timeout!");
				}
			}
		}
	};
	final protected void startTimer(int iSeconds) {
		if (iSeconds > 0) {
			iWaitTimeout = iSeconds*2;
			setActProgressSeconds(hTimer, " (" + iSeconds + ")");
			iStartTimeout = iWaitTimeout;
			d(DEBUG_BASIC, "Timer Start (" + iSeconds + " seconds)!");
		}
	}
	final protected void clearTimer() {
		iStartTimeout = 0;
		iWaitTimeout = 0;
		setActProgressSeconds(hTimer, "");
	}
	final protected boolean isTimeout() {
		return ((iWaitTimeout == 0) && (iStartTimeout != 0));
	}

	/************** String to Integer ***************/
	final protected int str2int(String sIn) {
		int iOut;
		if ((sIn == null) || (sIn.equals(""))) {
			return 0;
		}
		try {
			iOut = Integer.parseInt(sIn);
        } catch(NumberFormatException e) { 
 			e.printStackTrace();
 			return 0;
		}
		return iOut;
	}
	/************** Fields Utility ***************/
	final protected String mask2name(int mask, int group) {
		switch (group) {
		case 1:
			switch (mask) {
			case FIELD_MASK1_NUMBER:
			case FIELD_MASK1_SYNOPSIS:
			case FIELD_MASK1_ORIGINATOR:
			case FIELD_MASK1_ARRIVAL_DATE:
			case FIELD_MASK1_CATEGORY:
			case FIELD_MASK1_CLASS:
			case FIELD_MASK1_CONFIDENTIAL:
			case FIELD_MASK1_CUSTOMER_RISK:
			case FIELD_MASK1_FUNCTIONAL_AREA:
			case FIELD_MASK1_KEYWORDS:
			case FIELD_MASK1_LAST_MODIFIED:
			case FIELD_MASK1_PLANNED_RELEASE:
			case FIELD_MASK1_PROBLEM_LEVEL:
			case FIELD_MASK1_REPORTED_IN:
			case FIELD_MASK1_RESPONSIBLE:
			case FIELD_MASK1_DEV_OWNER:
			case FIELD_MASK1_STATE:
			case FIELD_MASK1_SUBMITTER_ID:
			
			case FIELD_MASK1_ATTRIBUTE:
			case FIELD_MASK1_AUTHOR:
			case FIELD_MASK1_BETA_CUSTOMERS:
			case FIELD_MASK1_BETA_PRIORITY:
			case FIELD_MASK1_BETA_PROGRAMS:
			case FIELD_MASK1_BLOCKER:
			case FIELD_MASK1_BLOCKING_HISTORY:
			case FIELD_MASK1_BLOCKING_RELEASE:
			case FIELD_MASK1_BRANCH:
			case FIELD_MASK1_CLIENT_BROWSER:
			case FIELD_MASK1_CLIENT_OS:
			case FIELD_MASK1_CLOSED_DATE:
			case FIELD_MASK1_COMMIT_HISTORY:
			case FIELD_MASK1_COMMITTED_IN:
			default:
			}
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		default:
			return null;
		}
		return null;
	}
	
	final protected String mask2format(int mask1, int mask2, int mask3, int mask4) {
		return null;
	}
	
	/************** Time Utility ***************/
	final private Calendar getNowCalendar() {
    	long Now = System.currentTimeMillis();
    	Calendar calNow = Calendar.getInstance();
    	calNow.setTimeInMillis(Now);
    	
    	return calNow;
	}
	final private String getTimeString(int t) {
		if ((t < 0) || (t > 59)) {
			return "";
		}
		if (t < 10) {
			return "0" + t;
		}
		return "" + t;
	}
	final protected String getNowDateTime(String s1, String s2, String s3) {
		if ((s1 == null) || (s2 == null) || (s3 == null)) { 
			return "";
		}

		Calendar calNow = getNowCalendar();

		String sDateTime = calNow.get(Calendar.YEAR) + s1 + 
				getTimeString(calNow.get(Calendar.MONTH)+1) + s1 + 
				getTimeString(calNow.get(Calendar.DAY_OF_MONTH)) + s2 + 
				getTimeString(calNow.get(Calendar.HOUR_OF_DAY)) + s3 + 
				getTimeString(calNow.get(Calendar.MINUTE));
		
		return sDateTime;
	}
	final protected String getNowTime(String s3) {
		if (s3 == null) { 
			return "";
		}

		Calendar calNow = getNowCalendar();

		String sDateTime = getTimeString(calNow.get(Calendar.HOUR_OF_DAY)) + s3 + 
				getTimeString(calNow.get(Calendar.MINUTE)) + s3 +
				getTimeString(calNow.get(Calendar.SECOND));
		
		return sDateTime;
	}
	
	/************** File Utility ***************/
	final protected boolean saveStringToFile(String sIn, String sPath, String sName) {
    	File fullPath;

    	if ((sIn == null) || (sPath == null) || (sName == null)) {
    		return false;
    	}
    	try {
    		fullPath = new File(sPath);
    		if (fullPath.exists() == false) {
    			if (fullPath.mkdirs() == false) {
    				d(DEBUG_BASIC, "Direcroty " + fullPath.toString() + " Create Fail!");
    	    		return false;
    			}
    		}
    		File fLog = new File(fullPath, sName);
    		if (fLog.exists() == false) {
    			if (fLog.createNewFile() == false) {
    				d(DEBUG_BASIC, "File " + fLog.toString() + " Create Fail!");
    	    		return false;
    			}
    		}
    		FileOutputStream sLog = new FileOutputStream(fLog);
    		OutputStreamWriter wLog = new OutputStreamWriter(sLog, "gb2312");
    		wLog.write(sIn);
    		wLog.flush();
    		wLog.close();
    		
    		sLog.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    		d(DEBUG_BASIC, "String Save Fail with exception:" + e.getMessage());
    		return false;
    	}
    	
    	d(DEBUG_BASIC, "String Save to " + fullPath.toString() + "/" + sName);
        return true;

	}
	final protected String getMyFullPath() {
    	File sdCardPath;
    	String sFullPath;
    	
    	/* Check the SDCard status */
    	if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		d(DEBUG_BASIC, "SD Card is not available!");
    		return null;
    	}
    	
		sdCardPath = Environment.getExternalStorageDirectory();
		sFullPath = sdCardPath.toString() + "/GNATSClient/";
		
		return sFullPath;
	}
}
