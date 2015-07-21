package com.example.oldbooksshop;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class FetchBookInfo extends Activity {
	
	ImageView book_image;
	Button bid_button, submit_bid, cancel;
	TextView name_view, author_view, edition_view, publisher_view, comments_view;
	Bundle extras;
	ProgressDialog nProgress;
	private final String NAMESPACE = "http://tempuri.org/";
	private final String URL = "http://emf.wmyfriend.com/webservice.asmx";
	private final String SOAP_ACTION_ADDSHOPPERINFO = "http://tempuri.org/add_shopper_info";
	private final String METHOD_NAME_ADDSHOPPERINFO = "add_shopper_info";
	
	@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_fetch_book_info);
	        
	        book_image = (ImageView)findViewById(R.id.fetch_bookthumb);
	        bid_button = (Button)findViewById(R.id.fetch_bid_button);
	        name_view = (TextView)findViewById(R.id.fetch_bookname);
	        author_view = (TextView)findViewById(R.id.fetch_bookauthor);
	        edition_view = (TextView)findViewById(R.id.fetch_bookedition);
	        publisher_view = (TextView)findViewById(R.id.fetch_bookpublisher);
	        comments_view = (TextView)findViewById(R.id.fetch_ownercomments);
	        extras = getIntent().getExtras();
	        
	        try{
	        	bid_button.setText("Bid! MSP: " + extras.getInt("Book_msp"));
	        	name_view.setText(extras.getCharSequence("Book_name"));
	        	author_view.setText(extras.getCharSequence("Book_author"));
	        	edition_view.setText("Edition: " + extras.getCharSequence("Book_edition"));
	        	publisher_view.setText("Publisher: " + extras.getCharSequence("Book_publisher"));
	        	comments_view.setText(extras.getCharSequence("Book_comments"));
	        	setImage(extras.getString("Book_image"));
	        }catch(Exception e){
	        	e.printStackTrace();
	        	Toast.makeText(this, "Error loading" , Toast.LENGTH_SHORT).show();
	        }
	        
	        bid_button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					  LayoutInflater mLayoutInflator = (LayoutInflater)FetchBookInfo.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					  View v = mLayoutInflator.inflate(R.layout.popup_bid, null, false);
					  final PopupWindow pw = new PopupWindow(v,600,500,true);
					  pw.showAtLocation(FetchBookInfo.this.findViewById(R.id.fetch_book_container), Gravity.CENTER, 0, 0);
					  pw.setContentView(v);
					  submit_bid = (Button)v.findViewById(R.id.popup_submit_but);
					  cancel = (Button)v.findViewById(R.id.popup_cancel_but);
					  final EditText logid = (EditText)v.findViewById(R.id.popup_emailid);
					  final EditText phone_num = (EditText)v.findViewById(R.id.popup_phonenumber);
					  final EditText quoted_price = (EditText)v.findViewById(R.id.popup_quotedprice);
					  try{
						  submit_bid.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								//submit bid here, direct user back to catalogue
								if(checkLevelOne(logid.getText().toString(),phone_num.getText().toString(),quoted_price.getText().toString())){
									new UploadBid(logid.getText().toString(),quoted_price.getText().toString(),phone_num.getText().toString(),extras.getInt("Book_id")).execute();
								}
								//Toast.makeText(FetchBookInfo.this, "Working", Toast.LENGTH_SHORT).show();
								pw.dismiss();
							}
						  });
						  
						  cancel.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								pw.dismiss();
							}
						});
					  }catch(Exception e){
						  e.printStackTrace();
						  Toast.makeText(FetchBookInfo.this, "Error", Toast.LENGTH_SHORT).show();
					  }
				}
			});
	 }
	
	private void setImage(String image){
		new SetBookThumbNailImage(image, book_image).execute();
	}
	
	private boolean checkLevelOne(String loginid, String phone_num, String quoted_price){
		
		if((loginid.trim().equals("")) && !(phone_num.trim().equals(""))){
			Toast.makeText(FetchBookInfo.this, "Phone number not supported yet.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(!(loginid.trim().equals("")) || !(phone_num.trim().equals(""))){ 
			if(!quoted_price.trim().equals("")){
				return true;
			}
		}
		return false;
	}
	
	class SetBookThumbNailImage extends AsyncTask<Void,Void,Boolean>{
		
		String image;
		Bitmap bitmap;
		ImageView imageView;
		SetBookThumbNailImage(String image, ImageView i){
			this.image = image;
			this.imageView = i;
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			 try{
			       byte [] encodeByte=Base64.decode(image,Base64.DEFAULT);
			       bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
			       return true;
			     }catch(Exception e){
			       e.getMessage();
			       bitmap = null;
			       return false;
			     }
		}
		
		@Override
		protected void onPostExecute(Boolean result){
			if(result){
				imageView.setImageBitmap(bitmap);
			}
			else{
				Log.i("SetImageAsync","Error setting image");
				Toast.makeText(FetchBookInfo.this, "Error setting image", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	class UploadBid extends AsyncTask<Void,Void,Boolean>{

		String shopper_id, quoted_price, phone_num;
		int book_id;
		
		public UploadBid(String shopper_id, String quoted_price, String phone_num,int book_id){
			this.shopper_id = shopper_id;
			this.quoted_price = quoted_price;
			this.book_id = book_id;
			this.phone_num = phone_num;
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			
			FetchBookInfo.this.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					nProgress = new ProgressDialog(FetchBookInfo.this);
					nProgress.setMessage("Uploading information..");
					nProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    		nProgress.setIndeterminate(true);
		    		nProgress.show();
				}
			});
			
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_ADDSHOPPERINFO);
			request.addProperty("shopper_id", shopper_id);
			request.addProperty("book_id", book_id);
			request.addProperty("quoted_price",quoted_price);
			request.addProperty("shopper_phone_number",phone_num);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		    envelope.dotNet = true;
		    envelope.setOutputSoapObject(request);
		    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
			try{
				androidHttpTransport.call(SOAP_ACTION_ADDSHOPPERINFO, envelope);//invoking web service
		        final Object response = envelope.getResponse();//get response
		        if(response.toString().equals("Successfully added")){
		        	
		        	FetchBookInfo.this.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(FetchBookInfo.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
							nProgress.dismiss();
							FetchBookInfo.this.finish();
						}
					});
		        }
		        else{
		        	FetchBookInfo.this.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(FetchBookInfo.this, "Unsuccessful, try again", Toast.LENGTH_SHORT).show();
							nProgress.dismiss();
						}
					});
		        }
		        
		    }catch(Exception e){
		    	e.printStackTrace();
		    	FetchBookInfo.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						nProgress.dismiss();
				    	Toast.makeText(FetchBookInfo.this, "Error..Please try again", Toast.LENGTH_SHORT).show();
					}
				});
		    }
			return null;
		}
		
	}
	
}
