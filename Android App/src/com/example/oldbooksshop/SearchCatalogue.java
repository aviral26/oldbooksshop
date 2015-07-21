package com.example.oldbooksshop;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchCatalogue extends Activity{
	static int PAGE_NUMBER_SEARCH_RESULT=0;
	private final String NAMESPACE = "http://tempuri.org/";
	private final String URL = "http://emf.wmyfriend.com/webservice.asmx";
	private final String SOAP_ACTION_GETSEARCHRESULT = "http://tempuri.org/GetSearchResult";
	private final String METHOD_NAME_GETSEARCHRESULT = "GetSearchResult";
	ProgressDialog mProgress;
	Button search, next_page;
	EditText keyword;
	ListView search_result;
	ArrayList<String> myList;
	ArrayList<String> myAuthorList,myBookEdition, myBookPublisher, myBookImage, myBookComments;
	ArrayList<Integer> myBookIds, myBookMsp;
	StableArrayAdapter2 adapter;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_catalogue);
        search = (Button)findViewById(R.id.searchcatalogue_button1);
        keyword = (EditText)findViewById(R.id.searchcatalogue_keyword);
        search_result = (ListView)findViewById(R.id.searchcatalogue_listview);
        next_page = (Button)findViewById(R.id.searchcatalogue_see_more);
        next_page.setVisibility(View.GONE);
        search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(!keyword.getText().toString().equals("")){
					Log.i("SearchCatalogue","launching async");
					PAGE_NUMBER_SEARCH_RESULT = 0;
					new FetchSearchResults().execute();
				}
				else{
					Toast.makeText(SearchCatalogue.this, "Enter a keyword", Toast.LENGTH_SHORT).show();
				}
			}
		});
        
        next_page.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(!keyword.getText().toString().equals("")){
					Log.i("SearchCatalogue","launching async");
					PAGE_NUMBER_SEARCH_RESULT = PAGE_NUMBER_SEARCH_RESULT+20;
					Log.i("SearchCatalogue","Page: " + PAGE_NUMBER_SEARCH_RESULT );
					new FetchSearchResults().execute();
				}
				else{
					Toast.makeText(SearchCatalogue.this, "Enter a keyword", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
        
        search_result.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Log.i("SearchCatalogue","item clicked");
				if(myBookIds.get(position) == -1){
    				Toast.makeText(SearchCatalogue.this,"Select another category", Toast.LENGTH_SHORT).show();
    			}
    			else{
    				
    				Intent intent = new Intent(SearchCatalogue.this, FetchBookInfo.class);
    				intent.putExtra("Book_id", myBookIds.get(position));
    				Log.i("SearchCatalogue","Sending book id: " + myBookIds.get(position));
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
        
	}
	
	class FetchSearchResults extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... arg0) {
			
			String result;
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_GETSEARCHRESULT);
			
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		    envelope.dotNet = true;
		    envelope.setOutputSoapObject(request);
		    request.addProperty("keyword", keyword.getText().toString());
		    request.addProperty("page",PAGE_NUMBER_SEARCH_RESULT);
		    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		    try{
		    	SearchCatalogue.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						mProgress = new ProgressDialog(SearchCatalogue.this);
						mProgress.setMessage("Loading books..");
			    		mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			    		mProgress.setIndeterminate(true);
			    		mProgress.show();
					}
				});
		    	
		    	androidHttpTransport.call(SOAP_ACTION_GETSEARCHRESULT, envelope);//invoking web service
		        Object response = envelope.getResponse();//get response
		        result = response.toString();
		        SearchCatalogue.this.runOnUiThread(new Runnable() {
					
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
		    }catch(Exception e){
		    	e.printStackTrace();
		        SearchCatalogue.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
					mProgress.dismiss();
						
					}
				});
		        return null;
		
		    }
		   
		}
		
		 @Override
	        protected void onPostExecute(String result) {
	            Log.i("SearchCatalogue", "onPostExecute");
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
        				Log.i("SearchCatalogue",eachBook[1] + " " + eachBook[2]);
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
        				Log.i("SearchCatalogue",eachBook[1]);
        			}
        			try{
        		        	Log.i("setSearchResultsCatalogue", "Setting adapter for list");
        		        	SearchCatalogue.this.runOnUiThread(new Runnable() {
        						
        						@Override
        						public void run() {
        							// TODO Auto-generated method stub
        							Log.i("setCatalogueCategory", "updating in main thread");
        							adapter =  new StableArrayAdapter2(SearchCatalogue.this,R.layout.listview_layout, R.id.book_title_listview,myList);
        							search_result.setAdapter(adapter);
        							search_result.setVisibility(View.VISIBLE);
        							next_page.setVisibility(View.VISIBLE);
        						}
        					});
        		        	
        		        	
        		        }catch(Exception e){
        		        	Log.i("set search result exception"," " + e.getMessage());
        		        	//Toast.makeText(SearchCatalogue.this, "No books found", Toast.LENGTH_SHORT).show();
        		        	search_result.setVisibility(View.GONE);
        		        	next_page.setVisibility(View.GONE);
        		        }
        				
        		}
        	//	adapter.notifyDataSetChanged();
        	}catch(Exception e){
        		e.printStackTrace();
        		Toast.makeText(SearchCatalogue.this, "Nothing received", Toast.LENGTH_SHORT).show();
        		myList = null;
        		 SearchCatalogue.this.runOnUiThread(new Runnable() {
     				
     				@Override
     				public void run() {
     				mProgress.dismiss();
     					
     				}
     			});
        		 search_result.setVisibility(View.GONE);
        		 next_page.setVisibility(View.GONE);
        		myAuthorList = null;
        	}
        	
        }
        else{
        	Toast.makeText(SearchCatalogue.this, "Nothing received", Toast.LENGTH_SHORT).show();
        	search_result.setVisibility(View.GONE);
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


}
