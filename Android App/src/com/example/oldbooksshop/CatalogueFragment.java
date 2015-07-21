package com.example.oldbooksshop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CatalogueFragment extends Fragment implements OnItemSelectedListener{
	
	private boolean result_category_update_service;
	private final String NAMESPACE = "http://tempuri.org/";
	private final String URL = "http://emf.wmyfriend.com/webservice.asmx";
	private final String SOAP_ACTION_GETBOOKS = "http://tempuri.org/GetBooksInfoByCategory";
	private final String METHOD_NAME_GETBOOKS = "GetBooksInfoByCategory";
	private final String METHOD_NAME_GETCATEGORIES = "GetCategories";
	private final String SOAP_ACTION_GETCATEGORIES = "http://tempuri.org/GetCategories";
	private static HashMap<String, Integer> PAGE_NUMBER_BOOKS_CATEGORY_WISE = new HashMap<String, Integer>();
	Spinner category_spinner;
	ProgressDialog mProgress;
	ListView myBookList;
	ArrayList<String> myList;
	ArrayList<String> myAuthorList,myBookEdition, myBookPublisher, myBookImage, myBookComments;
	ArrayList<Integer> myBookIds, myBookMsp;
	StableArrayAdapter adapter;
	String category_selected;
	List<String> categories; 
	Button see_more_button;
	
	  private BroadcastReceiver receiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				
				Bundle extras = intent.getExtras();
				result_category_update_service = extras.getBoolean("Result");
				Log.i("CatalogueFragment",intent.getAction().toString());
				if(intent.getAction().toString().equals(CategoryUpdateService.NOTIFICATION) && result_category_update_service){
					Log.i("CatalogueFragment", "category update service returned true");
					categories = HomePage.myDatabaseHelper.getCategories();
			 		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(CatalogueFragment.this.getActivity(),android.R.layout.simple_spinner_item, categories);
			        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			        try{
			        	category_spinner.setAdapter(dataAdapter);
			        	Log.i("CatalogueFragment","categories spinner updated");
			        	HomePage.setRefreshActionButtonState(false);
			        }catch(Exception e){
			        	e.printStackTrace();
			        	Log.i("CatalogueFragment","categories spinner not updated");
			        	HomePage.setRefreshActionButtonState(false);
			        }
			        Toast.makeText(CatalogueFragment.this.getActivity(), "Categories updated", Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(CatalogueFragment.this.getActivity(), "Error updating categories", Toast.LENGTH_SHORT).show();
					HomePage.setRefreshActionButtonState(false);
				}
			}
		};
	
	
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	 
			result_category_update_service = false;
	        View rootView = inflater.inflate(R.layout.fragment_catalogue, container, false);
	        category_spinner = (Spinner)rootView.findViewById(R.id.category_selector_spinner);
	        myBookList = (ListView)rootView.findViewById(R.id.books_listview);
	        category_spinner.setOnItemSelectedListener(this);
	        myList = new ArrayList<String>();
        	myAuthorList = new ArrayList<String>();
        	myBookIds = new ArrayList<Integer>();
        	myBookEdition = new ArrayList<String>();
        	myBookPublisher = new ArrayList<String>();
        	myBookImage = new ArrayList<String>();
        	myBookComments = new ArrayList<String>();
        	myBookMsp = new ArrayList<Integer>();
        	see_more_button = (Button)rootView.findViewById(R.id.see_more_button);
        	see_more_button.setVisibility(View.GONE);
        	
        	adapter = new StableArrayAdapter(this.getActivity(),R.layout.listview_layout, R.id.book_title_listview,myList);
        	myBookList.setAdapter(adapter);
        	categories = new ArrayList<String>();
	        
        	addCategories();
	        
        	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_spinner_item, categories);
	        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        category_spinner.setAdapter(dataAdapter);
	        
	        myBookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        		@Override
        		public void onItemClick(AdapterView<?> parent, final View view,int position, long id) {
        			
        			if(myBookIds.get(position) == -1){
        				Toast.makeText(CatalogueFragment.this.getActivity(),"Select another category", Toast.LENGTH_SHORT).show();
        			}
        			else{
        				Intent intent = new Intent(CatalogueFragment.this.getActivity(), FetchBookInfo.class);
        				intent.putExtra("Book_id", myBookIds.get(position));
        				intent.putExtra("Book_name",myList.get(position));
        				intent.putExtra("Book_author", myAuthorList.get(position));
        				intent.putExtra("Book_msp", myBookMsp.get(position));
        				intent.putExtra("Book_edition", myBookEdition.get(position));
        				intent.putExtra("Book_publisher", myBookPublisher.get(position));
        				intent.putExtra("Book_comments", myBookComments.get(position));
        				intent.putExtra("Book_image", myBookImage.get(position));
        				startActivity(intent);
        			}
        		}

        	});
	        
	        see_more_button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					int temp = PAGE_NUMBER_BOOKS_CATEGORY_WISE.get(category_selected);
					PAGE_NUMBER_BOOKS_CATEGORY_WISE.remove(category_selected);
					PAGE_NUMBER_BOOKS_CATEGORY_WISE.put(category_selected, temp+20);
					Log.i("SeeMoreButton","incrementing page no.: " + (temp+20));
					new HandleSeeMore().execute();
				}
			});
	        
	        return rootView;
	    }
		
		@Override
		public void onResume(){
			super.onResume();
			CatalogueFragment.this.getActivity().registerReceiver(receiver, new IntentFilter(CategoryUpdateService.NOTIFICATION));
		}
		
		@Override
		public void onPause() {
		    super.onPause();
		    CatalogueFragment.this.getActivity().unregisterReceiver(receiver);
		  }
	 
	 private void addCategories(){//categories still need to be refreshed periodically, create service for that
		 
		 	if(HomePage.myDatabaseHelper.checkIfCategoriesCached()){
		 		Log.i("CatalogueFragment", "found cached categories");
		 		categories = HomePage.myDatabaseHelper.getCategories();
		 		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(CatalogueFragment.this.getActivity(),android.R.layout.simple_spinner_item, categories);
		        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		        try{
		        	category_spinner.setAdapter(dataAdapter);
		        }catch(Exception e){
		        	e.printStackTrace();
		        }
		 	}	
		 	else{
		 		 Log.i("CatalogueFragment","Starting service to fetch categories");
		 		 Intent intent = new Intent(this.getActivity(), CategoryUpdateService.class);
		 		 Toast.makeText(this.getActivity(), "Updating categories..", Toast.LENGTH_SHORT).show();
		 		 this.getActivity().startService(intent);
		 	}
	 }
	 private void getValues(String category){
		
		Log.i("Value to fetch: ",category);
	    new Get_Books_By_Category(CatalogueFragment.this.getActivity(),category).execute();
		
	 }
	 
	@Override
	public void onItemSelected(AdapterView<?> parent, View arg1, int position, long arg3) {
		if(parent.getPositionForView(arg1) != 0){
			Toast.makeText(this.getActivity(), "You selected: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
			category_selected = parent.getItemAtPosition(position).toString();
			mProgress = new ProgressDialog(CatalogueFragment.this.getActivity());
			getValues(parent.getItemAtPosition(position).toString());
			
		}
		else{
			myBookList.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
		Toast.makeText(this.getActivity(), "Select a category.", Toast.LENGTH_SHORT).show();
	}
	
	public void setCatalogueCategory(){
		try{
			CatalogueFragment.this.getActivity().runOnUiThread(new Runnable() {
		
			
			@Override
			public void run() {
				
				Log.i("setCatalogueCategory", "updating in main thread");
				adapter =  new StableArrayAdapter(CatalogueFragment.this.getActivity(),R.layout.listview_layout, R.id.book_title_listview,myList);
				myBookList.setAdapter(adapter);
				myBookList.setVisibility(View.VISIBLE);
			}
		});
		}catch(Exception e){
			e.printStackTrace();
			Log.i("CatalogueFragment","error updating category spinner");
		}
		
	}
	
	class HandleSeeMore extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			String result = updateBooksByCategory(category_selected);
			if(result.equals("\\|") || result.equals("|") || result.equals("anyType{}") || result.equals(null) || result.equals("Sorry there has been an error: Object reference not set to an instance of an object.")){
				CatalogueFragment.this.getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						Toast.makeText(CatalogueFragment.this.getActivity(), "Nothing more", Toast.LENGTH_SHORT).show();
						see_more_button.setVisibility(View.GONE);
					}
				});
			}
			else{
				if(saveBooksToDatabase(result, category_selected)){
					Log.i("CatalogueFragment", "Successfully Cached downloaded books");
					final String data = HomePage.myDatabaseHelper.getBooksForCategory(category_selected);
					CatalogueFragment.this.getActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							setAdapterAndView(data);
							
						}
					});
				}
				else{
					Log.i("CatalogueFragment", "Caching unsuccessful of downloaded books");
				}
			}
			return null;
		}
		
	}
	
	class StableArrayAdapter extends ArrayAdapter<String>{
		Context context;
		
			public StableArrayAdapter(Context context, int resource,int textview_id,List objects) {
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
				Log.i("Setting layout", " fo the listView");
			    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    View rowView = inflater.inflate(R.layout.listview_layout, parent, false);
			    TextView textView_name = (TextView)rowView.findViewById(R.id.book_title_listview);
			    TextView textView_des = (TextView) rowView.findViewById(R.id.description_line);
			    ImageView imageView = (ImageView) rowView.findViewById(R.id.book_thumbnail);
			    TextView edition = (TextView)rowView.findViewById(R.id.listview_edition);
			    TextView publisher = (TextView)rowView.findViewById(R.id.listview_publisher);
			    Button cancel = (Button)rowView.findViewById(R.id.listview_remove);
			    TextView validTill = (TextView)rowView.findViewById(R.id.listview_valid_till);
			    
			    Log.i("getView", "bookName: " + myList.get(position));
			    textView_name.setText(myList.get(position));
			    Log.i("getView", "authorName: " + myAuthorList.get(position));
			    textView_des.setText(myAuthorList.get(position));
			    cancel.setVisibility(View.GONE);
			    validTill.setVisibility(View.GONE);
			    Log.i("getView", "edition: " + myBookEdition.get(position));
			    edition.setText(myBookEdition.get(position));
			    Log.i("getView", "publisher: " + myBookPublisher.get(position));
			    publisher.setText(myBookPublisher.get(position));
			    
			    new SetListViewImage(myBookImage.get(position),imageView).execute();

			    return rowView;
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
	
	class Get_Books_By_Category extends AsyncTask<String,Void,String>{

		Context context;
		String category;
		public Get_Books_By_Category(Context context,String category) {
			this.context = context;
			this.category = category;
			Log.i("Async task: ", "in constructor");
		}
		 @Override
	        protected void onPreExecute() {
	            Log.i("Get_Books_By_Category", "onPreExecute");
	            Toast.makeText(CatalogueFragment.this.getActivity(), "Fetching books..", Toast.LENGTH_SHORT).show();
	        }
		
		@Override
		protected String doInBackground(String... arg0) {
			
			Log.i("Get_Books_By_Category", "doInBackground");
            String books;
			books = getBooks(context,category);
			
			return books;
		}
		
		 @Override
	        protected void onPostExecute(String result) {
	            Log.i("Get_Books_By_Category", "onPostExecute");
	            //Toast.makeText(CatalogueFragment.this.getActivity(), "On post execute", Toast.LENGTH_SHORT).show();
	            see_more_button.setVisibility(View.VISIBLE);
	            //parsing information received
	            setAdapterAndView(result);
	            
	        }
	}
	
	
	private void setAdapterAndView(String result){
		if(result != null){
        	String[] allBooks = result.split("\\|");
        	int count=0;
        	try{
        		if(myList != null){
        			myList.clear();
        		}
        		else{
        			myList = new ArrayList<String>();
        		}
        		if(myAuthorList != null){
        			myAuthorList.clear();
        		}else{
        			myAuthorList = new ArrayList<String>();
        		}
        		if(myBookIds != null){
        			myBookIds.clear();
        		}
        		else{
        			myBookIds = new ArrayList<Integer>();
        		}
        		if(myBookPublisher != null){
        			myBookPublisher.clear();
        		}
        		else{
        			myBookPublisher = new ArrayList<String>();
        		}
        		if(myBookComments != null){
        			myBookComments.clear();
        		}
        		else{
        			myBookComments = new ArrayList<String>();
        		}
        		if(myBookEdition != null){
        			myBookEdition.clear();
        		}
        		else{
        			myBookEdition = new ArrayList<String>();
        		}
        		if(myBookImage != null){
        			myBookImage.clear();
        		}
        		else{
        			myBookImage = new ArrayList<String>();
        		}
        		if(myBookMsp != null){
        			myBookMsp.clear();
        		}
        		else{
        			myBookMsp = new ArrayList<Integer>();;
        		}
        	
        		for(int x=0;x<allBooks.length;x++){
        			String[] eachBook = allBooks[x].split("`");
        			if(allBooks.length == 1 || allBooks.length>1){
        				Log.i("GetBooksByCategory",eachBook[1] + " " + eachBook[2]);
        				myBookIds.add(Integer.parseInt(eachBook[0]));
        				myList.add(eachBook[1]);
        				myAuthorList.add(eachBook[2]);
        				myBookMsp.add((int)(Double.parseDouble(eachBook[3])));
        				myBookEdition.add(eachBook[4]);
        				myBookPublisher.add(eachBook[5]);
        				try{
        					myBookImage.add(eachBook[6]);
        				}catch(Exception e){
        					myBookImage.add("Nothing here");
        				}
        				try{
        					myBookComments.add(eachBook[7]);
        				}catch(Exception e){
        					myBookComments.add("Nothing here");
        				}
        			}
        			else{
        				myList.add("Nothing here");
        				myAuthorList.add("Move on");
        				myBookIds.add(-1);
        				Log.i("GetBooksByCategory",eachBook[1]);
        			}
        			try{
        		        	Log.i("setCatalogueCategory", "Setting adapter for list");
        		        	CatalogueFragment.this.getActivity().runOnUiThread(new Runnable() {
        						
        						@Override
        						public void run() {
        							// TODO Auto-generated method stub
        							Log.i("setCatalogueCategory", "updating in main thread");
        							adapter =  new StableArrayAdapter(CatalogueFragment.this.getActivity(),R.layout.listview_layout, R.id.book_title_listview,myList);
        							myBookList.setAdapter(adapter);
        							myBookList.setVisibility(View.VISIBLE);
        						}
        					});
        		        	
        		        	
        		        }catch(Exception e){
        		        	Log.i("set catalogue exception"," " + e.getMessage());
        		        	Toast.makeText(CatalogueFragment.this.getActivity(), "No books found", Toast.LENGTH_SHORT).show();
        		        	myBookList.setVisibility(View.GONE);
        		        }
        				
        		}
        	//	adapter.notifyDataSetChanged();
        	}catch(Exception e){
        		e.printStackTrace();
        		Toast.makeText(CatalogueFragment.this.getActivity(), "Nothing received", Toast.LENGTH_SHORT).show();
        		myList = null;
        		 CatalogueFragment.this.getActivity().runOnUiThread(new Runnable() {
     				
     				@Override
     				public void run() {
     				mProgress.dismiss();
     					
     				}
     			});
        		 myBookList.setVisibility(View.GONE);
        		myAuthorList = null;
        	}
        	
        }
        else{
        	Toast.makeText(CatalogueFragment.this.getActivity(), "Nothing received", Toast.LENGTH_SHORT).show();
        	myBookList.setVisibility(View.GONE);
        }
	}
	
	private String getBooks(Context context,String category){
		
		String result;
		
		if(HomePage.myDatabaseHelper.checkIfBooksCachedForCategory(category)){
			result = HomePage.myDatabaseHelper.getBooksForCategory(category);
			Log.i("CatalogueFragment", "Found cached books for category: "  + category);
			
			return result;
		}
		else{
			Log.i("CatalogueFragment", "downloading books by category");
			result = updateBooksByCategory(category);
			if(saveBooksToDatabase(result,category)){
				Log.i("CatalogueFragment", "downloaded books successfully cached");
			}
			else{
				Log.i("CatalogueFragment", "failed to cache downloaded books");
			}
			return result;
		}
	}
	
	private boolean saveBooksToDatabase(String result, String category){
		try{
			if(result.equals(null) || result.equals("|")){
				return false;
			}
			String[] books = result.split("\\|");
			for(int i=0;i<books.length;i++){
				String[] book = books[i].split("`");
				if(book[6].equals(null) || book[6].equals("") || book[6].equals(" ")){
					Log.i("Catalogue fragment: "," no image for book. manually adding");
					book[6].concat("nothing here");
				}
				Log.i("CatalogueFragment","book[6]" + book[6]);
				try{
					HomePage.myDatabaseHelper.cacheBooksForCategory(book[0], book[1], book[2], book[3], book[4], book[5], book[6], category, book[7]);
				}catch(Exception e){
					Log.i("CatalogueFragment: ","Trying to cache comments manually");
					HomePage.myDatabaseHelper.cacheBooksForCategory(book[0], book[1], book[2], book[3], book[4], book[5], book[6], category, "nothing here");
				}
			}
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	private String updateBooksByCategory(String category){	
		
		String result;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_GETBOOKS);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    envelope.dotNet = true;
	    envelope.setOutputSoapObject(request);
	    request.addProperty("category", category);
	    if(PAGE_NUMBER_BOOKS_CATEGORY_WISE.containsKey(category)){
	    	int temp = PAGE_NUMBER_BOOKS_CATEGORY_WISE.get(category);
	    	Log.i("CatalogueFragment","sending page number: " + temp);
	    	request.addProperty("page",temp);
	    	category_selected = category;
			PAGE_NUMBER_BOOKS_CATEGORY_WISE.put(category_selected, temp);
	    }
	    else{
	    	int temp=0;
	    	Log.i("CatalogueFragment","sending page number: " + temp);
	    	request.addProperty("page",temp);
	    	category_selected = category;
			PAGE_NUMBER_BOOKS_CATEGORY_WISE.put(category_selected, temp);
	    }
	    Log.i("getBooks", "category: " + category);
	    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
	    try {
	    	CatalogueFragment.this.getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
				
					mProgress.setMessage("Loading books..");
		    		mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    		mProgress.setIndeterminate(true);
		    		mProgress.show();
				}
			});
	    	
	        androidHttpTransport.call(SOAP_ACTION_GETBOOKS, envelope);//invoking web service
	        Object response = envelope.getResponse();//get response
	        result = response.toString();
	        CatalogueFragment.this.getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
				mProgress.dismiss();
					
				}
			});
	        if(result.equals(null)){
	        	Log.i("Get_Books_By_Category","nothing received");
	        }
	        else{
	        	Log.i("Result: ",result);
	        }
	        return result;
	    } catch (Exception e) {
	        e.printStackTrace();
	        CatalogueFragment.this.getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
				mProgress.dismiss();
					
				}
			});
	        return null;
	    }
	}
	
}
