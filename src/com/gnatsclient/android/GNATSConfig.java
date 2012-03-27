package com.gnatsclient.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class GNATSConfig {
	public String sServer;
	public String sPort;
	public int iPort;
	public String sUser;
	public String sPassword;
	public boolean bDebug;
	public boolean bDebugToSD;
	
	public String sDatabase;
	public String sPermission;
	
	public int iDebugFlag;
	public int iFieldsPredefined;
	public int iFieldsMask1;
	public int iFieldsMask2;
	public int iFieldsMask3;
	public int iFieldsMask4;
	
	public final String PREF_KEY_SERVER =		"GNATS Server";
	public final String PREF_KEY_PORT =			"GNATS Port";
	public final String PREF_KEY_USER =			"GNATS User";
	public final String PREF_KEY_PASSWORD =		"GNATS Password";
	public final String PREF_KEY_DEBUG =		"GNATS Debug";
	public final String PREF_KEY_DEBUG_SD =		"GNATS Debug SD";

	public final String PREF_KEY_DB =			"GNATS Database";
	public final String PREF_KEY_PERMISSION =	"GNATS Permission";

	public final String PREF_KEY_DEBUG_OPTION =	"GNATS Debug Option";
	
	public final String PREF_KEY_FIELD_PREDEF =	"GNATS Field Predefined";
	public final String PREF_KEY_FIELD_MASK1 =	"GNATS Field Mask1";
	public final String PREF_KEY_FIELD_MASK2 =	"GNATS Field Mask2";
	public final String PREF_KEY_FIELD_MASK3 =	"GNATS Field Mask3";
	public final String PREF_KEY_FIELD_MASK4 =	"GNATS Field Mask4";
	
	public final String PREF_DEF_ALL =		"";
	public final String PREF_DEF_PORT =		"1529";
	public final boolean PREF_DEF_DEBUG =	false;
	public final String PREF_DEF_DB =		"default";
	
	public final int PREF_FIELDS_PREDEF_LIST =		0;
	public final int PREF_FIELDS_PREDEF_STANDARD =	1;
	public final int PREF_FIELDS_PREDEF_DETAIL =	2;

	public boolean loadPreference(Context ctxCurrent) {
        SharedPreferences spConfig = 
        		PreferenceManager.getDefaultSharedPreferences(ctxCurrent);
        sServer = spConfig.getString(PREF_KEY_SERVER, "");
        if (sServer.equals("")) {
        	return false;
        }
        sPort = spConfig.getString(PREF_KEY_PORT, PREF_DEF_PORT);
        if (sPort.equals("")) {
        	return false;
        }
	    try {
	 	   	iPort = Integer.parseInt(sPort);
	    } catch(NumberFormatException e) { 
			e.printStackTrace();
			return false;
	    }
        sUser = spConfig.getString(PREF_KEY_USER, "");
        if (sUser.equals("")) {
        	return false;
        }
        sPassword = spConfig.getString(PREF_KEY_PASSWORD, "");
        if (sPassword.equals("")) {
        	sPassword = "*";
        }
        bDebug = spConfig.getBoolean(PREF_KEY_DEBUG, PREF_DEF_DEBUG);
        bDebugToSD = spConfig.getBoolean(PREF_KEY_DEBUG_SD, PREF_DEF_DEBUG);
        sDatabase = spConfig.getString(PREF_KEY_DB, PREF_DEF_DB);
        sPermission = spConfig.getString(PREF_KEY_PERMISSION, "");
        iDebugFlag = spConfig.getInt(PREF_KEY_DEBUG_OPTION, 0);
        iFieldsPredefined = spConfig.getInt(PREF_KEY_FIELD_PREDEF, PREF_FIELDS_PREDEF_STANDARD);
        iFieldsMask1 = spConfig.getInt(PREF_KEY_FIELD_MASK1, 0x00000007);
        iFieldsMask2 = spConfig.getInt(PREF_KEY_FIELD_MASK2, 0x00000000);
        iFieldsMask3 = spConfig.getInt(PREF_KEY_FIELD_MASK3, 0x00000000);
        iFieldsMask4 = spConfig.getInt(PREF_KEY_FIELD_MASK4, 0x00000000);

        return true;
    }
	
    public boolean savePreference(Context ctxCurrent) {
        SharedPreferences spConfig = 
        		PreferenceManager.getDefaultSharedPreferences(ctxCurrent);
        SharedPreferences.Editor seEdit = spConfig.edit();
        
        seEdit.putString(PREF_KEY_SERVER, sServer);
        seEdit.putString(PREF_KEY_PORT, sPort);
        seEdit.putString(PREF_KEY_USER, sUser);
        seEdit.putString(PREF_KEY_PASSWORD, sPassword);
        seEdit.putBoolean(PREF_KEY_DEBUG, bDebug);
        seEdit.putBoolean(PREF_KEY_DEBUG_SD, bDebugToSD);
        
        seEdit.putString(PREF_KEY_DB, sDatabase);
        seEdit.putString(PREF_KEY_PERMISSION, sPermission);
        
        seEdit.putInt(PREF_KEY_DEBUG_OPTION, iDebugFlag);
        seEdit.putInt(PREF_KEY_FIELD_PREDEF, iFieldsPredefined);
        seEdit.putInt(PREF_KEY_FIELD_MASK1, iFieldsMask1);
        seEdit.putInt(PREF_KEY_FIELD_MASK2, iFieldsMask2);
        seEdit.putInt(PREF_KEY_FIELD_MASK3, iFieldsMask3);
        seEdit.putInt(PREF_KEY_FIELD_MASK4, iFieldsMask4);

        seEdit.commit();
        
        return true;
    }
}
