package com.example.oldbooksshop;

import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class GetMyBooksService extends IntentService{

	DatabaseHelper myDbHelper;
	private final String NAMESPACE = "http://tempuri.org/";
	private final String URL = "http://emf.wmyfriend.com/webservice.asmx";
	private final String SOAP_ACTION_GETMYBOOKS = "http://tempuri.org/GetMyBooks";
	private final String METHOD_NAME_GETMYBOOKS = "GetMyBooks";
	
	public GetMyBooksService() {
		super("GetMyBooksService");
		myDbHelper = new DatabaseHelper(this);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i("GetMyBooksService", "starting async task");
		new fetchMyBooksFromDatabase(intent.getExtras().getString("Login_id")).execute();
		
	}
	
	class fetchMyBooksFromDatabase extends AsyncTask<Void, Void, Boolean>{

		String login_id;
		String result;
		
		public fetchMyBooksFromDatabase(String login_id){
			super();
			this.login_id = login_id;
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			
			result = null;
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_GETMYBOOKS);
			request.addProperty("login_id", login_id);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		    envelope.dotNet = true;
		    envelope.setOutputSoapObject(request);
		    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		    
		    try {
		    	Log.i("GetMyBooksService","invoking webservice");
		    	androidHttpTransport.call(SOAP_ACTION_GETMYBOOKS, envelope);//invoking web service
		        Object response = envelope.getResponse();//get response
		        
		        result = response.toString();
		        
		        Log.i("GetMyBooksService", "received string: " + result);
		        if(result.equals("GetMyBooksError")){
		        	return false;
		        }
		    }catch(Exception e){
		    	Log.i("GetMyBooksService","Error getting result");
		    	e.printStackTrace();
		    	return false;
		    }
		    
		  //parsing received result
		    try{
		    	String[] books = result.split(";");
		    	myDbHelper.clearCachedMyBooks();
		    	for(int i=0;i<books.length;i++){
		    		String [] book = books[i].split("`");
		    		try{
		    			Log.i("GetMyBooksService","Adding book to database.number: " + i);
		    			String date;
		    			try{
		    				date = book[7];
		    			}catch(Exception e){
		    				date ="not set";
		    				Log.i("GetMyBooksService","Book Image Not Obtained");
		    			}
		    			myDbHelper.addMyBooksToDatabase(book[0], book[1], (int)Double.parseDouble(book[2]), book[3], book[4], Integer.parseInt(book[5]),book[6],date);
		    		}catch(Exception e){
		    			e.printStackTrace();
		    			return false;
		    		}
		    	}
			    
		    }catch(Exception e){
		    	e.printStackTrace();
		    	Log.i("CategoryUpdateService","ERROR");
		    	return false;
		    }
		    
		    return true;
		}
		
		@Override
		protected void onPostExecute(Boolean status){
			Log.i("GetMyBooksService","in post execute. result is " + status);
			if(result.equals("anyType{}")){
				MyBooksFragment.NOTHING_HERE = true;
			}
			else{
				MyBooksFragment.NOTHING_HERE = false;
			}
			publishResults(status);
		}
	}
	
	private void publishResults(boolean status){
		Intent intent = new Intent(MyBooksFragment.NOTIFICATION_F);
	    intent.setAction(MyBooksFragment.NOTIFICATION_F);
		intent.putExtra("Result", status);
	    sendBroadcast(intent);
	}
}