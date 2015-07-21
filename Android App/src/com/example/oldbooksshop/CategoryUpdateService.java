package com.example.oldbooksshop;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

public class CategoryUpdateService extends IntentService{
	
	DatabaseHelper myHelper;
	private final String NAMESPACE = "http://tempuri.org/";
	private final String URL = "http://emf.wmyfriend.com/webservice.asmx";
	private final String METHOD_NAME_GETCATEGORIES = "GetCategories";
	private final String SOAP_ACTION_GETCATEGORIES = "http://tempuri.org/GetCategories";
	public static final String NOTIFICATION = "com.example.oldbooksshop.CATEGORY_UPDATE_SERVICE";
	
	public CategoryUpdateService() {
		super("CategoryUpdateService");
		myHelper = new DatabaseHelper(this);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i("CategoryUpdateService", "starting async task");
		new fetchCategoriesFromDatabase().execute();
	
	}
	
	private void publishResults(boolean status){
		Intent intent = new Intent(NOTIFICATION);
	    intent.setAction(NOTIFICATION);
		intent.putExtra("Result", status);
	    sendBroadcast(intent);
	}
	
	class fetchCategoriesFromDatabase extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... arg0) {
			List<String> temp = new ArrayList<String>();
			String result = null;
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_GETCATEGORIES);
			
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		    envelope.dotNet = true;
		    envelope.setOutputSoapObject(request);
		    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		    try {
		    	Log.i("CategoryUpdateService","invoking webservice");
		    	androidHttpTransport.call(SOAP_ACTION_GETCATEGORIES, envelope);//invoking web service
		        Object response = envelope.getResponse();//get response
		        
		        result = response.toString();
		        
		        Log.i("getCategories", "received string: " + result);
		    }catch(Exception e){
		    	Log.i("CategoryUpdateService","Error getting result");
		    	e.printStackTrace();
		    	return false;
		    }
		    //parsing received result
		    try{
		    	String[] cat = result.split("`");
		    	temp.add("Select a category..");
		    	for(int i=0;i<cat.length;i++){
		    		temp.add(cat[i]);
		    	}
			    myHelper.cacheCategories(temp);
		    }catch(Exception e){
		    	e.printStackTrace();
		    	Log.i("CategoryUpdateService","ERROR");
		    	return false;
		    }
		    
		    return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result){
			Log.i("CategoryUpdateService","in post execute. result is " + result);
			publishResults(result);
		}
		 
	 }

}
