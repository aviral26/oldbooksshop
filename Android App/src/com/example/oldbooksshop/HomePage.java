package com.example.oldbooksshop;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.TabsPagerAdapter;

public class HomePage extends FragmentActivity implements ActionBar.TabListener {

	private static int CURRENT_TAB;
	private final String NAMESPACE = "http://tempuri.org/";
	private final String URL = "http://emf.wmyfriend.com/webservice.asmx";
	private final String SOAP_ACTION_VALIDATELOGIN = "http://tempuri.org/get_user_credentials";
	private final String METHOD_NAME_VALIDATELOGIN = "get_user_credentials";
	TextView login_text;
	
	static Menu savedMenu;
	ProgressDialog mProgress;
	Button login_button,cancel_login_button;
	PopupWindow pw;
	/*private boolean result_category_update_service;*/
	public static boolean login_status = false;
	public static String user_login_id = null;
  	private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private static String[] tabs = {"Catalogue","Books to be sold"}; 
    public static DatabaseHelper myDatabaseHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        
        
        viewPager = (ViewPager) findViewById(R.id.myViewPager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        myDatabaseHelper = new DatabaseHelper(this.getApplicationContext());
 
        
        
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
         
            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
            	CURRENT_TAB = position;
            	
            	if(position == 1){
            		Log.i("HomePage","Nothing here: " + MyBooksFragment.NOTHING_HERE);	
            		if(MyBooksFragment.setBackGroundAndLoginStatus() && !MyBooksFragment.NOTHING_HERE){
            			Intent intent = new Intent(MyBooksFragment.NOTIFICATION_F);
            			intent.setAction(MyBooksFragment.NOTIFICATION_F);
            			intent.putExtra("Result", true);
            			Log.i("HomePage","Sending broadcast to set my books");
            			sendBroadcast(intent);
            		}
            	}
                actionBar.setSelectedNavigationItem(position);
            }

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
        }); 
        
     // Adding Tabs
    	for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
    }
    
    private void checkIfUserLoggedIn(){
    	if(myDatabaseHelper.checkAnythingCached()){
    		login_status = true;
    		user_login_id = myDatabaseHelper.getFirstUser();
    		if(user_login_id.equals("ERROR")){
    			login_status = false;
    			user_login_id = null;
    			Log.i("HomePage", "UserloginId = ERROR");
    			actionBar.setSubtitle(null);
    			invalidateOptionsMenu();
    		}
    		else{
    			Log.i("HomePage", "User logged in: " + user_login_id);
    			actionBar.setSubtitle("Logged in as " + user_login_id);
    			
    			invalidateOptionsMenu();
    		}
    		
    	}
    	else{
    		login_status = false;
    		user_login_id = null;
			actionBar.setSubtitle(null);
			invalidateOptionsMenu();
    		Log.i("HomePage", "No one logged in");
    	}
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	checkIfUserLoggedIn();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      savedMenu = menu;
      MenuInflater inflater = getMenuInflater();
      if(!login_status){
    	  inflater.inflate(R.menu.main_menu, menu);
      }
      else{
    	  inflater.inflate(R.menu.main_menu_after_login, menu);
      }
      return true;
    } 
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      // action with ID action_refresh was selected
      case R.id.login_action: 
    	  					  Toast.makeText(this, "Log-in selected", Toast.LENGTH_SHORT).show();
        					  LayoutInflater mLayoutInflator = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        					  View v = mLayoutInflator.inflate(R.layout.popup_login, null, false);
        					  pw = new PopupWindow(v,600,500,true);
        					  pw.showAtLocation(this.findViewById(R.id.home_page_container), Gravity.CENTER, 0, 0);
        					  pw.setContentView(v);
        					  login_button = (Button)v.findViewById(R.id.button_login);
        					  cancel_login_button = (Button)v.findViewById(R.id.cancel_login_button);
        					  final EditText logid = (EditText)v.findViewById(R.id.editText_login_id);
        					  final EditText pwd = (EditText)v.findViewById(R.id.editText_password);
        					  try{
        						  login_button.setOnClickListener(new OnClickListener() {
        							@Override
        							public void onClick(View arg0) {
        								// Validate LOGIN here
        								new ValidateLogin(logid.getText().toString(),pwd.getText().toString()).execute();
        								
        							}
        						  });
        						  cancel_login_button.setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View arg0) {
										HomePage.this.pw.dismiss();
										Toast.makeText(HomePage.this, "Not logged in", Toast.LENGTH_SHORT).show();
									}
								});
        						  
        					  }catch(Exception e){
        						  e.printStackTrace();
        					  }
        					  break;
        					  
      case R.id.sign_up_action: //Toast.makeText(this, "Sign up selected", Toast.LENGTH_SHORT).show();
      							Intent intent4 = new Intent(HomePage.this, RegisterNewUser.class);
      							startActivity(intent4);
    	  						break;
    	  						
      case R.id.search_catalogue:	Intent i3 = new Intent(HomePage.this, SearchCatalogue.class);
    	  							startActivity(i3);
      								break;
      case R.id.search_catalogue_after_login:	Intent i4 = new Intent(HomePage.this, SearchCatalogue.class);
    	  										startActivity(i4);
      											break;
      
      case R.id.upload_action_after_login:  Toast.makeText(this, "Sell a book! selected", Toast.LENGTH_SHORT).show();
											Intent intent = new Intent(HomePage.this, SellBook.class);
											Log.i("HomePage","Starting sellbook activity");
											startActivity(intent);
      										break;
	      
      case R.id.login_action_after_login: AlertDialog.Builder logoutDialogue = new AlertDialog.Builder(HomePage.this);
      										logoutDialogue.setTitle("Log out");
      										logoutDialogue.setMessage("Are you sure?")
      										.setCancelable(false)
      										.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
      											public void onClick(DialogInterface dialog,int id) {
      												login_status = false;
      												user_login_id = null;
      												getActionBar().setSubtitle("");
      												MyBooksFragment.setBackGroundAndLoginStatus();
      												Toast.makeText(HomePage.this, "Logged out", Toast.LENGTH_SHORT).show();
      												savedMenu.clear();
      												if(myDatabaseHelper.clearUserLoginTable()){
      													Log.i("HomePage","Cleared login table");
      												}
      												if(myDatabaseHelper.clearCachedMyBooks()){
      													Log.i("HomePage","Cleared login table");
      												}
      												if(myDatabaseHelper.clearCachedBidders()){
      													Log.i("HomePage","Cleared cached bidders table");
      												}
      												//sign_up_textview.setVisibility(View.VISIBLE);
      								    			getMenuInflater().inflate(R.menu.main_menu, savedMenu);
      								    			
      											}
      										  })
      										.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
      											public void onClick(DialogInterface dialog,int id) {
           												dialog.cancel();
      											}
      										});
      						 
      										// create alert dialog
      										AlertDialog alertDialog = logoutDialogue.create();
      						 
      										// show it
      										alertDialog.show();
      										
      										break;
      
      case R.id.refresh_categories:	if(CURRENT_TAB == 0){
    	  								Toast.makeText(HomePage.this, "Refreshing categories..", Toast.LENGTH_SHORT).show();
    	  								Intent intent2 = new Intent(HomePage.this, CategoryUpdateService.class);
    	  								setRefreshActionButtonState(true);
    	  								Toast.makeText(HomePage.this, "Updating categories..", Toast.LENGTH_SHORT).show();
    	  								HomePage.this.startService(intent2);
      								}
      								if(CURRENT_TAB == 1 && login_status){
      									Toast.makeText(HomePage.this, "Refreshing books..", Toast.LENGTH_SHORT).show();
      									Intent intent2 = new Intent(HomePage.this, GetMyBooksService.class);
      									intent2.putExtra("Login_id", user_login_id);
    	  								setRefreshActionButtonState(true);
    	  								Toast.makeText(HomePage.this, "Updating..", Toast.LENGTH_SHORT).show();
    	  								HomePage.this.startService(intent2);
      								}
      								if(CURRENT_TAB == 1 && !login_status){
											Toast.makeText(HomePage.this, "Log in first", Toast.LENGTH_SHORT).show();
									}
      								break;
      case R.id.refresh_categories_after_login:if(CURRENT_TAB == 0){
    	  											Toast.makeText(HomePage.this, "Refreshing categories..", Toast.LENGTH_SHORT).show();
    	  											Intent intent3 = new Intent(HomePage.this, CategoryUpdateService.class);
    	  											setRefreshActionButtonState(true);
    	  											Toast.makeText(HomePage.this, "Updating categories..", Toast.LENGTH_SHORT).show();
    	  											HomePage.this.startService(intent3);
      											}
      											if(CURRENT_TAB == 1 && login_status){
      												Toast.makeText(HomePage.this, "Refreshing books..", Toast.LENGTH_SHORT).show();
      		      									Intent intent2 = new Intent(HomePage.this, GetMyBooksService.class);
      		      									intent2.putExtra("Login_id", user_login_id);
      		    	  								setRefreshActionButtonState(true);
      		    	  								Toast.makeText(HomePage.this, "Updating..", Toast.LENGTH_SHORT).show();
      		    	  								HomePage.this.startService(intent2);
      											}
      											if(CURRENT_TAB == 1 && !login_status){
      												Toast.makeText(HomePage.this, "Log in first", Toast.LENGTH_SHORT).show();
      											}
    	  											break;
      											
      	default:
			break;
      }


      return true;
   } 
    
    public static void setRefreshActionButtonState(final boolean refreshing) {
        try{
        	if (savedMenu != null) {
        
            
            	if (login_status == false) {
            		final MenuItem refreshItem = savedMenu
                            .findItem(R.id.refresh_categories);
                        
            		if (refreshing) {
                    	refreshItem.setActionView(R.layout.action_view_refresh);
                	} else {
                    	refreshItem.setActionView(null);
                	}
            	}
            	else{
            		final MenuItem refreshItem2 = savedMenu
                            .findItem(R.id.refresh_categories_after_login);
            		if (refreshing) {
                    	refreshItem2.setActionView(R.layout.action_view_refresh);
                	} else {
                    	refreshItem2.setActionView(null);
                	}
            	}
        	}
       	}catch(Exception e){
       		e.printStackTrace();
       	}
        
    }
    class ValidateLogin extends AsyncTask<Void, Void, Boolean>{
    	
    	String login_id, password;
    	
    	public ValidateLogin(String a, String b){
    		this.login_id = a;
    		this.password = b;
    	}
    	@Override
    	protected Boolean doInBackground(Void... arg){
    		String result=null;
    		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_VALIDATELOGIN);
    		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    		envelope.dotNet = true;
    		request.addProperty("login_id", login_id);
    		request.addProperty("password", password);
    		envelope.setOutputSoapObject(request);
    		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
    		try {
    			HomePage.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						mProgress = new ProgressDialog(HomePage.this);
						mProgress.setMessage("Validating.. ");
			    		mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			    		mProgress.setIndeterminate(true);
			    		mProgress.show();
					}
				});
	    		androidHttpTransport.call(SOAP_ACTION_VALIDATELOGIN, envelope);//invoking web service
	    		Object response = envelope.getResponse();//get response
	    		result = response.toString();
	    		Log.i("validate login", "received string: " + result);
	    		HomePage.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						mProgress.dismiss();
						
					}
				});
				
    		}catch(Exception e){
    			new Handler().post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						mProgress.dismiss();
					}
				});
    			e.printStackTrace();
    			result = "false";
    		}
    		return result.equals("valid");
    	}
    	
    	@Override
    	protected void onPostExecute(Boolean status){
    		if(status){
    			new Handler().post(new Runnable() {
					
					@Override
					public void run() {
						getActionBar().setSubtitle("Logged in as " + login_id);
						login_status = true;
		    			user_login_id = login_id;
		    			//sign_up_textview.setVisibility(View.GONE);
		    			HomePage.this.pw.dismiss();
		    			savedMenu.clear();
		    			//MyBooksFragment.setBackGroundAndLoginStatus();
		    			if(MyBooksFragment.setBackGroundAndLoginStatus()){
	            			Intent intent = new Intent(MyBooksFragment.NOTIFICATION_F);
	            			intent.setAction(MyBooksFragment.NOTIFICATION_F);
	            			intent.putExtra("Result", true);
	            			Log.i("HomePage","Sending broadcast to set my books");
	            			sendBroadcast(intent);
	            		}
		    			getMenuInflater().inflate(R.menu.main_menu_after_login, savedMenu);
					}
				});
    			//caching user here
    			if( myDatabaseHelper.addUserToDatabase(login_id, password)){
    				Log.i("HomePage", "Successfully cached user credentials");
    			}
    			
    		}
    		else{
    			new Handler().post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						HomePage.this.pw.dismiss();
						Toast.makeText(HomePage.this, "Not logged in - invalid credentials", Toast.LENGTH_SHORT).show();
					}
				});
    			
    		}
    	}
    }
    
	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {
		CURRENT_TAB = tab.getPosition();
    	if(tab.getPosition() == 1){
    		Log.i("HomePage","Nothing here: " + MyBooksFragment.NOTHING_HERE);
    		if(MyBooksFragment.setBackGroundAndLoginStatus() && !MyBooksFragment.NOTHING_HERE){
    			Intent intent = new Intent(MyBooksFragment.NOTIFICATION_F);
    			intent.setAction(MyBooksFragment.NOTIFICATION_F);
    			intent.putExtra("Result", true);
    			Log.i("HomePage","Sending broadcast to set my books");
    			sendBroadcast(intent);
    		}
    	}
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
}
