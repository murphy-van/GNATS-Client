package com.gnatsclient.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GNATSPRListActivity extends ListActivity {
	private final static String[] GNATS_FMT = {
		"\"%F%F%F\" Number Originator Synopsis",
		"standard",
		"full"
	};
	
	private final static int GNATS_QUERY_ABORT =	-1;
	private final static int GNATS_QUERY_DONE =		0;
	private final static int GNATS_QUERY_NOPR =		1;
	private final static int GNATS_PROGRESS_TITLE =	2;
	private final static int GNATS_PROGRESS_SECONDS =	3;
	
	protected static Handler hQuery;
	
	private GNATSUtility u = new GNATSUtility();
	private GNATSConfig config = new GNATSConfig();
	private GNATSQuery q;
	private String sDWIM, sFormat;
	private PRList lPRList;
	private List<Integer> liNumber;
	private PRListAdapter adapter;
	private List<Map<String, Object>> smlPRList;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (config.loadPreference(GNATSPRListActivity.this) == false) {
        	u.d(u.DEBUG_ACT, "Load Configuration Fail.");
        }
        sDWIM = getIntent().getStringExtra("DWIM");
        u.d(u.DEBUG_ACT, "DWIM: " + sDWIM);
        q = new GNATSQuery(config);
        liNumber = new ArrayList<Integer>();
        adapter = new PRListAdapter(this);
        sFormat = GNATS_FMT[config.iFieldsPredefined];
        
        u.createProgress(GNATSPRListActivity.this, "Do What I Mean");
        u.startProgress();
        Thread thq = new Thread() {
        	public void run() {
        		Message msg = new Message();
        		
        		lPRList = q.query(sDWIM, sFormat, hQuery);
        		if (lPRList == null) {
        			msg.arg1 = GNATS_QUERY_ABORT;
        		} else if (lPRList.iPRCount == 0) {
        			msg.arg1 = GNATS_QUERY_NOPR;
        		} else {
        			msg.arg1 = GNATS_QUERY_DONE;
        		}
        		hQuery.sendMessage(msg);
        		q.clearRef(GNATSPRListActivity.this);
        	}
        };
        thq.start();
        
        hQuery = new Handler() {
        	@Override
        	public void handleMessage(Message msg) {
        		Bundle bundle;
        		super.handleMessage(msg);
        		switch (msg.arg1) {
        		case GNATS_QUERY_DONE:
        	        smlPRList = getPRList();
        	        setListAdapter(adapter);
        	        GNATSPRListActivity.this.setResult(GNATSClientActivity.PRLIST_OK);
        	       	break;
        		case GNATS_QUERY_ABORT:
        			GNATSPRListActivity.this.setResult(GNATSClientActivity.PRLIST_FAIL);
        			break;
        		case GNATS_QUERY_NOPR:
        			GNATSPRListActivity.this.setResult(GNATSClientActivity.PRLIST_NOPR);
        			break;
        		case GNATS_PROGRESS_TITLE:
        			bundle = msg.getData();
        			String sTitle = bundle.getString("Title");
        			u.setProgressTitle(sTitle);
        			return;
        		case GNATS_PROGRESS_SECONDS:
        			bundle = msg.getData();
        			String sSeconds = bundle.getString("Seconds");
        			u.setProgressSeconds(sSeconds);
        			return;
        		default:
        		}
    	        u.stopProgress();
    	       	u.showLastError(GNATSPRListActivity.this);
        	}
        };
        
        u.createTimer(hQuery);
    }
    
    private List<Map<String, Object>> getPRList() {
    	List<Map<String, Object>> lPRListFill = new ArrayList<Map<String, Object>>();
    	Map<String, Object> mPR;
    	String sNumber, sOriginator, sSynopsis;
    	int iCount = 0;
    	if ((lPRList == null)||(lPRList.iPRCount == 0)) {
    		return null;
    	}

		Iterator<Entry<Integer, PR>> it = lPRList.mPRs.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, PR> entry = (Map.Entry<Integer, PR>)it.next();
			Integer iNumber = entry.getKey();
			PR prT = entry.getValue();
			sNumber = prT.getTextByName(u.FIELD_NUMBER);
			sOriginator = prT.getTextByName(u.FIELD_ORIGINATOR);
			sSynopsis = prT.getTextByName(u.FIELD_SYNOPSIS);
			mPR = new HashMap<String, Object>();
    		mPR.put(u.FIELD_NUMBER, sNumber);
    		mPR.put(u.FIELD_ORIGINATOR, sOriginator);
    		mPR.put(u.FIELD_SYNOPSIS, sSynopsis);
    		lPRListFill.add(mPR);
    		liNumber.add(iNumber);
    		iCount++;
    		u.d(u.DEBUG_ACT, "getPRList add " + iCount);
		}
		u.d(u.DEBUG_ACT, "getPRList Done!");
    	return lPRListFill;
    }
    
    @Override
	public void onBackPressed() {
    	super.onBackPressed();
    	clearRef();
        finish();
    }
    
    private void clearRef() {
    	if (lPRList != null) {
    		lPRList.clearRef();
        	lPRList = null;
    	}
    	if (q != null) {
    		q.clearRef(GNATSPRListActivity.this);
    		q = null;
    	}
    	smlPRList = null;
    	u = null;
    	config = null;
        adapter = null;
        sDWIM = null;
        liNumber = null;
        hQuery = null;
        setListAdapter(null);    	
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(this, GNATSPRDetailActivity.class);
		Bundle bundle = new Bundle();
    	bundle.putStringArrayList("Fields", lPRList.getPRByNumber((int)id).getArrayNameText());
    	intent.putExtras(bundle);
    	startActivity(intent);
    }
    
    public final class ViewHolder {
    	public TextView tvNumber;
    	public TextView tvOriginator;
    	public TextView tvSynopsis;
    }
    
    public class PRListAdapter extends BaseAdapter {
    	private LayoutInflater liInfater;
    	
    	public PRListAdapter (Context ctx) {
    		this.liInfater = LayoutInflater.from(ctx);
    	}
    	
    	public int getCount() {
    		return (smlPRList == null)?0:smlPRList.size();
    	}
    	
    	public Object getItem(int arg0) {
    		return null;
    	}
    	
    	public long getItemId(int arg0) {
    		int position = arg0;
    		int iID=0;
    		iID = liNumber.get(position);
    		return iID;
    	}
    	
    	public View getView(int arg0, View arg1, ViewGroup arg2) {
			int position = arg0;
			View convertView = arg1;
			/*ViewGroup parent = arg2;*/
    		ViewHolder holder = null;
    		if (convertView == null) {
    			holder = new ViewHolder();
    			
    			convertView = liInfater.inflate(R.layout.pr_list, null);
    			holder.tvNumber = (TextView)convertView.findViewById(R.id.tvprnumber);
    			holder.tvOriginator = (TextView)convertView.findViewById(R.id.tvproriginator);
    			holder.tvSynopsis = (TextView)convertView.findViewById(R.id.tvprsynopsis);
    			convertView.setTag(holder);
    		} else {
    			holder = (ViewHolder)convertView.getTag();
    		}
    		
    		holder.tvNumber.setText((String)smlPRList.get(position).get(u.FIELD_NUMBER));
    		holder.tvOriginator.setText((String)smlPRList.get(position).get(u.FIELD_ORIGINATOR));
    		holder.tvSynopsis.setText((String)smlPRList.get(position).get(u.FIELD_SYNOPSIS));
		
    		return convertView;
		}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the currently selected menu XML resource. */
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pr_list_menu, menu);

        u.d(u.DEBUG_ACT, "PR List Menu Created.");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        	case R.id.menuprlistanalysis:
                Toast.makeText(this, "PR List Analysis", Toast.LENGTH_SHORT).show();
            	return true;
        	case R.id.menuprlistsave:
                Toast.makeText(this, "PR List Saved", Toast.LENGTH_SHORT).show();
            default:
                break;
        }
        
        return false;
    }


}
