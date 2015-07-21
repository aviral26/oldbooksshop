package com.example.oldbooksshop;

import java.util.Random;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterNewUser extends Activity{
	
	private final String NAMESPACE = "http://tempuri.org/";
	private final String URL = "http://emf.wmyfriend.com/webservice.asmx";
	private final String METHOD_NAME_ADDUSER = "add_user";
	private final String SOAP_ACTION_ADDUSER = "http://tempuri.org/add_user";
	EditText login_id, password1, password2, received_verification_code;
	Button verification_mail_button, registration_button, cancel_button;
	int random_verification_code;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);
        login_id = (EditText)findViewById(R.id.registernewuser_emailid);
        password1 = (EditText)findViewById(R.id.registernewuser_password1);
        password2 = (EditText)findViewById(R.id.registernewuser_password2);
        received_verification_code = (EditText)findViewById(R.id.registernewuser_verificationcode);
        verification_mail_button = (Button)findViewById(R.id.registernewuser_sendverificationcodebutton);
        registration_button = (Button)findViewById(R.id.registernewuser_registrationbutton);
        cancel_button = (Button)findViewById(R.id.registernewuser_cancelbutton);
        
        cancel_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder logoutDialogue = new AlertDialog.Builder(RegisterNewUser.this);
					logoutDialogue.setTitle("Cancel");
					logoutDialogue.setMessage("Are you sure? All changes would be lost.")
					.setCancelable(false)
					.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							dialog.cancel();
							RegisterNewUser.this.finish();
						}
					  })
					.setNegativeButton("No",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
								dialog.cancel();
						}
					});
					AlertDialog alertDialog = logoutDialogue.create();
	 				alertDialog.show();
			}
		});
        
        registration_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(verifyFields(2)){
					registerUser();
				}
				else{
					Toast.makeText(RegisterNewUser.this, "Invalid information", Toast.LENGTH_SHORT).show();
				}
			}
		});
        
        verification_mail_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(verifyFields(1)){
					random_verification_code = new Random().nextInt();
					sendVerificationMail(random_verification_code);
				}
				else{
				Toast.makeText(RegisterNewUser.this, "Provide an email and password.", Toast.LENGTH_SHORT).show();
				}
			}
		});
        
        
	}
	
	private boolean verifyFields(int x){
		switch (x){
			case 1: return !login_id.getText().toString().trim().equals("") && !password1.getText().toString().equals("") && !password2.getText().toString().equals("");
			case 2:	if(password1.getText().toString().equals(password2.getText().toString())){
							if(received_verification_code.getText().toString().trim().equals(new Integer(random_verification_code).toString())){
								Log.i("verifyFields","random code: " + random_verification_code);
								return true;
							}
						}
					
					else{
						return false;
					}
					
			default:Log.i("Register new user", "verify fields returning default"); 
					return false;
		}
		
	}
	
	private void registerUser(){
		new RegisterUser().execute();
	}
	
	class RegisterUser extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {

			String result = null;
			Log.i("RegisterNewUser","initialising..");
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_ADDUSER);
			request.addProperty("login_id", login_id.getText().toString());
			request.addProperty("password", password1.getText().toString());
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		    envelope.dotNet = true;
		    envelope.setOutputSoapObject(request);
		    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		    try {
		    	Log.i("RegisterNewUser","invoking webservice");
		    	androidHttpTransport.call(SOAP_ACTION_ADDUSER, envelope);//invoking web service
		        Object response = envelope.getResponse();//get response
		        
		        result = response.toString();
		        
		        Log.i("RegisterNewUser", "received string: " + result);
		    }catch(Exception e){
		    	Log.i("RegisterNewUser","Error getting result");
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
		    	Log.i("RegisterNewUser","ERROR");
		    	return false;
		    }
			
		}
		@Override
		protected void onPostExecute(Boolean result){
			Log.i("RegisterNewUser","in post execute. result is " + result);
			if(result){
				RegisterNewUser.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(RegisterNewUser.this, "Successfully signed up.", Toast.LENGTH_SHORT).show();
					}
				});
			}
			else{
				RegisterNewUser.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(RegisterNewUser.this, "Registration failed.", Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
	}
	
	private void sendVerificationMail(int random_code){
		new SendVerificationEmail(random_code).execute();
	}
	
	class SendVerificationEmail extends AsyncTask<Void, Void, Boolean>{

		int random_code;
		
		SendVerificationEmail(int random_code){
			this.random_code = random_code;
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			
			
				RegisterNewUser.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(RegisterNewUser.this, "Sending mail..", Toast.LENGTH_SHORT).show();
					}
				});
			
			
			Mail m = new Mail("oldbooksshop.10thjune","puranikitabein");
			m.setTo(new String[]{login_id.getText().toString().trim()});
			m.setFrom("OldBooksShop.1006@gmail.com");
			m.setSubject("Email ID verification, OldBooks Shop");
			m.setBody("Your verification code is " + random_code + ". Copy this and paste it into the textbox provided in the registration page. \n \n \n Best Wishes,\nAviral Takkar\nOldBooks Shop");
			try{
				Log.i("RegisterNewUser","sending mail..");
				if(m.send()) { 
					Log.i("RegisterNewUser","successful");
					return true;
				}
				else{
					Log.i("RegisterNewUser","unsuccessful");
					return false;
				}
			}catch(Exception e){
				e.printStackTrace();
				Log.i("RegisterNewUser","unsuccessful");
				return false;
			}
		}		
		
		@Override
		protected void onPostExecute(Boolean result){
			if(result){
				Toast.makeText(RegisterNewUser.this, "Verification Email sent.", Toast.LENGTH_SHORT).show();
			}
			else{
				Toast.makeText(RegisterNewUser.this, "Error in sending email, retry", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
