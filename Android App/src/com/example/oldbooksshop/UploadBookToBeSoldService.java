package com.example.oldbooksshop;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class UploadBookToBeSoldService extends IntentService{

	private final String NAMESPACE = "http://tempuri.org/";
	private final String URL = "http://emf.wmyfriend.com/webservice.asmx";
	private final String METHOD_NAME_ADDBOOKFORUSER = "add_book";
	private final String SOAP_ACTION_ADDBOOK = "http://tempuri.org/add_book";
	public static final String NOTIFICATION_UpBook = "com.example.oldbooksshop.UploadBookToBeSoldService";
	Bundle extras;
	
	public UploadBookToBeSoldService() {
		super("UploadBookToBeSoldService");

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i("UploadBookToBeSoldService", "starting async task");
		extras = intent.getExtras();
		new UploadBookInfo().execute();
	}
	
	class UploadBookInfo extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... arg0) {
			
			String result = null;
			Log.i("UploadBookService",extras.getString("book_image"));
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_ADDBOOKFORUSER);
			request.addProperty("book_name", extras.getString("book_name"));
			request.addProperty("book_author", extras.getString("book_author"));
			request.addProperty("msp", Integer.parseInt(extras.getString("book_msp")));
			request.addProperty("edition", extras.getString("book_edition"));
			request.addProperty("publisher", extras.getString("book_publisher"));
			request.addProperty("image", extras.getString("book_image"));
			request.addProperty("comments", extras.getString("book_comments"));
			request.addProperty("category", extras.getString("book_category"));
			request.addProperty("login_id", extras.getString("user_login_id"));
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		    envelope.dotNet = true;
		    envelope.setOutputSoapObject(request);
		    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		    try {
		    	Log.i("UploadBookToBeSoldService","invoking webservice");
		    	androidHttpTransport.call(SOAP_ACTION_ADDBOOK, envelope);//invoking web service
		        Object response = envelope.getResponse();//get response
		        
		        result = response.toString();
		        
		        Log.i("UploadBookToBeSoldService", "received string: " + result);
		    }catch(Exception e){
		    	Log.i("UploadBookToBeSoldService","Error getting result");
		    	e.printStackTrace();
		    	return false;
		    }
		    //parsing received result
		    try{
		    	if(result.equals("Successfully added")){
		    		return true;
		    	}
		    	else{
		    		return false;
		    	}
		    }catch(Exception e){
		    	e.printStackTrace();
		    	Log.i("UploadBookToBeSoldService","ERROR");
		    	return false;
		    }
		    
		}
		
		@Override
		protected void onPostExecute(Boolean result){
			Log.i("UploadBookToBeSold","in post execute. result is " + result);
			publishResults(result);
		}
		 
		private void publishResults(Boolean result){
			Intent intent = new Intent(NOTIFICATION_UpBook);
		    intent.setAction(NOTIFICATION_UpBook);
			intent.putExtra("Result", result);
		    sendBroadcast(intent);
		}
	 }
	

}
