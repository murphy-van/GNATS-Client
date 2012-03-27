package com.gnatsclient.android;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.os.Handler;

public class GNATSQuery {

	private GNATSUtility u;
	private GNATSProtocol p;
	private GNATSConfig config;
	
	private final int DWIM_NOTHING =		0;
	private final int DWIM_VIEW_A_PR =		1;
	private final int DWIM_EDIT_A_PR =		2;
	private final int DWIM_MY_PR =			3;
	private final int DWIM_MY_SUBS =		4;
	private final int DWIM_USER_PR =		5;
	private final int DWIM_A_EXPR =			6;
	private final int DWIM_VIEW_PR_LIST =	7;
	private final int DWIM_CREATE_PR =		8;
	private final int DWIM_ADV_QUERY =		9;
	private final int DWIM_HELP =			10;
	private final int DWIM_UNKNOWN =		99;

	private String sInputUser;
	private String sPRNumber;
	private List<String> lsPRList;

	GNATSQuery(GNATSConfig c) {
		u  = new GNATSUtility();
		p = new GNATSProtocol(c);
		config = c;
	}
	
	public void clearRef(Context ctxCurrent) {
		if (p != null) {
			p.clearRef(ctxCurrent);
			p = null;
		}
		u = null;
		config = null;
		lsPRList = null;
	}
	
	public PRList query(String sDwim, String sFormat, Handler hQuery) {
		String sExpr;
		PRList plResult;
		
		u.d(u.DEBUG_QUERY, "Query Start!");
		if (p.clientConnect() == u.GNATS_ERROR) {
			return null;
		}
		
		sExpr = DWIM2EXPR(sDwim);
		if ((sExpr == null) || (sExpr.length() == 0)) {
			u.d(u.DEBUG_QUERY, "Convert DWIM to expression fail!");
			return null;
		}

		if (p.clientReset() != u.GNATS_OK) {
			u.d(u.DEBUG_QUERY, "Query Rest Fail.");
			return null;
		}
		
		u.d(u.DEBUG_QUERY, "Query Format: \"" + sFormat + "\"");
		plResult = p.clientQuery(sExpr, sFormat, hQuery);
		if (plResult == null) {
			u.d(u.DEBUG_QUERY, "Query Fail!");
		}

		if (p.clientDisconnect() < u.GNATS_OK) {
			u.d(u.DEBUG_QUERY, "Query Disconnect Fail!");
		}

		u.d(u.DEBUG_QUERY, "Query End!");
		return plResult;
	}
	
	protected String DWIM2EXPR(String sDwim) {
		int iCategory;
		String sExpr = "";
		int iListCount, iIndex;
		String sUsername = config.sUser;
		
		if ((sDwim == null) || (sDwim.length() == 0)) {
			return null;
		}
		
		u.d(u.DEBUG_QUERY, "Do What I Mean: \"" + sDwim + "\".");
		iCategory = DWIM2Category(sDwim);
		switch (iCategory) {
		case DWIM_NOTHING:
			break;
		case DWIM_VIEW_A_PR:
			if ((sPRNumber != null)&&(sPRNumber != "")) {
				sExpr = "Number==\"" + sPRNumber + "\"";
			}
			break;
		case DWIM_EDIT_A_PR:
			break;
		case DWIM_MY_PR:
			if ((sUsername != null)&&(sUsername != "")) {
				sExpr = "(!((state==\"suspended\")|(state==\"closed\")))&(responsible==\"" + sUsername + "\")";
			}
			break;
		case DWIM_MY_SUBS:
			if ((sUsername != null)&&(sUsername != "")) {
				sExpr = "(!((state==\"suspended\")|(state==\"closed\")))&(originator==\"" + sUsername + "\")";
			}
			break;
		case DWIM_USER_PR:
			if ((sInputUser != null)&&(sInputUser != "")) {
				sExpr = "(!((state==\"suspended\")|(state==\"closed\")))&(responsible==\"" + sInputUser + "\")";
			}
			break;
		case DWIM_A_EXPR:
			sExpr = sDwim;
			break;
		case DWIM_VIEW_PR_LIST:
			iListCount = lsPRList.size();
			if (iListCount > 0) {
				sExpr = "(Number==\"" + lsPRList.get(0) + "\")";
				iIndex = 1;
				while (iIndex < iListCount) {
					sExpr += " | (Number==\"" + lsPRList.get(iIndex) + "\")";
					iIndex++;
				}
			}
			break;
		case DWIM_CREATE_PR:
			break;
		case DWIM_ADV_QUERY:
			break;
		case DWIM_HELP:
			break;
		case DWIM_UNKNOWN:
		default:
			u.setErr(u.CAUTION, 3001, "Sorry, I was unable to guess what you meant by \"" + sDwim + "\"");
			break;
		}
		
		if (sExpr != null) {
			u.d(u.DEBUG_QUERY, "Parse to expression(" + iCategory + "): \"" + sExpr +"\".");
		}
		
		return sExpr;
	}
	private int DWIM2Category(String sDwim) {
		int iSpace, iComma;
		int iView, iEdit;
		int iPRs;
		
		sDwim = sDwim.toLowerCase();
		sDwim = sDwim.trim();
		
		if (sDwim.length() == 0) {
			return DWIM_NOTHING;
		}
		if ((sDwim.compareTo("my") == 0) || (sDwim.compareTo("my prs") == 0) || 
				(sDwim.compareTo("myprs") == 0) || (sDwim.compareTo("mine") == 0)) {
			return DWIM_MY_PR;
		}
		if ((sDwim.compareTo("my subs") == 0) || (sDwim.compareTo("my subms") == 0) ||
				(sDwim.compareTo("my submissions") == 0) || (sDwim.compareTo("mysubs") == 0)) {
			return DWIM_MY_SUBS;
		}
		if ((sDwim.compareTo("cr") == 0) || (sDwim.compareTo("create") == 0)) {
			return DWIM_CREATE_PR;
		}
		if ((sDwim.compareTo("query") == 0) || (sDwim.compareTo("qu") == 0)) {
			return DWIM_ADV_QUERY;
		}
		if ((sDwim.compareTo("help") == 0) || (sDwim.compareTo("?") == 0)) {
			return DWIM_HELP;
		}
		
		
		if ((sDwim.indexOf('=') != u.GNATS_ERROR) || (sDwim.indexOf('\"') != u.GNATS_ERROR) ||
				(sDwim.indexOf('~') != u.GNATS_ERROR) || (sDwim.indexOf('!') != u.GNATS_ERROR) || 
				(sDwim.indexOf('<') != u.GNATS_ERROR) || (sDwim.indexOf('>') != u.GNATS_ERROR) || 
				(sDwim.indexOf('&') != u.GNATS_ERROR) || (sDwim.indexOf(':') != u.GNATS_ERROR)) {
			if (sDwim.indexOf(',') != u.GNATS_ERROR) {
				return DWIM_UNKNOWN;
			} else {
				sDwim = removeScope(sDwim);
				return DWIM_A_EXPR;
			}
		}
		
		iPRs = sDwim.indexOf(" prs");
		if (iPRs == u.GNATS_ERROR) {
			iPRs = sDwim.indexOf("\'s prs");
		}
		if (iPRs != u.GNATS_ERROR) {
			sInputUser = sDwim.substring(0, iPRs-1);
			return DWIM_USER_PR;
		}
		
		iComma = sDwim.indexOf(',');
		if (iComma != u.GNATS_ERROR) {
			sDwim = sDwim.replace(',', ' ');
		}
		
		sDwim = removeScope(sDwim);
		
		iSpace = sDwim.indexOf(' ');
		if (iSpace != u.GNATS_ERROR) {
			int len;
			
			do {
				len = sDwim.length();
				sDwim = sDwim.replace("  ", " ");
			} while (len != sDwim.length());

			if (sDwim.length() == 1) {
				return DWIM_NOTHING;
			}
			
			iView = sDwim.indexOf("view ");
			if (iView == u.GNATS_ERROR) {
				iView = sDwim.indexOf("vie ");
				if (iView == u.GNATS_ERROR) {
					iView = sDwim.indexOf("vi ");
					if (iView == u.GNATS_ERROR) {
						iView = sDwim.indexOf("v ");
					}
				}
			}
			if (iView != u.GNATS_ERROR) {
				if (iSpace != sDwim.lastIndexOf(' ')) {
					/* more than one space and has view in */
					return DWIM_UNKNOWN;
				}
				sPRNumber = sDwim.substring(iSpace+1);
				return DWIM_VIEW_A_PR;
			}
			iEdit = sDwim.indexOf("edit ");
			if (iEdit == u.GNATS_ERROR) {
				iEdit = sDwim.indexOf("edi ");
				if (iEdit == u.GNATS_ERROR) {
					iEdit = sDwim.indexOf("ed ");
					if (iEdit == u.GNATS_ERROR) {
						iEdit = sDwim.indexOf("e ");
					}
				}
			}
			if (iEdit != u.GNATS_ERROR) {
				if (iSpace != sDwim.lastIndexOf(' ')) {
					/* more than one space and has edit in */
					return DWIM_UNKNOWN;
				}
				sPRNumber = sDwim.substring(iSpace+1);
				return DWIM_EDIT_A_PR;
			}
			
			lsPRList = (List<String>)getListPR(sDwim);
			if (lsPRList == null) {
				return DWIM_UNKNOWN;
			} else {
				return DWIM_VIEW_PR_LIST;
			}
		}
		
		if (u.str2int(sDwim) > 0) {
			sPRNumber = sDwim;
			return DWIM_VIEW_A_PR;
		} else {
			sInputUser = sDwim;
			return DWIM_USER_PR;
		}
	}
	
	List<String> getListPR(String sDwim) {
		String[] PRs = sDwim.split(" ");
		List<String> lsList = Arrays.asList(PRs);
		Iterator<String> isPR = lsList.iterator();
		while (isPR.hasNext()) {
			if (u.str2int(isPR.next().toString()) == 0) {
				return null;
			}
		}
		return lsList;
	}
	String removeScope(String sDwim) {
		int iHyphen = sDwim.indexOf('-');
		int iSpace;
		
		while (iHyphen != u.GNATS_ERROR) {
			iSpace = sDwim.indexOf(' ', iHyphen);
			if (iSpace == u.GNATS_ERROR) {
				return sDwim.substring(0, iHyphen-1);
			}
			sDwim = sDwim.substring(0, iHyphen-1) + sDwim.substring(iSpace);
			iHyphen = sDwim.indexOf('-');
		}
		
		return sDwim;
	}
}
