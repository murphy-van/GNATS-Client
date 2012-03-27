package com.gnatsclient.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class PRList {
	private GNATSUtility u;
	protected int iPRCount = 0;
	protected Map<Integer, PR> mPRs = new HashMap<Integer, PR>();
	
	PRList() {
		u = new GNATSUtility();
	}

	protected boolean addPR(PR prAdd) {
		if (prAdd == null) {
			return false;
		}
		mPRs.put(prAdd.iNumber, prAdd);
		iPRCount++;
		return true;
	}
	protected PR getPRByNumber(String sNumber) {
		int iNumber = u.str2int(sNumber);
		if (iNumber == 0) {
			return null;
		}
		return getPRByNumber(iNumber);
	}
	protected PR getPRByNumber(int iNumber) {
		return mPRs.get(iNumber);
	}
	protected void clearRef() {
		u = null;
		Iterator<Entry<Integer, PR>> it = mPRs.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, PR> entry = (Map.Entry<Integer, PR>)it.next();
			PR prC = entry.getValue();
			prC.clearRef();
		}
		mPRs = null;
	}
	protected void dumpPRList() {
		Iterator<Entry<Integer, PR>> it = mPRs.entrySet().iterator();
		u.d(u.DEBUG_DUMP, "**********PR List Dump Start**********");
		while (it.hasNext()) {
			Map.Entry<Integer, PR> entry = (Map.Entry<Integer, PR>)it.next();
			Integer iNumber = entry.getKey();
			PR prC = entry.getValue();
			u.d(u.DEBUG_DUMP, "PR(" + iNumber + ")Dump:");
			prC.dumpPR();
		}
		u.d(u.DEBUG_DUMP, "**********PR List Dump End************");
	}
}

class PR {
	private GNATSUtility u;
	
	protected int iNumber;
	protected int iFieldCount = 0;
	protected Map<String, String> mFields = new TreeMap<String, String>();

	PR () {
		u = new GNATSUtility();
	}
	protected boolean addField(PRField pfAdd) {
		if ((pfAdd.sName == null) || (pfAdd.sName.equals("")) || (pfAdd.sText == null)) {
			return false;
		}
		if (pfAdd.sName.equals("Number:")) {
			iNumber = u.str2int(pfAdd.sText);
			if (iNumber == 0) {
				return false;
			}
		}
		mFields.put(pfAdd.sName, pfAdd.sText);
		iFieldCount++;
		return true;
	}
	protected String getTextByName(String sName) {
		return mFields.get(sName).toString();
	}
	protected void clearRef() {
		mFields = null;
	}
	protected void dumpPR() {
		Iterator<Entry<String, String>> it = mFields.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>)it.next();
			String sName = entry.getKey();
			String sText = entry.getValue();
			u.d(u.DEBUG_DUMP, "Name:\"" + sName + "\" Text:\"" + sText + "\"" );
		}
	}
	protected ArrayList<String> getArrayNameText() {
		ArrayList<String> slNameText = new ArrayList<String>();
		Iterator<Entry<String, String>> it = mFields.entrySet().iterator();
		slNameText.add("Number:");
		slNameText.add(getTextByName("Number:"));
		slNameText.add("Synopsis:");
		slNameText.add(getTextByName("Synopsis:"));
		while (it.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>)it.next();
			String sName = entry.getKey();
			String sText = entry.getValue();
			if ((sName.equals("Number:"))||(sName.equals("Synopsis:"))) {
				continue;
			}
			slNameText.add(sName);
			slNameText.add(sText);
		}

		return slNameText;
	}
}

class PRField {
	String sName;
	String sText;
}