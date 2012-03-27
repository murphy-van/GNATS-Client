package com.gnatsclient.android;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GNATSClientActivity extends Activity {
	private GNATSUtility u = new GNATSUtility();
	private GNATSConfig config;
	private EditText etdwim;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /* Watch for button clicks. */
        Button btDWIM = (Button)findViewById(R.id.btdwim);
        Button bt0 = (Button)findViewById(R.id.bt0);
        Button bt1 = (Button)findViewById(R.id.bt1);
        Button bt2 = (Button)findViewById(R.id.bt2);
        Button bt3 = (Button)findViewById(R.id.bt3);
        Button bt4 = (Button)findViewById(R.id.bt4);
        Button bt5 = (Button)findViewById(R.id.bt5);
        Button bt6 = (Button)findViewById(R.id.bt6);
        Button bt7 = (Button)findViewById(R.id.bt7);
        Button bt8 = (Button)findViewById(R.id.bt8);
        Button bt9 = (Button)findViewById(R.id.bt9);
        Button btview = (Button)findViewById(R.id.btview);
        Button btspace = (Button)findViewById(R.id.btspace);
        Button btedit = (Button)findViewById(R.id.btedit);
        Button btcreate = (Button)findViewById(R.id.btcreate);
        Button btquery = (Button)findViewById(R.id.btquery);
        Button btmyprs = (Button)findViewById(R.id.btmyprs);
        Button btmysubs = (Button)findViewById(R.id.btmysubs);
        Button btclear = (Button)findViewById(R.id.btclear);

        btDWIM.setOnClickListener(DoWhatIMean);
        bt0.setOnClickListener(ButtonTextClick);
        bt1.setOnClickListener(ButtonTextClick);
        bt2.setOnClickListener(ButtonTextClick);
        bt3.setOnClickListener(ButtonTextClick);
        bt4.setOnClickListener(ButtonTextClick);
        bt5.setOnClickListener(ButtonTextClick);
        bt6.setOnClickListener(ButtonTextClick);
        bt7.setOnClickListener(ButtonTextClick);
        bt8.setOnClickListener(ButtonTextClick);
        bt9.setOnClickListener(ButtonTextClick);
        btview.setOnClickListener(ButtonViewClick);
        btspace.setOnClickListener(ButtonSpaceClick);
        btedit.setOnClickListener(ButtonEditClick);
        btcreate.setOnClickListener(ButtonCreateClick);
        btquery.setOnClickListener(ButtonQueryClick);
        btmyprs.setOnClickListener(ButtonMyPRsClick);
        btmysubs.setOnClickListener(ButtonMySubsClick);
        btclear.setOnClickListener(ButtonClearClick);

        etdwim = (EditText)findViewById(R.id.etdwim);
        
        config = new GNATSConfig();
        config.loadPreference(GNATSClientActivity.this);

        updateUI();
        
        u.setDebugFlag(config.iDebugFlag);
        u.setDebug(config.bDebug, config.bDebugToSD);
        u.d(u.DEBUG_ACT, "GNATSClientActivity Created.");
		
    }
    
    @Override
	public void onBackPressed() {
    	super.onBackPressed();
        finish();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the currently selected menu XML resource. */
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        
        u.d(u.DEBUG_ACT, "Main Menu Created.");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
        switch (item.getItemId()) {
        case R.id.menuconfig:
        	return loadConfigActivity();
        case R.id.menulog:
        	return loadLogActivity();
        case R.id.menuhelp:
        	return loadHelpActivity();
        case R.id.menuexit:
            u.d(u.DEBUG_ACT, "Menu Exit Pressed.");
        	finish();
        	System.exit(0);
        	break;
        default:
            u.d(u.DEBUG_ACT, "Menu Unknown (" + item.getItemId() + ") Pressed.");
            break;
        }
        
        return false;
    }

	public final static int PRLIST_FAIL =	-1;
	public final static int PRLIST_OK =		0;
	public final static int PRLIST_NOPR =	1;

	private final static int CODE_PRLIST =	1;
	private final static int CODE_CONFIG =	2;
	private final static int CODE_LOG = 	3;
	private final static int CODE_HELP =	4;
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	u.d(u.DEBUG_ACT, "Request Code: " + requestCode + " Return Code: " + resultCode);
    	if (requestCode == CODE_PRLIST) {
	    	switch(resultCode) {
	    	case PRLIST_FAIL:
	    		/* u.setErr(u.CAUTION, 4001, "Query Abort!"); */
	    		break;
	    	case PRLIST_NOPR:
	    		u.setErr(u.CAUTION, 4001, "Query Cannot Find Matched PR!");
	    		break;
	    	case PRLIST_OK:
	    		
	    	default:
	    		break;
	    	}
    	}
    	if (requestCode == CODE_CONFIG) {
    		config.loadPreference(GNATSClientActivity.this);
    		u.setDebugFlag(config.iDebugFlag);
    		u.setDebug(config.bDebug, config.bDebugToSD);
    	}
    	
    	updateUI();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            	u.d(u.DEBUG_ACT, "Activity changes to landscape orientation");
            } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            	u.d(u.DEBUG_ACT, "Activity changes to portrait orientation");
            }
    }
    
    private boolean updateUI() {
    	TextView tvdatabase = (TextView)findViewById(R.id.tvdatabase);
    	if (tvdatabase != null) {
    		tvdatabase.setText(config.sDatabase);
    	}
    	TextView tvuserpassword = (TextView)findViewById(R.id.tvuserpassword);
    	if (tvuserpassword != null) {
    		String sPermission = config.sPermission;
    		if (sPermission == "") {
    			tvuserpassword.setText(config.sUser);
    		} else {
    			tvuserpassword.setText(config.sUser + "(" + sPermission + ")");
    		}
    	}
    	EditText etdwim = (EditText)findViewById(R.id.etdwim);
    	etdwim.setText("");
    	u.showLastError(GNATSClientActivity.this);
    	return true;
    }
    private OnClickListener ButtonTextClick = new OnClickListener() {
        public void onClick(View v) {
            u.d(u.DEBUG_ACT, ((Button)v).getText() + " Pressed.");
        	etdwim.append(((Button)v).getText());
    	}
    };
    private OnClickListener ButtonViewClick = new OnClickListener() {
        public void onClick(View v) {
            u.d(u.DEBUG_ACT, ((Button)v).getText() + " Pressed.");
        	if (etdwim.getText().length() == 0) {
        		etdwim.setText("view ");
        		etdwim.setSelection(5);
        	}
    	}
    };
    private OnClickListener ButtonSpaceClick = new OnClickListener() {
        public void onClick(View v) {
            u.d(u.DEBUG_ACT, ((Button)v).getText() + " Pressed.");
        	etdwim.append(" ");
    	}
    };
    private OnClickListener ButtonEditClick = new OnClickListener() {
        public void onClick(View v) {
            u.d(u.DEBUG_ACT, ((Button)v).getText() + " Pressed.");
        	if (etdwim.getText().length() == 0) {
        		etdwim.setText("edit ");
        		etdwim.setSelection(5);
        	}
    	}
    };
    private OnClickListener ButtonCreateClick = new OnClickListener() {
        public void onClick(View v) {
            u.d(u.DEBUG_ACT, ((Button)v).getText() + " Pressed.");
        	if (etdwim.getText().length() == 0) {
        		etdwim.setText("create");
        		etdwim.setSelection(6);
        	}
    	}
    };
    private OnClickListener ButtonQueryClick = new OnClickListener() {
        public void onClick(View v) {
            u.d(u.DEBUG_ACT, ((Button)v).getText() + " Pressed.");
        	if (etdwim.getText().length() == 0) {
        		etdwim.setText("query");
        		etdwim.setSelection(5);
        	}
    	}
    };
    private OnClickListener ButtonMyPRsClick = new OnClickListener() {
        public void onClick(View v) {
            u.d(u.DEBUG_ACT, ((Button)v).getText() + " Pressed.");
        	if (etdwim.getText().length() == 0) {
        		etdwim.setText("my prs");
        		etdwim.setSelection(6);
        	}
        }
    };
    private OnClickListener ButtonMySubsClick = new OnClickListener() {
        public void onClick(View v) {
            u.d(u.DEBUG_ACT, ((Button)v).getText() + " Pressed.");
        	if (etdwim.getText().length() == 0) {
        		etdwim.setText("my submissions");
        		etdwim.setSelection(14);
        	}
    	}
    };
    private OnClickListener ButtonClearClick = new OnClickListener() {
        public void onClick(View v) {
            u.d(u.DEBUG_ACT, ((Button)v).getText() + " Pressed.");
        	etdwim.setText("");
    	}
    };
    private OnClickListener DoWhatIMean = new OnClickListener() {
        public void onClick(View v) {
            u.d(u.DEBUG_ACT, ((Button)v).getText() + " Pressed.");
        	String sDWIM = etdwim.getText().toString();
        	if (sDWIM.length() == 0) {
        		return;
        	}
        	
        	loadPRListActivity(sDWIM);
        }
    };

    private boolean loadConfigActivity() {
        u.d(u.DEBUG_ACT, "Loading Config Activity.");
    	Intent intent = new Intent(this, GNATSConfigActivity.class);
    	startActivityForResult(intent, CODE_CONFIG);
    	return true;
    }
    private boolean loadLogActivity() {
        u.d(u.DEBUG_ACT, "Loading Log Activity.");
    	Intent intent = new Intent(this, GNATSLogActivity.class);
    	startActivityForResult(intent, CODE_LOG);
    	return true;
    }
    private boolean loadHelpActivity() {
        u.d(u.DEBUG_ACT, "Loading Help Activity.");
    	Intent intent = new Intent(this, GNATSHelpActivity.class);
    	startActivityForResult(intent, CODE_HELP);
    	return true;
    }
    private boolean loadPRListActivity(String sDWIM) {
        u.d(u.DEBUG_ACT, "Loading PRList Activity.");
    	Intent intent = new Intent(this, GNATSPRListActivity.class);
    	intent.putExtra("DWIM", sDWIM);
//    	intent.putExtra("DWIM", "product==\"ns-series\"");
    	startActivityForResult(intent, CODE_PRLIST);
    	return true;
    }
    
}