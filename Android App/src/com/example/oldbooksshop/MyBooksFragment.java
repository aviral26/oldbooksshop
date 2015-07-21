package com.example.oldbooksshop;


import java.util.ArrayList;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyBooksFragment extends Fragment {
	
	//ImageButton refresh;
	static TextView my_books_text;
	View rootView;
	public static boolean NOTHING_HERE;
	static View trialView;
	private static boolean LOGGED_IN;
	public static int current_book_id_selected = -1;
	private final String NAMESPACE = "http://tempuri.org/";
	private final String URL = "http://emf.wmyfriend.com/webservice.asmx";
	private final String SOAP_ACTION_REMOVEMYBOOK = "http://tempuri.org/removeMyBook";
	private final String METHOD_NAME_REMOVEMYBOOK = "removeMyBook";
	public static final String NOTIFICATION_F = "com.example.oldbooksshop.FETCH_MY_BOOKS_SERVICE";
	public static final String NOTIFICATION_B = "com.example.oldbooksshop.GET_BIDDERS_SERVICE";
	
	static ListView myBookList;
	ArrayList<String> myList;
	ArrayList<String> myAuthorList,myBookEdition, myBookPublisher, myBookImage, myBookComments,myBookValidTill;
	ArrayList<Integer> myBookIds, myBookMsp;
	StableArrayAdapter2 adapter;
	
	private BroadcastReceiver receiver_set_my_books = new BroadcastReceiver(){
		
		public void onReceive(Context context, Intent intent) {
			Log.i("MyBooksFragment","BrocastReceived: " + intent.getAction().toString());
			if(intent.getAction().toString().equals(MyBooksFragment.NOTIFICATION_F)){
				Log.i("MyBooksFragment","We're in");
				if(intent.getExtras().getBoolean("Result")){
					Log.i("MyBooksFragment","Result is true");
					setMyBooks();
				}
				else{
					Log.i("MyBooksFragment","broadcast received is false");
					//Toast.makeText(MyBooksFragment.this.getActivity(), "Nothing here", Toast.LENGTH_SHORT).show();
				}
				HomePage.setRefreshActionButtonState(false);
			}
		};
	
	};
	
private BroadcastReceiver receiver_show_bidders = new BroadcastReceiver(){
		
		public void onReceive(Context context, Intent intent) {
			Log.i("MyBooksFragment","BrocastReceived: " + intent.getAction().toString());
			if(intent.getAction().toString().equals(MyBooksFragment.NOTIFICATION_B)){
				Log.i("MyBooksFragment","We're in");
				if(intent.getExtras().getBoolean("Result") && current_book_id_selected != -1){
					Log.i("MyBooksFragment","Result is true");
					Intent new_in = new Intent(MyBooksFragment.this.getActivity(), ShowBidders.class);
					new_in.putExtra("Book_id", current_book_id_selected);
					startActivity(new_in);
				}
				else{
					Log.i("MyBooksFragment","broadcast received is false or current_book_id_selected: " + current_book_id_selected);
					AlertDialog.Builder logoutDialogue = new AlertDialog.Builder(MyBooksFragment.this.getActivity());
					logoutDialogue.setTitle("No bidders yet");
					logoutDialogue.setMessage("Still awaiting bids.")
					.setCancelable(false)
					.setPositiveButton("Okay",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							dialog.cancel();
						}
					  });
	 				AlertDialog alertDialog = logoutDialogue.create();
	 				alertDialog.show();
					//Toast.makeText(MyBooksFragment.this.getActivity(), "No bidders", Toast.LENGTH_SHORT).show();
				}
				HomePage.setRefreshActionButtonState(false);
			}
		};
	
	};

	private BroadcastReceiver receiver_uploaded_book = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			Log.i("MyBooksFragment","BrocastReceived: " + intent.getAction().toString());
				Bundle extras = intent.getExtras();
				Boolean result = extras.getBoolean("Result");
				Log.i("MyBooksFragment: ",intent.getAction().toString());
			
				if(intent.getAction().toString().equals(UploadBookToBeSoldService.NOTIFICATION_UpBook) && result){
					Log.i("MyBooksFragment", "uploadbooktobesold service returned true");
				
					MyBooksFragment.this.getActivity().runOnUiThread(new Runnable() {
					
						@Override
						public void run() {
							AlertDialog.Builder logoutDialogue = new AlertDialog.Builder(MyBooksFragment.this.getActivity());
								logoutDialogue.setTitle("Sell book");
								logoutDialogue.setMessage("Book successfully recorded for sale in catalogue.")
								.setCancelable(false)
								.setPositiveButton("Okay",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {
										dialog.cancel();
									}
								});
								AlertDialog alertDialog = logoutDialogue.create();
								alertDialog.show();
								Intent intent = new Intent(MyBooksFragment.this.getActivity(), CategoryUpdateService.class);
								Toast.makeText(MyBooksFragment.this.getActivity(), "Updating categories..", Toast.LENGTH_SHORT).show();
								MyBooksFragment.this.getActivity().startService(intent);
						}
					});
				}
				else{
					MyBooksFragment.this.getActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							 AlertDialog.Builder logoutDialogue = new AlertDialog.Builder(MyBooksFragment.this.getActivity());
								logoutDialogue.setTitle("Sell book");
								logoutDialogue.setMessage("Error encountered while uploading book information. Try again")
								.setCancelable(false)
								.setPositiveButton("Okay",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {
										dialog.cancel();
								  }
								});
							AlertDialog alertDialog = logoutDialogue.create();
							alertDialog.show();
						}
					});
				}
			}
	};
	
	public MyBooksFragment() {
		LOGGED_IN = false;
	}
	
	public static void  setLoggedin(boolean set){
		LOGGED_IN = set;
		
	}
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	 
	        rootView = inflater.inflate(R.layout.fragment_my_books, container, false);
	        trialView = rootView;
	        myBookList = (ListView)rootView.findViewById(R.id.my_books_listview);
	        myList = new ArrayList<String>();
        	myAuthorList = new ArrayList<String>();
        	myBookIds = new ArrayList<Integer>();
        	myBookEdition = new ArrayList<String>();
        	myBookPublisher = new ArrayList<String>();
        	myBookImage = new ArrayList<String>();
        	myBookComments = new ArrayList<String>();
        	myBookMsp = new ArrayList<Integer>();
        	my_books_text = (TextView)rootView.findViewById(R.id.my_books_text_view);
        	myBookValidTill = new ArrayList<String>();
        	
        	myBookList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,	int position, long id) {
					
					/*if(HomePage.myDatabaseHelper.checkIfBidderCachedForBook(myBookIds.get(position))){//making this false for now
						Log.i("MyBooksFragment","Bidders cached for book");
						Intent intent = new Intent(MyBooksFragment.this.getActivity(), ShowBidders.class);
						intent.putExtra("Book_id", myBookIds.get(position));
						startActivity(intent);
					}*/
					
					/*else{*/
						Log.i("MyBooksFragment","Bidders not cached for book");
						if(myBookIds.get(position) == -1){
							Toast.makeText(MyBooksFragment.this.getActivity(),"Internal error, sorry", Toast.LENGTH_SHORT).show();
						}
						else{
							Intent intent = new Intent(MyBooksFragment.this.getActivity(), GetBidders.class);
							intent.putExtra("Book_id", myBookIds.get(position));
							current_book_id_selected = myBookIds.get(position);
							Log.i("MyBooksFragment:","Book id selected is " + current_book_id_selected);
							Toast.makeText(MyBooksFragment.this.getActivity(),"Fetching bidders..", Toast.LENGTH_SHORT).show();
							MyBooksFragment.this.getActivity().startService(intent);
						}
					/*}*/
				}
        		
        	});
        	
	        return rootView;
	 }
	 
	 @Override
		public void onResume(){
			super.onResume();
			MyBooksFragment.this.getActivity().registerReceiver(receiver_show_bidders, new IntentFilter(MyBooksFragment.NOTIFICATION_B));
			MyBooksFragment.this.getActivity().registerReceiver(receiver_set_my_books, new IntentFilter(MyBooksFragment.NOTIFICATION_F));
			MyBooksFragment.this.getActivity().registerReceiver(receiver_uploaded_book, new IntentFilter(UploadBookToBeSoldService.NOTIFICATION_UpBook));
		}
		
		@Override
		public void onPause() {
		    super.onPause();
		    MyBooksFragment.this.getActivity().unregisterReceiver(receiver_show_bidders);
		    MyBooksFragment.this.getActivity().unregisterReceiver(receiver_set_my_books);
		    MyBooksFragment.this.getActivity().unregisterReceiver(receiver_uploaded_book);
		    
		  }
	 
	 public static boolean setBackGroundAndLoginStatus(){
		 if(checkLoginStatus()){
	        	LOGGED_IN = true;
	        	trialView.setBackgroundResource(R.drawable.abc_ab_bottom_transparent_light_holo);
	        	myBookList.setVisibility(View.VISIBLE);
	        	my_books_text.setVisibility(View.VISIBLE);
	        	return true;
		 }
	     else{
	    	 	LOGGED_IN = false;
	    	 	
	        	trialView.setBackgroundResource(R.drawable.login_first);
	        	myBookList.setVisibility(View.GONE);
	        	my_books_text.setVisibility(View.GONE);
	        	return false;
	     }
	 }
	 
	 private static boolean checkLoginStatus(){
		 return HomePage.login_status;
	 }
	 
	 public void setMyBooks(){
		 if(HomePage.myDatabaseHelper.checkIfMyBooksCached()){
			 try{
				 if(myBookIds != null && myList != null && myAuthorList != null && myBookMsp != null && myBookEdition != null && myBookPublisher != null){
					 myList.clear();
					 myAuthorList.clear();
					 myBookMsp.clear();
					 myBookEdition.clear();
					 myBookPublisher.clear();
					 myBookIds.clear();
					 myBookImage.clear();
					 myBookValidTill.clear();
				 }
			 }catch(Exception e){
				 myList = new ArrayList<String>();
		         myAuthorList = new ArrayList<String>();
		         myBookEdition = new ArrayList<String>();
		         myBookMsp = new ArrayList<Integer>();
		         myBookPublisher = new ArrayList<String>();
		         myBookIds = new ArrayList<Integer>();
		         myBookImage = new ArrayList<String>();
		         myBookValidTill = new ArrayList<String>();
			 }
			 
			 Log.i("MyBooksFragment","found cached my books");
			 myList = HomePage.myDatabaseHelper.getMyBooksNames();
			 myAuthorList = HomePage.myDatabaseHelper.getMyBooksAuthors();
			 myBookMsp = HomePage.myDatabaseHelper.getMyBooksMsp();
			 myBookEdition = HomePage.myDatabaseHelper.getMyBooksEdition();
			 myBookPublisher = HomePage.myDatabaseHelper.getMyBooksPublishers();
			 myBookIds = HomePage.myDatabaseHelper.getMyBooksId();
			 myBookImage = HomePage.myDatabaseHelper.getMyBooksImage();
			 myBookValidTill = HomePage.myDatabaseHelper.getMyBooksValidTill();
			 adapter = new StableArrayAdapter2(this.getActivity(),R.layout.listview_layout, R.id.book_title_listview,myList);
		     myBookList.setAdapter(adapter);
		 }
		 else{
			 Log.i("MyBooksFragment","found no books cached");
			 
			 Intent intent = new Intent(this.getActivity(),GetMyBooksService.class);
			 intent.putExtra("Login_id", HomePage.user_login_id);
			 Toast.makeText(MyBooksFragment.this.getActivity(), "Updating..", Toast.LENGTH_SHORT).show();
			 Log.i("MyBooksFragment","launching service to fetch books");
			 this.getActivity().startService(intent);
		 }
	 }
	 

	 class StableArrayAdapter2 extends ArrayAdapter<String>{
			Context context;
			
				public StableArrayAdapter2(Context context, int resource,int textview_id,List objects) {
					super(context, resource, textview_id, objects);
					int size;
					this.context = context;
					if(objects == null){
						size = 0;
					}
					else{
						size = objects.size();
					}
				}
				@Override
				  public View getView(int position, View convertView, ViewGroup parent) {
					final int pos = position;
					Log.i("Setting layout", " fo the listView");
				    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				    View rowView = inflater.inflate(R.layout.listview_layout, parent, false);
				    TextView textView_name = (TextView)rowView.findViewById(R.id.book_title_listview);
				    TextView textView_des = (TextView) rowView.findViewById(R.id.description_line);
				    ImageView imageView = (ImageView) rowView.findViewById(R.id.book_thumbnail);
				    Log.i("getView", "bookName: " + myList.get(position));
				    textView_name.setText(myList.get(position));
				    Log.i("getView", "authorName: " + myAuthorList.get(position));
				    textView_des.setText("Author: " + myAuthorList.get(position));
				    TextView edition = (TextView)rowView.findViewById(R.id.listview_edition);
				    TextView publisher = (TextView)rowView.findViewById(R.id.listview_publisher);
				    Button cancel = (Button)rowView.findViewById(R.id.listview_remove);
				    TextView validTill = (TextView)rowView.findViewById(R.id.listview_valid_till);
				    
				    cancel.setVisibility(View.VISIBLE);
				    validTill.setVisibility(View.VISIBLE);
				    
				    Log.i("getView", "Valid Till: " + myBookValidTill.get(position).split(" ")[0]);
				    validTill.setText("Valid till " + myBookValidTill.get(position).split(" ")[0]);
				    Log.i("getView", "edition: " + myBookEdition.get(position));
				    edition.setText("Ed: " + myBookEdition.get(position));
				    Log.i("getView", "publisher: " + myBookPublisher.get(position));
				    publisher.setText("Pblshr: " + myBookPublisher.get(position));
				    cancel.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							Log.i("MyBooksFragment","sold button clicked");
							MyBooksFragment.this.getActivity().runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									AlertDialog.Builder logoutDialogue = new AlertDialog.Builder(MyBooksFragment.this.getActivity());
										logoutDialogue.setTitle("Remove Book");
										logoutDialogue.setMessage("Have you sold this book?")
										.setCancelable(false)
										.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,int id) {
												removeMyBookFromServer(pos);
												dialog.cancel();
											}
										  })
										.setNegativeButton("No",new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,int id) {
   												dialog.cancel();
   												Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
											}
										});
										
										AlertDialog alertDialog = logoutDialogue.create();
			      						alertDialog.show();
									//removeMyBookFromServer(pos);
																		
								}
							});
							
						}
					});
				   new SetListViewImage(myBookImage.get(position),imageView).execute();
				    
				    return rowView;
				  }
				
	}
	 
	private void removeMyBookFromServer(int position){
		new RemoveMyBookFromServer(position).execute();
	}
	
	class RemoveMyBookFromServer extends AsyncTask<Void, Void, Boolean>{
		int pos;
		
		RemoveMyBookFromServer(int pos){
			this.pos = pos;
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			
			int book_id = myBookIds.get(pos);
			if(HomePage.myDatabaseHelper.removeMyBook(book_id)){
				Log.i("MyBooksFragment", "book removed from cache");
			}
			else{
				Log.i("MyBooksFragment", "failed to remove cached book");
			}
			
			String result;
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_REMOVEMYBOOK);
			
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		    envelope.dotNet = true;
		    envelope.setOutputSoapObject(request);
		    request.addProperty("book_id",book_id);
		    request.addProperty("login_id",HomePage.user_login_id);
		    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		    try{
		    	MyBooksFragment.this.getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(getActivity(), "Removing..", Toast.LENGTH_SHORT).show();
					}
				});
		    	
		    	androidHttpTransport.call(SOAP_ACTION_REMOVEMYBOOK, envelope);//invoking web service
		        Object response = envelope.getResponse();//get response
		        result = response.toString();
		        if(result.equals("true")){
		        	return true;
		        }
		        else{
		        	return false;
		        }
		        
		    }catch(Exception e){
		    	e.printStackTrace();
		        return false;
		
		    }
			
		}
		
		@Override
		protected void onPostExecute(Boolean status){
			if(status){
				Log.i("MyBooksFragment","resetting view");
				//myBookList.removeViewAt(pos);
				try{
					myList.remove(pos);
					myAuthorList.remove(pos);
					myBookEdition.remove(pos);
					myBookPublisher.remove(pos);
					myBookMsp.remove(pos);
					myBookImage.remove(pos);
					myBookComments.remove(pos);
					myBookIds.remove(pos);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				adapter.notifyDataSetChanged();
				Log.i("MyBooksFragment","cleared lists");
			}
			else{
				MyBooksFragment.this.getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(MyBooksFragment.this.getActivity(),"Unsuccessful, try again",Toast.LENGTH_SHORT).show();
						
					}
				});
				
			}
		}
		
		
	}
	 
	class SetListViewImage extends AsyncTask<Void, Void, Boolean>{
			
			String image;
			Bitmap bitmap;
			ImageView imageView;
			
			public SetListViewImage(String image, ImageView imageView) {
				this.image = image;
				this.imageView = imageView;
			}
			
			@Override
			protected Boolean doInBackground(Void... arg0) {
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
				}
			}
			
		}
	 
	 
	 
}
