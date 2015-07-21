package com.example.oldbooksshop;

import java.util.ArrayList;
import java.util.List;

import com.example.oldbooksshop.R.id;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowBidders extends Activity{
	
	ListView bidders_listview;
	DatabaseHelper dataHelp;
	ArrayList<String> shopper_ids;
	ArrayList<Integer> shopper_prices;
	ArrayList<Integer> shopper_phone_number;
	BidderArrayAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_show_bidders);
	    
		//bidders_listview = (ListView)findViewById(R.id.bidders_listview);
		dataHelp = new DatabaseHelper(this);
		shopper_prices = dataHelp.getShoppersprice(getIntent().getExtras().getInt("Book_id"));
		shopper_ids = dataHelp.getShopperidsForBook(getIntent().getExtras().getInt("Book_id"));
		shopper_phone_number = dataHelp.getShopperPhoneNumberForBook(getIntent().getExtras().getInt("Book_id"));
		
		if(shopper_prices == null){
			Log.i("ShowBidders","array lists null. setting manually");
			shopper_prices.add(0);
			shopper_phone_number.add(0);
			shopper_ids.add("Nothing here");
		}
		Log.i("ShowBidder","first shooper_id " + shopper_ids.get(0));
		adapter = new BidderArrayAdapter(this,R.layout.bidder_list_listview, R.id.bidder_email_id,shopper_ids);
    	try{
    		bidders_listview = (ListView) findViewById(R.id.bidders_listview);
    		bidders_listview.setAdapter(adapter);
    		bidders_listview.setOnItemClickListener(new OnItemClickListener() {

    			@Override
    			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
    					long arg3) {
    				Toast.makeText(ShowBidders.this,"You selected " + shopper_ids.get(arg2),Toast.LENGTH_SHORT).show();
    				
    			}
    		});

    	}catch(Exception e){
    		e.printStackTrace();
    		Toast.makeText(ShowBidders.this, "Error. Sorry", Toast.LENGTH_SHORT).show();
    	}
    	    	
	}
	
	class BidderArrayAdapter extends ArrayAdapter<String>{
		Context context;
		
			public BidderArrayAdapter(Context context, int resource,int textview_id,List objects) {
				super(context, resource, textview_id, objects);
				int size;
				this.context = context;
				Log.i("Adapter","Constructor");
				if(objects == null){
					size = 0;
				}
				else{
					size = objects.size();
				}
			}
			@Override
			  public View getView(int position, View convertView, ViewGroup parent) {
				Log.i("Setting layout", " for the bidder listView");
			    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    
			    View rowView = inflater.inflate(R.layout.bidder_list_listview, parent, false);
			    Log.i("ShowBidder","Listview set");
			    TextView shopper_email = (TextView)rowView.findViewById(R.id.bidder_email_id);
			    TextView shopper_quoted_price = (TextView)rowView.findViewById(R.id.bidder_quoted_price);
			    TextView shopper_phone_no = (TextView)rowView.findViewById(R.id.bidder_phone_number);
			    
			    Log.i("getView", "shopper email: " + shopper_ids.get(position));
			    shopper_email.setText(shopper_ids.get(position));
			    
			    Log.i("getView", "shopper phone number: " + shopper_phone_number.get(position));
			    shopper_phone_no.setText(shopper_phone_number.get(position).toString());
			    
			    Log.i("getView", "shopper price: " + shopper_prices.get(position));
			    shopper_quoted_price.setText(shopper_prices.get(position).toString());
			    

			    return rowView;
			  }
			
	}

}
