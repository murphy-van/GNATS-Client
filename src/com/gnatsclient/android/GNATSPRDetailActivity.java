package com.gnatsclient.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class GNATSPRDetailActivity extends ListActivity {

	private final static String FIELD_NAME = "NAME";
	private final static String FIELD_TEXT = "Text";
	
	private List<String> slFields;
	private PRListAdapter adapter;
	private List<Map<String, Object>> smlFieldList;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle bundle = getIntent().getExtras();
        slFields = bundle.getStringArrayList("Fields");
        smlFieldList = getPRFieldList();
        adapter = new PRListAdapter(this);
        setListAdapter(adapter);
    }
    
    private List<Map<String, Object>> getPRFieldList() {
    	List<Map<String, Object>> lFieldListFill = new ArrayList<Map<String, Object>>();
    	Map<String, Object> mField;
    	int iIndex;
    	String sName, sText;
    	
    	if ((slFields == null)||(slFields.size() == 0)) {
    		return null;
    	}
    	for (iIndex=0;iIndex<slFields.size()/2;iIndex++) {
			sName = slFields.get(iIndex*2);
			sText = slFields.get(iIndex*2+1);
			mField = new HashMap<String, Object>();
			mField.put(FIELD_NAME, sName);
			mField.put(FIELD_TEXT, sText);
			lFieldListFill.add(mField);
		}
    	return lFieldListFill;
    }
    
    @Override
	public void onBackPressed() {
    	super.onBackPressed();
    	clearRef();
        finish();
    }
    
    private void clearRef() {
    	slFields = null;
    	smlFieldList = null;
        adapter = null;
        setListAdapter(null);    	
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		showDialog(position);
/*		Intent intent = new Intent(this, GNATSPRDetailActivity.class);
    	intent.putExtra(u.FIELD_NUMBER, (int)id);
    	startActivity(intent);*/  
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	String sName, sText;
    	
		sName = slFields.get(id*2);
		sText = slFields.get(id*2+1);
    	
    	return new AlertDialog.Builder(GNATSPRDetailActivity.this)
        .setIcon(R.drawable.alert_dialog_icon)
        .setTitle(sName)
        .setMessage(sText)
        .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                /* User clicked OK so do some stuff */
            }
        })
        .setNegativeButton("Back", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                /* User clicked Cancel so do some stuff */
            }
        })
        .create();
    }

    public final class ViewHolder {
    	public TextView tvName;
    	public TextView tvText;
    }
    
    public class PRListAdapter extends BaseAdapter {
    	private LayoutInflater liInfater;
    	
    	public PRListAdapter (Context ctx) {
    		this.liInfater = LayoutInflater.from(ctx);
    	}
    	
    	public int getCount() {
    		return (smlFieldList == null)?0:smlFieldList.size();
    	}
    	
    	public Object getItem(int arg0) {
    		return null;
    	}
    	
    	public long getItemId(int arg0) {
/*    		int position = arg0;
    		int iID;
    		iID = liNumber.get(position);
    		return iID;*/
    		return 0;
    	}
    	
    	public View getView(int arg0, View arg1, ViewGroup arg2) {
			int position = arg0;
			View convertView = arg1;
			/*ViewGroup parent = arg2;*/
    		ViewHolder holder = null;
    		if (convertView == null) {
    			holder = new ViewHolder();
    			
    			convertView = liInfater.inflate(R.layout.pr_detail, null);
    			holder.tvName = (TextView)convertView.findViewById(R.id.tvfieldname);
    			holder.tvText = (TextView)convertView.findViewById(R.id.tvfieldtext);
    			convertView.setTag(holder);
    		} else {
    			holder = (ViewHolder)convertView.getTag();
    		}
    		
    		holder.tvName.setText((String)smlFieldList.get(position).get(FIELD_NAME));
    		holder.tvText.setText((String)smlFieldList.get(position).get(FIELD_TEXT));
		
    		return convertView;
		}
    }

}
