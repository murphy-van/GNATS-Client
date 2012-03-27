package com.gnatsclient.android;

import android.content.Context;
import android.os.Handler;

public class GNATSProtocol {

	private final int INPUT_BUF_SHORT =	1024*8;
	private final int INPUT_BUF_LONG =	1024*512;
	
	/* GNATS Server Code */
	protected static final int CODE_GREETING = 				200;
	protected static final int CODE_CLOSING = 				201;
	protected static final int CODE_OK = 					210;
	protected static final int CODE_SEND_PR = 				211;
	protected static final int CODE_SEND_TEXT = 			212;
	protected static final int CODE_SEND_CHANGE_REASON = 	213;
	protected static final int CODE_NO_PRS_MATCHED = 		220;
	protected static final int CODE_NO_ADM_ENTRY = 			221;

	protected static final int CODE_PR_READY = 				300;
	protected static final int CODE_TEXT_READY = 			301;
	protected static final int CODE_INFORMATION = 			350;
	protected static final int CODE_INFORMATION_FILLER = 	351;

	protected static final int CODE_NONEXISTENT_PR = 		400;
	protected static final int CODE_EOF_PR = 				401;
	protected static final int CODE_UNREADABLE_PR = 		402;
	protected static final int CODE_INVALID_PR_CONTENTS = 	403;
	protected static final int CODE_INVALID_FIELD_NAME = 	410;
	protected static final int CODE_INVALID_ENUM = 			411;
	protected static final int CODE_INVALID_DATE = 			412;
	protected static final int CODE_INVALID_FIELD_CONTENTS =413;
	protected static final int CODE_INVALID_SEARCH_TYPE = 	414;
	protected static final int CODE_INVALID_EXPR = 			415;
	protected static final int CODE_INVALID_LIST = 			416;
	protected static final int CODE_INVALID_DATABASE = 		417;
	protected static final int CODE_INVALID_QUERY_FORMAT = 	418;
	protected static final int CODE_INVALID_FIELD_EDIT = 	419;
	protected static final int CODE_NO_KERBEROS = 			420;
	protected static final int CODE_AUTH_TYPE_UNSUP = 		421;
	protected static final int CODE_NO_ACCESS = 			422;
	protected static final int CODE_LOCKED_PR = 			430;
	protected static final int CODE_GNATS_LOCKED = 			431;
	protected static final int CODE_GNATS_NOT_LOCKED = 		432;
	protected static final int CODE_PR_NOT_LOCKED = 		433;
	protected static final int CODE_READONLY_FIELD = 		434;
	protected static final int CODE_INVALID_FTYPE_PROPERTY =435;
	protected static final int CODE_CMD_ERROR = 			440;
	protected static final int CODE_WRITE_PR_FAILED = 		450;

	protected static final int CODE_ERROR = 				600;
	protected static final int CODE_TIMEOUT = 				610;
	protected static final int CODE_NO_GLOBAL_CONFIG = 		620;
	protected static final int CODE_INVALID_GLOBAL_CONFIG = 621;
	protected static final int CODE_INVALID_INDEX = 		622;
	protected static final int CODE_NO_INDEX = 				630;
	protected static final int CODE_FILE_ERROR = 			640;
	
	private GNATSUtility u;
	private GNATSNetwork n;
	private static char[] cpShort, cpLong;
	private boolean bProtoConnected = false;
	private GNATSConfig config;
	private String sDatabase;
	private String sPermission;
	
	private int iInputCode = 0, iPRCount = 0;
	private String sInputText;
	private PRList plQuery;
	
	private Handler hAct;
	
	private final static byte PR_FIELD_TAG_END = ':';
	
	private final static String INPUT_FIELD_PREFIX = "\r\n>";
	private final static String PR_FIELD_NUMBER = "Number:";
	private final static String INPUT_FIELD_NUMBER = INPUT_FIELD_PREFIX + PR_FIELD_NUMBER;
	private final static String INPUT_PR_END = "\r\n.\r\n";
	private final static String INPUT_PR_FAKE_END = "\r\n..\r\n";
	private final static String INPUT_DATABASE = "Now accessing GNATS database";
	private final static String INPUT_PERMISSION = "210 User access level set to";
	
	GNATSProtocol(GNATSConfig c) {
		u = new GNATSUtility();
		n = new GNATSNetwork(c);
		config = c;
		sDatabase = "";
		if (cpShort == null) {
			cpShort = new char[INPUT_BUF_SHORT];
		}
		if (cpLong == null) {
//			cpLong = new char[INPUT_BUF_LONG];
		}
	}
	protected void clearRef(Context ctxCurrent) {
		u = null;
		cpShort = null;
		cpLong = null;
		if (n != null) {
			n.clearRef();
			n = null;
		}
		if (sDatabase != null) {
			config.sDatabase = sDatabase;
			sDatabase = null;
		}
		if (sPermission != null) {
			config.sPermission = sPermission;
			sPermission = null;
		}
		config.savePreference(ctxCurrent);
		config = null;
	}
	
	protected boolean isProtoConnected() {
		return bProtoConnected;
	}
	
	protected int clientConnect() {
		if (bProtoConnected == true) {
			return u.GNATS_OK;
		}
		
		if (n.socketConnect() != u.GNATS_OK) {
			u.d(u.DEBUG_PROTO, "Connect Fail.");
			return u.GNATS_ERROR;
		}
		
		sInputText = null;
		if (clientReadShort() != u.GNATS_OK) {
			u.d(u.DEBUG_PROTO, "Read Welcome Reply Fail.");
			return n.socketDisconnect();
		}
		
		if (clientCHDB(sDatabase) != u.GNATS_OK) {
			u.d(u.DEBUG_PROTO, "Change Database Fail.");
			return n.socketDisconnect();
		}
		
		if (clientUser(config.sUser, config.sPassword) != u.GNATS_OK) {
			u.d(u.DEBUG_PROTO, "User/Password Fail.");
			return n.socketDisconnect();
		}
		
		u.d(u.DEBUG_PROTO, "Protocol Connected.");
		bProtoConnected = true;
		return u.GNATS_OK;
	}
	protected PRList clientQuery(String sExpr, String sFormat, Handler hQuery) {
		int iRet;
		
		if (bProtoConnected == false) {
			return null;
		}
		
		hAct = hQuery;
		
		if (sExpr != "") {
			if (netCommand("EXPR " + sExpr, false) != u.GNATS_OK) {
				u.d(u.DEBUG_PROTO, "Send expression fail!");
				return null;
			}
		}

		if (sendQueryFormat (sFormat) != u.GNATS_OK) {
			u.d(u.DEBUG_PROTO, "Send format fail!");
			return null;
		}
		
		if (sFormat.equals("full")) {
			iRet = sendQuery(true);
		} else {
			iRet = sendQuery(false);
		}
		if (iRet == u.GNATS_TIMEOUT) {
			u.d(u.DEBUG_PROTO, "Query timeout, wait more!");
		} 
		if (iRet != u.GNATS_OK){
			return null;
		}
		
		return plQuery;
	}
	protected int sendQueryFormat (String sFormat)	{
		return netCommand("QFMT " + sFormat, false);
	}
	protected int sendQuery(boolean bLongWait) {
		return netCommand("QUER", bLongWait);
	}
	protected int clientDisconnect() {
		if (bProtoConnected == true) {
			if (clientExit() != u.GNATS_OK) {
				return u.GNATS_ERROR;
			}
			bProtoConnected = false;
		}

		if (n.socketDisconnect() != u.GNATS_OK) {
			return u.GNATS_ERROR;
		}
		return u.GNATS_OK;
	}
	protected int clientReadShort() {
		return clientRead(cpShort, INPUT_BUF_SHORT, u.DEFAULT_WAIT_TIME);
	}
	protected int clientReadLong() {
		return clientRead(cpShort, INPUT_BUF_SHORT, u.LONG_WAIT_TIME);
	}
	protected int clientRead(char[] cpBuf, int iSize, int iWaitTime) {
		String sRead;
		
		sRead = n.socketRead(cpBuf, iSize, iWaitTime);
		 if (sRead == null) {
			u.d(u.DEBUG_PROTO, "Read Fail.");
			return u.GNATS_ERROR;
		} else if (sRead.equals("")) {
			u.d(u.DEBUG_PROTO, "Read Timeout.");
			return u.GNATS_TIMEOUT;
		}
		
		if (clientParseReply(sRead) != u.GNATS_OK) {
			u.d(u.DEBUG_PROTO, "Parse Reply Fail.");
			return u.GNATS_ERROR;
		}
		
		if (clientHandleReply() != u.GNATS_OK) {
			u.d(u.DEBUG_PROTO, "Handle Reply Fail.");
			return u.GNATS_ERROR;
		}
		
		return u.GNATS_OK;
	}
	protected int clientWrite(String sOutput) {
		return n.socketWrite(sOutput);
	}
	protected int clientExit() {
		return netCommand("QUIT", false);
	}
	protected int clientParseReply(String sRead) {
		String sStr3;
		int iCode = 0;
		
		iInputCode = 0;

		if (sRead == null) {
			return u.GNATS_ERROR;
		}
		if (sInputText == null) {
			if (sRead.length() < 5) {
				u.d(u.DEBUG_PROTO, "Content is too short.");
				return u.GNATS_ERROR;
			}
		
			sStr3 = sRead.substring(0, 3);
			iCode = u.str2int(sStr3);
			if (iCode == 0) {
				u.d(u.DEBUG_PROTO, "Code is not readable! (\"" + sStr3 + "\")");
				return u.GNATS_ERROR;
			}
			
			iInputCode = iCode;
			u.d(u.DEBUG_PROTO, "Code " + iCode + " Received.");
			
			sInputText = sRead.substring(4);
		} else {
			sInputText += sRead;
			u.setActProgressTitle(hAct, sInputText.length()/1024 + " KBs Received");
		}
		
		if (sRead.length()>256) {
			u.d(u.DEBUG_DUMP, "Content(" + sRead.length() + " Bytes): " + sRead.substring(0, 256));
		} else {
			u.d(u.DEBUG_DUMP, "Content(" + sRead.length() + "Bytes): " + sRead);
		}
		u.d(u.DEBUG_PROTO, "Total Read " + sInputText.length() + " Bytes.");
		
		if (sInputText.length() >= INPUT_BUF_LONG) {
			u.setErr(u.CAUTION, 1007, "Buffer Overflow! Please enlarge the network buffer in preferance.");
			return u.GNATS_OVERFLOW;
		}
		return u.GNATS_OK;
	}
	private int countInPR(String iInput, String sKey) {
		int iCount = 0;
		int iIndex = 0;
		
		u.d(u.DEBUG_PROTO, "Counting PR");
		while (iIndex != u.GNATS_ERROR) {
			iIndex = iInput.indexOf(sKey, iIndex);
//			u.d(u.DEBUG_PROTO, "Find the " + (iCount+1) + " at " + iIndex);
			if (iIndex != u.GNATS_ERROR) {
				iIndex+=sKey.length();
				iCount++;
			}
		}
		
		return iCount;
	}
	protected int clientHandleReply() {
		int iRet = u.GNATS_ERROR;
		
		u.d(u.DEBUG_PROTO, "Reply Handling...Code (" + iInputCode + ")");
		
		switch (iInputCode) {

	    case CODE_GREETING:
	    	u.d(u.DEBUG_PROTO, "Greeting: " + sInputText);
	    	iRet = u.GNATS_OK;
	    	break;
	    case CODE_OK:
	    	clientGetDatabase(sInputText);
	    	clientGetPermission(sInputText);
	    	iRet = u.GNATS_OK;
	    	break;
	    case CODE_SEND_PR:
	    case CODE_SEND_TEXT:
	    case CODE_SEND_CHANGE_REASON:
	    case CODE_INFORMATION_FILLER:
	    	u.d(u.DEBUG_PROTO, "Text: ");
	    	iRet = u.GNATS_OK;
	    	break;
	    case CODE_CLOSING:
	    	/* server closing down */
	    	u.d(u.DEBUG_PROTO, "Server is closing down!");
	    	iRet = u.GNATS_OK;
		    break;
	    case CODE_PR_READY:
	    case CODE_TEXT_READY:
   			plQuery = readPRFromServer();
   			if (plQuery != null) {
   				u.d(u.DEBUG_PROTO, plQuery.iPRCount + " PR Read into database.");
  	    		plQuery.dumpPRList();
  	    		iRet = u.GNATS_OK;
  	    	}
	    	break;
	    case CODE_INFORMATION:
	    	u.setErr(u.INFO, 2001, "Code Information(" + iInputCode + ")!\r\n" + sInputText);
		    break;
	    case CODE_NONEXISTENT_PR:
	    case CODE_UNREADABLE_PR:
	    case CODE_NO_ACCESS:
	    case CODE_LOCKED_PR:
	    case CODE_FILE_ERROR:
	    case CODE_ERROR:
	    case CODE_NO_ADM_ENTRY:
	    case CODE_INVALID_FIELD_NAME:
	    case CODE_INVALID_PR_CONTENTS:
	    case CODE_INVALID_ENUM:
	    case CODE_INVALID_DATE:
	    case CODE_INVALID_EXPR:
	    case CODE_INVALID_DATABASE:
	    case CODE_INVALID_FIELD_EDIT:
	    	u.setErr(u.ERROR, 2002, "Server Error(" + iInputCode + ")!\r\n" + sInputText);
	    	break;
	    case CODE_PR_NOT_LOCKED:
	    	u.setErr(u.ERROR, 2003, "PR is not locked!\r\n" + sInputText);
	    	break;
	    case CODE_GNATS_LOCKED:
	    	u.setErr(u.ERROR, 2004, "Database is locked!\r\n" + sInputText);
	    	break;
	    case CODE_NO_PRS_MATCHED:
	    	u.setErr(u.INFO, 2005, "No PR is matched!\r\n" + sInputText);
	    	break;
	    case CODE_NO_KERBEROS:
			u.setErr(u.ERROR, 2006, "No Kerberos support, authentication failed\r\n" + sInputText);
			/* FALLTHROUGH */
			break;
		case 0:
		default:
	    	u.setErr(u.ERROR, 2007, "Code " + iInputCode + " is unknown!\r\n" + sInputText);
	    	break;
		}
		
		u.d(u.DEBUG_PROTO, "Handling " + ((iRet == u.GNATS_OK)?"OK!":"Fail/Error!"));
		return iRet;
	}
	protected int netCommand(String sCmd, boolean bLongRead) {
		if (clientWrite(sCmd + "\r\n") != u.GNATS_OK) {
			u.d(u.DEBUG_PROTO, "Command \"" + sCmd + "\" send fail!");
		}
		
		sInputText = null;
		if (bLongRead) {
			return clientReadLong();
		} else {
			return clientReadShort();
		}
	}
	protected int clientCHDB(String sNewRoot) {
		if ((sNewRoot == null) || (sNewRoot.length() == 0)) {
			sNewRoot = "default";
		}

		/* send the change db command and get server's reply */
		return netCommand("CHDB " + sNewRoot, false);
	}
	protected int clientUser(String sUser, String sPass) {
		return netCommand("USER " + config.sUser + " " + config.sPassword, false);
	}
	protected int clientReset() {
		return netCommand("RSET", false);
	}
	private int clientGetDatabase(String sInput) {
		int iIndex, iStart, iEnd;
		iIndex = sInput.indexOf(INPUT_DATABASE);
		if (iIndex == u.GNATS_ERROR) {
			return u.GNATS_ERROR;
		}
		iStart = sInput.indexOf('\'', iIndex + INPUT_DATABASE.length());
		if (iStart == u.GNATS_ERROR) {
			return u.GNATS_ERROR;
		}
		iEnd = sInput.indexOf('\'', iStart + 1);
		if ((iEnd == u.GNATS_ERROR)||(iEnd == iStart + 1)) {
			return u.GNATS_ERROR;
		}
		sDatabase = sInput.substring(iStart+1, iEnd);
		u.d(u.DEBUG_PROTO, "Database: " + sDatabase);
		return u.GNATS_OK;
	}
	private int clientGetPermission(String sInput) {
		int iIndex, iStart, iEnd;
		iIndex = sInput.indexOf(INPUT_PERMISSION);
		if (iIndex == u.GNATS_ERROR) {
			return u.GNATS_ERROR;
		}
		iStart = sInput.indexOf('\'', iIndex + INPUT_PERMISSION.length());
		if (iStart == u.GNATS_ERROR) {
			return u.GNATS_ERROR;
		}
		iEnd = sInput.indexOf('\'', iStart + 1);
		if ((iEnd == u.GNATS_ERROR)||(iEnd == iStart + 1)) {
			return u.GNATS_ERROR;
		}
		sPermission = sInput.substring(iStart+1, iEnd);
		u.d(u.DEBUG_PROTO, "Permission: " + sPermission);
		return u.GNATS_OK;
	}
	private String readFullPRList() {
		String sRead = null;
		
		if (sInputText == null) {
			u.d(u.DEBUG_PROTO, "Input is NULL!");
			return null;
		}
		if (sInputText.length() <= INPUT_BUF_SHORT-4) {
			/* Should have more need to be read */
			u.d(u.DEBUG_PROTO, "Try to Read More PR From Network.");
			do {
				if (clientParseReply(sRead) == u.GNATS_OVERFLOW) {
					break;
				}
				sRead = n.socketRead(cpShort, INPUT_BUF_SHORT, u.DEFAULT_WAIT_TIME);
			} while ((sRead != null)&&(!sRead.equals("")));
			if (sRead == null) {
				u.d(u.DEBUG_PROTO, "Read Fail.");
			} else if (sRead.equals("")) {
				u.d(u.DEBUG_PROTO, "Read Timeout.");
			}
		}
		iPRCount = countInPR(sInputText, INPUT_FIELD_NUMBER);
		u.d(u.DEBUG_PROTO,  + iPRCount + " PR Received.");
		if (iPRCount > 0) {
			u.setMaxProgress(iPRCount);
			u.setActProgressTitle(hAct, "PR Parser");
		}
		return sInputText;
	}
	private PRList readPRFromServer() {
		int iIndex, iCurrentPR, iNextPR;
		String sThisPR, sInput;
		PR prAdd;
		PRList plAdd = new PRList();
		
		if ((readFullPRList() == null) || (iPRCount == 0)) {
			return null;
		}
		sInput = sInputText;
		u.saveStringToFile(sInput, u.getMyFullPath(), "PR.txt");
		u.d(u.DEBUG_PR_PARSER, "Prepare to read " + iPRCount + 
				" PR from input buffer(" + sInput.length() + " Bytes).");
		u.setMaxProgress(iPRCount);
		for (iIndex = 0; iIndex < iPRCount; iIndex++) {
			iCurrentPR = sInput.indexOf(INPUT_FIELD_NUMBER);
			if (iCurrentPR == u.GNATS_ERROR) {
				u.d(u.DEBUG_PR_PARSER, "Abnormal exit from PR loop. index=" + iIndex);
				return null;
			}
			u.d(u.DEBUG_PR_PARSER, "Current PR offset:" + iCurrentPR);
			iNextPR = sInput.indexOf(INPUT_FIELD_NUMBER, iCurrentPR + INPUT_FIELD_NUMBER.length());
			if (iNextPR == u.GNATS_ERROR) {
				u.d(u.DEBUG_PR_PARSER, "This is the last PR");
				iNextPR = sInput.indexOf(INPUT_PR_END, iCurrentPR + INPUT_FIELD_NUMBER.length());
				if (iNextPR == u.GNATS_ERROR) {
					u.setErr(u.CAUTION,  2008, "Cannot find the end of PR signature!");
					return plAdd; /* Ignore the last corrupted PR */
				}
				sThisPR = sInput.substring(iCurrentPR, iNextPR);
			} else {
				u.d(u.DEBUG_PR_PARSER, "Next PR offset:" + iNextPR);
				sThisPR = sInput.substring(iCurrentPR, iNextPR);
				sInput = sInput.substring(iNextPR);
			}
			
			prAdd = readOnePRFromServer(sThisPR);
			if (prAdd == null) {
				u.d(u.DEBUG_PR_PARSER, "Read One PR fail!");
				return null;
			}
			
			plAdd.addPR(prAdd);

			u.d(u.DEBUG_PR_PARSER, "PR added with " + prAdd.iFieldCount + " fileds.");
			if (u.isProgressCancelled()) {
				u.d(u.DEBUG_PR_PARSER, "Cancelled! " + iIndex + " PRs have been processed.");
				return null;
			}
			u.setProgress(iIndex+1);
		}
		return plAdd;
	}
	private PR readOnePRFromServer(String sInput) {
		int iFieldCount, iIndex, iCurrentField, iNextField;
		String sThisField;
		PR prAdd = new PR();
		PRField pfAdd;
	
		iFieldCount = countInPR(sInput, INPUT_FIELD_PREFIX);
		u.d(u.DEBUG_PR_PARSER, "Prepare to read " + iFieldCount + 
				" fields from input buffer(" + sInput.length() + " Bytes).");
		for (iIndex = 0; iIndex < iFieldCount; iIndex++) {
			iCurrentField = sInput.indexOf(INPUT_FIELD_PREFIX);
			if (iCurrentField == u.GNATS_ERROR) {
				u.d(u.DEBUG_PR_PARSER, "Abnormal exit from field loop. index=" + iIndex);
				return null;
			}
			u.d(u.DEBUG_PR_PARSER, "Current field offset:" + iCurrentField);
			iNextField = sInput.indexOf(INPUT_FIELD_PREFIX, iCurrentField + 
					INPUT_FIELD_PREFIX.length());
			if (iNextField == u.GNATS_ERROR) {
				u.d(u.DEBUG_PR_PARSER, "This is the last field.");
				sThisField = sInput.substring(iCurrentField);
			} else {
				u.d(u.DEBUG_PR_PARSER, "Next field offset:" + iNextField);
				sThisField = sInput.substring(iCurrentField, iNextField);
				sInput = sInput.substring(iNextField);
			}
			
			pfAdd = readOneFieldFromServer(sThisField);
			if (pfAdd == null) {
				u.d(u.DEBUG_PR_PARSER, "Read One field fail!");
				return null;
			}
			prAdd.addField(pfAdd);
		}
		
		return prAdd;
	}
	private PRField readOneFieldFromServer(String sInput) {
		String sHeader, sText;
		PRField pfAdd = new PRField();
		
		/* recover the faked end and remove the start prefix */
		sInput = sInput.replace(INPUT_PR_FAKE_END, INPUT_PR_END).substring(2); 
		
		u.d(u.DEBUG_PR_PARSER, sInput);
		
      	sHeader = getFieldHeader (sInput);
      	if (sHeader != null) {
      		u.d(u.DEBUG_PR_PARSER, "Found Header(" + sHeader.length() + 
      				" Bytes): " + sHeader);
      		if (sHeader.indexOf(PR_FIELD_TAG_END) == u.GNATS_ERROR) {
      			u.d(u.DEBUG_PR_PARSER, "Header is not ended with \':\'!");
      			return null;
      		}
      		pfAdd.sName = sHeader;
  			sText = getFieldText(sInput.substring(sHeader.length()+1));
  			if (sText != null) {
  				u.d(u.DEBUG_PR_PARSER, "Found Text(" + sText.length() + 
  						"Bytes): " + sText);
  				pfAdd.sText = sText;
  			}
      	}
		return pfAdd;
	}
	private String getFieldHeader (String sField)
	{
		int iOffsetColon;
		int iOffset = 0;
		
		if ((sField == null) || (sField.length() == 0)) {
			return null;
		}
		
		iOffsetColon = sField.indexOf(PR_FIELD_TAG_END);
		
		/* Grab the first word ending in : */
		if (iOffsetColon >= 0) {
			u.d(u.DEBUG_PR_PARSER, "Found Colon, Offset:" + iOffsetColon);
			/* We want to include the : in the result */
			iOffset = iOffsetColon+1; 
		} else {
			u.d(u.DEBUG_PR_PARSER, "Colon are not found");
			return null;
		}

		return (iOffset == 0)?null:(new String(sField.substring(1, iOffset)));
	}
	private String getFieldText(String sField) {
		int i = 0;
		
		if ((sField == null) || (sField.length() == 0)) {
			return null;
		}
		while (sField.getBytes()[i] == ' ') {
			i++;
			if (i > (sField.length() - 1)) {
				u.d(u.DEBUG_PR_PARSER, "No Text following!");
				return null;
			}
		}
		u.d(u.DEBUG_PR_PARSER, "Skip " + i + " Spaces.");
		return new String(sField.substring(i));
	}

}

