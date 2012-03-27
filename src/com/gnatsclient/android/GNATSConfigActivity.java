package com.gnatsclient.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class GNATSConfigActivity extends Activity {
	private GNATSUtility u = new GNATSUtility();
	private GNATSConfig config = new GNATSConfig();
	
    private EditText etServer;
    private EditText etPort;
    private EditText etUser;
    private EditText etPassword;
    private ToggleButton tbDebug, tbDebugSD;
    private Button btDebugOption;
    private Button btFieldsPredefined;
    
    private static final int DIALOG_DEBUG_OPTION =	1;
    private static final int DIALOG_FILEDS_PREDEF =	2;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);

        etServer = (EditText)findViewById(R.id.etserver);
        etPort = (EditText)findViewById(R.id.etport);
        etUser = (EditText)findViewById(R.id.etuser);
        etPassword = (EditText)findViewById(R.id.etpassword);
        tbDebug = (ToggleButton)findViewById(R.id.tbdebug);
        tbDebugSD = (ToggleButton)findViewById(R.id.tbdebugsd);
        btDebugOption = (Button)findViewById(R.id.btdebugoption);
        btFieldsPredefined = (Button)findViewById(R.id.btfieldpredefined);
        
        btDebugOption.setOnClickListener(ButtonDebugOptionClick);
        btFieldsPredefined.setOnClickListener(ButtonFieldsPredefClick);
        
        config.loadPreference(GNATSConfigActivity.this);
        setConfig();
        
        u.d(u.DEBUG_ACT, "GNATSConfigActivity Created.");
    }
    
    private OnClickListener ButtonDebugOptionClick = new OnClickListener() {
        public void onClick(View v) {
        	showDialog(DIALOG_DEBUG_OPTION);
    	}
    };
    
    private OnClickListener ButtonFieldsPredefClick = new OnClickListener() {
        public void onClick(View v) {
        	showDialog(DIALOG_FILEDS_PREDEF);
    	}
    };
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch (id) {
    	case DIALOG_DEBUG_OPTION:
	    	return new AlertDialog.Builder(GNATSConfigActivity.this)
	        //.setIcon(R.drawable.ic_popup_reminder)
	        .setTitle("Debug Options")
	        .setMultiChoiceItems(R.array.select_dialog_debug_option,
	                new boolean[]{((config.iDebugFlag&u.DEBUG_BASIC) != 0) , 
        					((config.iDebugFlag&u.DEBUG_NETWORK) != 0),
        					((config.iDebugFlag&u.DEBUG_PROTO) != 0),
        					((config.iDebugFlag&u.DEBUG_QUERY) != 0),
        					((config.iDebugFlag&u.DEBUG_ACT) != 0),
        					((config.iDebugFlag&u.DEBUG_PR_PARSER) != 0),
        					((config.iDebugFlag&u.DEBUG_DUMP) != 0),
        				},
	                new DialogInterface.OnMultiChoiceClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton,
	                            boolean isChecked) {
	                    	switch (whichButton) {
	                    	case 0:
	                    		if (isChecked) {
	                    			config.iDebugFlag |= u.DEBUG_BASIC;
	                    		} else {
	                    			config.iDebugFlag &= ~u.DEBUG_BASIC;
	                    		}
	                    		break;
	                    	case 1:
	                    		if (isChecked) {
	                    			config.iDebugFlag |= u.DEBUG_NETWORK;
	                    		} else {
	                    			config.iDebugFlag &= ~u.DEBUG_NETWORK;
	                    		}
	                    		break;
	                    	case 2:
	                    		if (isChecked) {
	                    			config.iDebugFlag |= u.DEBUG_PROTO;
	                    		} else {
	                    			config.iDebugFlag &= ~u.DEBUG_PROTO;
	                    		}
	                    		break;
	                    	case 3:
	                    		if (isChecked) {
	                    			config.iDebugFlag |= u.DEBUG_QUERY;
	                    		} else {
	                    			config.iDebugFlag &= ~u.DEBUG_QUERY;
	                    		}
	                    		break;
	                    	case 4:
	                    		if (isChecked) {
	                    			config.iDebugFlag |= u.DEBUG_ACT;
	                    		} else {
	                    			config.iDebugFlag &= ~u.DEBUG_ACT;
	                    		}
	                    		break;
	                    	case 5:
	                    		if (isChecked) {
	                    			config.iDebugFlag |= u.DEBUG_PR_PARSER;
	                    		} else {
	                    			config.iDebugFlag &= ~u.DEBUG_PR_PARSER;
	                    		}
	                    		break;
	                    	case 6:
	                    		if (isChecked) {
	                    			config.iDebugFlag |= u.DEBUG_DUMP;
	                    		} else {
	                    			config.iDebugFlag &= ~u.DEBUG_DUMP;
	                    		}
	                    		break;
	                    	}
	                        /* User clicked on a check box do some stuff */
	                    }
	                })
	        .setPositiveButton("Back", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	                /* User clicked Yes so do some stuff */
	            }
	        })
	       .create();
    	case DIALOG_FILEDS_PREDEF:
    		return new AlertDialog.Builder(GNATSConfigActivity.this)
//            .setIcon(R.drawable.alert_dialog_icon)
            .setTitle("Fields Predefined")
            .setSingleChoiceItems(R.array.select_dialog_fields_predefined, config.iFieldsPredefined, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
            		config.iFieldsPredefined = whichButton;
                	switch (whichButton) {
                	case 0:
                		break;
                	case 1:
                		break;
                	case 2:
                		break;
                	default:
                	}
                }
            })
            .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    /* User clicked Yes so do some stuff */
                }
            })
           .create();
    		default:
    	}
    	return null;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the currently selected menu XML resource. */
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.config_menu, menu);

        u.d(u.DEBUG_ACT, "Config Menu Created.");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        	case R.id.menuconfigdefault:
        		setConfigDefault();
                Toast.makeText(this, "Default Preference", Toast.LENGTH_SHORT).show();
            	return true;
        	case R.id.menuconfigapply:
        		getConfig();
        		config.savePreference(GNATSConfigActivity.this);
                Toast.makeText(this, "Preference Saved", Toast.LENGTH_SHORT).show();
            case R.id.menuconfigcancel:
            	finish();
                return true;
            default:
                break;
        }
        
        return false;
    }
    
    private boolean setConfigDefault() {
		etServer.setText(config.PREF_DEF_ALL);
		etPort.setText(config.PREF_DEF_PORT);
		etUser.setText(config.PREF_DEF_ALL);
		etPassword.setText(config.PREF_DEF_ALL);
		tbDebug.setChecked(config.PREF_DEF_DEBUG);
		tbDebugSD.setChecked(config.PREF_DEF_DEBUG);
		
		config.iDebugFlag = 0;
		
        u.d(u.DEBUG_ACT, "Reset to Default Preference.");
		return true;
    }
    private boolean getConfig() {
    	config.sServer = etServer.getText().toString();
    	config.sPort = etPort.getText().toString();
    	config.sUser = etUser.getText().toString();
    	config.sPassword = etPassword.getText().toString();
    	if (config.sPort.equals("")) {
    		config.sPort = "1529";
    	}
        try {
     	   	Integer.parseInt(config.sPort);
        } catch(NumberFormatException e) { 
 			e.printStackTrace();
 			config.sPort = config.PREF_DEF_PORT;
        }
        config.bDebug = (tbDebug.getText().toString() == tbDebug.getTextOn().toString());
        config.bDebugToSD = (tbDebugSD.getText().toString() == tbDebugSD.getTextOn().toString());
        
    	return true;
    }
    private boolean setConfig() {
        if (config.sServer != null) {
    	   etServer.setText(config.sServer);
        }
        if (config.sPort != null) {
    	   etPort.setText(config.sPort);
        }
        if (config.sUser != null) {
    	   etUser.setText(config.sUser);
        }
        if (config.sPassword != null) {
    	   etPassword.setText(config.sPassword);
        }
        tbDebug.setChecked(config.bDebug);
        tbDebugSD.setChecked(config.bDebugToSD);
        
        return true;
    }
    


}
