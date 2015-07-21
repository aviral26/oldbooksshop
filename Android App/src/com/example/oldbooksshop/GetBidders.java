package com.example.oldbooksshop;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class GetBidders extends IntentService{
	
	private final String NAMESPACE = "http://tempuri.org/";
	private final String URL = "http://emf.wmyfriend.com/webservice.asmx";
	private final String SOAP_ACTION_GETSHOPPERSBYBOOK = "http://tempuri.org/GetShoppersByBook";
	private final String METHOD_NAME_GETSHOPPERSBYBOOK = "GetShoppersByBook";
	DatabaseHelper myDataHelper;
	
	public GetBidders() {
		super("GetBidders");
		myDataHelper = new DatabaseHelper(this);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		int book_id = intent.getExtras().getInt("Book_id");
		new FetchBidders(book_id,HomePage.user_login_id).execute();
	}
	
	class FetchBidders extends AsyncTask<Void, Void, Boolean>{

		int book_id;
		String login_id;
		FetchBidders(int book_id, String login_id){
			super();
			this.book_id = book_id;
			this.login_id = login_id;
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			
			String result = null;
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_GETSHOPPERSBYBOOK);
			request.addProperty("book_id", book_id);
			request.addProperty("login_id", login_id);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		    envelope.dotNet = true;
		    envelope.setOutputSoapObject(request);
		    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		    
		    try {
		    	Log.i("GetBidders","invoking webservice");
		    	androidHttpTransport.call(SOAP_ACTION_GETSHOPPERSBYBOOK, envelope);//invoking web service
		        Object response = envelope.getResponse();//get response
		        
		        result = response.toString();
		        
		        Log.i("GetBidders", "received string: " + result);
		        if(result.equals("GetShoppersByBookError") || result.equals("anyType{}")){
		        	return false;
		        }
		    }catch(Exception e){
		    	Log.i("GetBidders","Error getting result");
		    	e.printStackTrace();
		    	return false;
		    }
		    
		  //parsing received result
		    try{
		    	String[] bidders = result.split(";");
		    	myDataHelper.clearCachedBidders();
		    	for(int i=0;i<bidders.length;i++){
		    		String [] bidder = bidders[i].split("`");
		    		try{
		    			Log.i("GetBidders","Adding bidder to database.number: " + i);
		    			int temp;
		    			try{
		    				temp = Integer.parseInt(bidder[2]);
		    			}catch(Exception e){
		    				temp = 000;
		    			}
		    			myDataHelper.cacheBidder(bidder[0], book_id, Integer.parseInt(bidder[1]), temp);
		    		}catch(Exception e){
		    			e.printStackTrace();
		    			return false;
		    		}
		    	}
			    
		    }catch(Exception e){
		    	e.printStackTrace();
		    	Log.i("GetBidders","ERROR");
		    	return false;
		    }
		    
		    return true;
			
		}	
		
		@Override
		protected void onPostExecute(Boolean status){
			Log.i("GetBidders","in post execute. result is " + status);
			publishResults(status);
		}
		
	}
	
	private void publishResults(Boolean result){
		Intent intent = new Intent(MyBooksFragment.NOTIFICATION_B);
	    intent.setAction(MyBooksFragment.NOTIFICATION_B);
		intent.putExtra("Result", result);
	    sendBroadcast(intent);
	}

}
