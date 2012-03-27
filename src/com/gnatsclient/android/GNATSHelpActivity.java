package com.gnatsclient.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class GNATSHelpActivity extends Activity {

	private GNATSUtility u = new GNATSUtility();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        
        InputStream isHelp = getResources().openRawResource(R.raw.help);
        InputStreamReader irHelp = new InputStreamReader(isHelp);
        BufferedReader brHelp = new BufferedReader(irHelp);
        StringBuilder sbHelp = new StringBuilder();
        String sLine = null;
        PackageManager pm = getPackageManager();
        
        /* Read the version */
        try {
			sbHelp.append("GNATS Client Version: " + pm.getPackageInfo(getPackageName(), 0).versionName + 
					"\r\n\r\n");
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			u.d(u.DEBUG_ACT, e.getMessage());
			return;
		}

        /* Read the Help from raw\help.txt */
        try {
			while ((sLine = brHelp.readLine()) != null) {
				sbHelp.append(sLine + "\r\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			u.d(u.DEBUG_ACT, e.getMessage());
			return;
		}
        
        /* Show the version and help */
        TextView tvhelp = (TextView)findViewById(R.id.tvhelp);
        tvhelp.setText(sbHelp.toString());
        
        u.d(u.DEBUG_ACT, "GNATSHelpActivity Created.");

    }

}
