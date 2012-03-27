package com.gnatsclient.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class GNATSLogActivity extends Activity {
	private GNATSUtility u = new GNATSUtility();
	private boolean bMemoryLog = false;
	String log;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);
        
        String sDebugFullName = u.getDebugSDCardFullName();
        TextView tvlog = (TextView)findViewById(R.id.tvlog);
        log = u.getDebugLog();
        if ((log != null) && (log != "")) {
            bMemoryLog = true;
            tvlog.setText("Debug in Memory:\r\n\r\n");
            tvlog.append(log);
        } else if (u.isDebugToSDCard() && (sDebugFullName != null) && (!sDebugFullName.equals(""))) {
        	tvlog.setText("Debug in " + sDebugFullName + ":\r\n\r\n");
    		loadLogFromFile(tvlog, sDebugFullName);
        } else {
        	u.d(u.DEBUG_ACT, "No Log can be found to display.");
        	tvlog.setText("No Log Available!");
        }
        
    	u.d(u.DEBUG_ACT, "GNATSLogActivity Created.");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	if (bMemoryLog) {
	        /* Inflate the currently selected menu XML resource. */
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.log_menu, menu);
	    	u.d(u.DEBUG_ACT, "Log Menu Created.");
    	}
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
            case R.id.menulogclear:
            	if (!bMemoryLog) {
            		break;
            	}
            	TextView tvlog = (TextView)findViewById(R.id.tvlog);
            	tvlog.setText("");
            	Toast.makeText(this, "Log cleared!", Toast.LENGTH_SHORT).show();
            	u.clearDebugLog();
            	u.d(u.DEBUG_BASIC, "Log Cleared at " + u.getNowDateTime(".", " ", ":"));
               	tvlog.setText("No Log Available!");
               	bMemoryLog = false;
                return true;
            case R.id.menulogsave:
            	if (!bMemoryLog) {
            		break;
            	}
            	return saveLogToFile();
            default:
                u.d(u.DEBUG_ACT, "Menu Unknown (" + item.getItemId() + ") Pressed.");
                break;
        }
        
        return false;
    }

    private boolean saveLogToFile() {
    	String sFullPath;
    	
    	/* Build debug file name */
    	String sFileName =  "Debug-" + u.getNowDateTime("", "", "") + ".txt";
		sFullPath = u.getMyFullPath();
		if (sFullPath == null) {
	    	u.d(u.DEBUG_ACT, "My Path is not available!");
	    	Toast.makeText(this, "Log save fail!", Toast.LENGTH_LONG).show();
			return false;
		}
			
		if (u.saveStringToFile(log, sFullPath, sFileName) == false) {
	    	u.d(u.DEBUG_ACT, "Log Save Fail!");
	    	Toast.makeText(this, "Log save fail!", Toast.LENGTH_LONG).show();
		} else {    	
	    	u.d(u.DEBUG_ACT, "Log Save to " + sFullPath + "/" + sFileName);
	    	Toast.makeText(this, "Log save to " + sFullPath + "/" + sFileName, 
					Toast.LENGTH_LONG).show();
		}
        return true;
    }
    
    private boolean loadLogFromFile(TextView tv, String sFullName) {
    	String sLine;
    	
    	try {
    		File fLog = new File(sFullName);
    		if (fLog.exists() == false) {
	    		Toast.makeText(this, "File " + fLog.toString() + " does not exist!", 
	    				Toast.LENGTH_SHORT).show();
	    		return false;
    		}
    		FileInputStream sLog = new FileInputStream(fLog);
    		InputStreamReader rLog = new InputStreamReader(sLog);
    		BufferedReader brLog = new BufferedReader(rLog);
    		while ((sLine = brLog.readLine()) != null) {
    			tv.append(sLine + "\r\n");
    		}
    		brLog.close();
    		rLog.close();
    		sLog.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    		u.d(u.DEBUG_ACT, "Log Load Fail with exception:" + e.getMessage());
    		return false;
    	}
    	return true;
    }
}
