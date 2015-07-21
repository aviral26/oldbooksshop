package com.example.oldbooksshop;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

public class SellBook extends Activity {
	
	private String user_login_id;
	AutoCompleteTextView book_title, book_author,  book_publisher, book_category;
	EditText book_msp, book_edition;
	EditText book_comments;
	RatingBar book_rating;
	ImageView book_image;
	Button proceed_button, cancel_button;
	String book_image_string;
	
	public SellBook(){
		super();
		user_login_id = HomePage.user_login_id;
	}
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_book);
        book_title = (AutoCompleteTextView) findViewById(R.id.sell_book_title1);
        book_author = (AutoCompleteTextView) findViewById(R.id.sell_author);
        book_publisher = (AutoCompleteTextView) findViewById(R.id.sell_publisher);
        book_category = (AutoCompleteTextView) findViewById(R.id.sell_category);
        book_msp = (EditText)findViewById(R.id.sell_msp);
        book_edition = (EditText)findViewById(R.id.sell_edition);
        book_comments = (EditText)findViewById(R.id.sell_book_comments);
        book_rating = (RatingBar)findViewById(R.id.sell_rating_bar);
        book_image = (ImageView)findViewById(R.id.sell_book_image);
        proceed_button = (Button)findViewById(R.id.sell_proceed_button);
        cancel_button = (Button)findViewById(R.id.sell_cancell_button);
        
        book_image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				 Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			     startActivityForResult(intent, 0);
			}
		});
        
        proceed_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(validateBookEntries() && HomePage.login_status){
					//launch upload service here
					Intent intent = new Intent(SellBook.this,UploadBookToBeSoldService.class);
					intent.putExtra("book_name", book_title.getText().toString());
					intent.putExtra("book_author", book_author.getText().toString());
					intent.putExtra("book_msp", book_msp.getText().toString());
					intent.putExtra("book_edition", book_edition.getText().toString());
					intent.putExtra("book_publisher", book_publisher.getText().toString());
					intent.putExtra("book_image", book_image_string);
					intent.putExtra("book_comments", book_comments.getText().toString());
					intent.putExtra("book_category", book_category.getText().toString());
					intent.putExtra("user_login_id",HomePage.user_login_id);
					
					Log.i("SellBook","starting upload service");
					SellBook.this.startService(intent);
					Toast.makeText(SellBook.this, "Uploading information..", Toast.LENGTH_SHORT).show();
					SellBook.this.finish();
				}
				else{
					Toast.makeText(SellBook.this, "All fields are required/Login error", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
        
        cancel_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder logoutDialogue = new AlertDialog.Builder(SellBook.this);
					logoutDialogue.setTitle("Cancel");
					logoutDialogue.setMessage("Are you sure? All changes would be lost.")
					.setCancelable(false)
					.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							SellBook.this.finish();
						}
					  })
					.setNegativeButton("Go back",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
								dialog.cancel();
						}
					});
	 				AlertDialog alertDialog = logoutDialogue.create();
	 				alertDialog.show();
			}
		});
        
	}
	
	 @Override
	   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	      super.onActivityResult(requestCode, resultCode, data);
	      Bitmap bp = (Bitmap) data.getExtras().get("data");
	      book_image.setImageBitmap(bp);
	      ByteArrayOutputStream ByteStream=new  ByteArrayOutputStream();
	      bp.compress(Bitmap.CompressFormat.JPEG,80, ByteStream);
	      byte [] b=ByteStream.toByteArray();
	      book_image_string=Base64.encodeToString(b, Base64.DEFAULT);
	      Log.i("Sell book","book_image_string set" + book_image_string);
	 }
	
	private boolean validateBookEntries(){
		if(!book_title.getText().toString().equals("")){
			if(!book_author.getText().toString().equals("")){
				if(!book_edition.getText().toString().equals("")){
					if(!book_publisher.getText().toString().equals("")){
						if(!book_msp.getText().toString().equals("")){
							if(!book_image_string.equals(null)){
							return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
}
